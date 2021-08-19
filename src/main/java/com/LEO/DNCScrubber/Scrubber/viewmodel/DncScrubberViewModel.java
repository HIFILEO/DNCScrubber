package com.LEO.DNCScrubber.Scrubber.viewmodel;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileController;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.interactor.ScrubberInteractor;
import com.LEO.DNCScrubber.Scrubber.model.CommandType;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.model.action.*;
import com.LEO.DNCScrubber.Scrubber.model.event.*;
import com.LEO.DNCScrubber.Scrubber.model.result.*;
import com.LEO.DNCScrubber.Scrubber.model.uiModel.UiModel;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DncScrubberViewModel {
    final static Logger logger = LoggerFactory.getLogger(DncScrubberViewModel.class);
    private final ScreenData screenData;
    private final ScrubberInteractor scrubberInteractor;
    private final PublishRelay<UiEvent> publishRelayUiEvents = PublishRelay.create();

    private Observable<UiModel> uiModelObservable;
    private ObservableTransformer<UiEvent, Action> transformEventsIntoActions;

    /**
     * Constructor. Members are injected.
     */
    @Inject
    public DncScrubberViewModel(ScreenData screenData, CsvFileController csvFileController,
                                DatabaseGateway databaseGateway) {
        this.screenData = screenData;
        this.scrubberInteractor = new ScrubberInteractor(csvFileController, databaseGateway);
        setupTransformers();
        bind();
    }

    @NonNull
    public Observable<UiModel> getUiModels() {
        return uiModelObservable;
    }

    /**
     * Process events from the UI.
     * @param uiEvent - {@link UiEvent}
     */
    public void processUiEvent(UiEvent uiEvent) {
        //
        //Guard
        //
        if (uiModelObservable == null) {
            throw new IllegalStateException("Model Observer not ready. Did you forget to call init()?");
        }

        //
        //Process UiEvent
        //
        publishRelayUiEvents.accept(uiEvent);
    }

    /**
     * Bind to {@link PublishRelay}.
     */
    protected void bind() {
        uiModelObservable = publishRelayUiEvents
                .observeOn(Schedulers.computation())
                //Translate UiEvents into Actions
                .compose(transformEventsIntoActions)
                //Asynchronous Actions To Interactor
                .publish(scrubberInteractor::processAction)
                //Scan Results to Update UiModel
                .scan(UiModel.init(screenData.getMainCommands()), (uiModel, result) -> {
                    //logger.debug("Scan results: Thread name {}", Thread.currentThread());
                    if (result instanceof ExitResult) {
                        return UiModel.exit();
                    } else if (result instanceof LoadRawLeadsResult) {
                        return processLoadRawLeadsResult(uiModel, (LoadRawLeadsResult) result);
                    } else if (result instanceof NoSelectionResult) {
                        return processNoSelectionResult(uiModel, (NoSelectionResult) result);
                    } else if (result instanceof LoadFileDialogResult) {
                        return processLoadFileDialogResult(uiModel, (LoadFileDialogResult) result);
                    }

                    //Unknown result - throw error
                    throw new IllegalArgumentException("Unknown Result: " + result);
                });
                //Save history for late subscribers - for restoring state
                //.replay(1)
                //.autoConnect();
    }

    /**
     * Setup the transformers used by this
     */
    private void setupTransformers() {
        final ObservableTransformer<ExitEvent, ExitAction> transformExit =
                upstream -> upstream.flatMap(new Function<ExitEvent, ObservableSource<ExitAction>>() {
                    @Override
                    public ObservableSource<ExitAction> apply(ExitEvent exitEvent) throws Exception {
                        return Observable.just(new ExitAction());
                    }
                });

        final ObservableTransformer<LoadRawLeadsEvent, LoadRawLeadsAction> transformLoadRawLeads =
                upstream -> upstream.flatMap((Function<LoadRawLeadsEvent, ObservableSource<LoadRawLeadsAction>>)
                        this::transformLoadRawLeads);

        final ObservableTransformer<NoSelectionEvent, NoSelectionAction> transformNoSelection =
                upstream -> upstream.flatMap(noSelectionEvent -> Observable.just(new NoSelectionAction()));

        final ObservableTransformer<LoadFileDialogEvent, LoadFileDialogAction> transformLoadFileDialog =
                upstream -> upstream.flatMap((Function<LoadFileDialogEvent, ObservableSource<LoadFileDialogAction>>)
                        this::transformLoadFileDialog);

        transformEventsIntoActions = upstream -> upstream.publish(uiEventObservable ->
                Observable.merge(
                        uiEventObservable.ofType(LoadRawLeadsEvent.class).compose(transformLoadRawLeads),
                        uiEventObservable.ofType(ExitEvent.class).compose(transformExit),
                        uiEventObservable.ofType(NoSelectionEvent.class).compose(transformNoSelection),
                        uiEventObservable.ofType(LoadFileDialogEvent.class).compose(transformLoadFileDialog)
                )
        );
    }

    private UiModel processNoSelectionResult(UiModel uiModel, NoSelectionResult noSelectionResult) {
        UiModel.UiModelBuilder uiModelBuilder = new UiModel.UiModelBuilder(uiModel);
        uiModelBuilder.setScreenMessage(screenData.getNoSelectionMade());

        return uiModelBuilder.createUiModel();
    }

    private Observable<LoadRawLeadsAction> transformLoadRawLeads(LoadRawLeadsEvent loadRawLeadsEvent) {
        return Observable.just(
                new LoadRawLeadsAction(loadRawLeadsEvent.getCsvFile())
        );
    }

    private Observable<LoadFileDialogAction> transformLoadFileDialog(LoadFileDialogEvent loadFileDialogEvent) {
        return Observable.just(
                new LoadFileDialogAction(loadFileDialogEvent.getCommandType(),
                        loadFileDialogEvent.isUserCanceled(),
                        loadFileDialogEvent.isFileLoadError(),
                        loadFileDialogEvent.getErrorMessage())
        );
    }

    private UiModel processLoadRawLeadsResult(UiModel uiModel, LoadRawLeadsResult loadRawLeadsResult) {
        UiModel.UiModelBuilder uiModelBuilder = new UiModel.UiModelBuilder(uiModel);

        switch (loadRawLeadsResult.getType()) {
            case Result.ResultType.IN_FLIGHT:
                uiModelBuilder.setScreenMessage(CommandType.LOAD_RAW_LEADS + " " + screenData.getInProgress());
                uiModelBuilder.setInFlight(true);
                uiModelBuilder.setShowLoadFileDialog(false);
                uiModelBuilder.setCommand(CommandType.LOAD_RAW_LEADS);
                break;
            case Result.ResultType.FAILURE:
                uiModelBuilder.setCommand(CommandType.NONE);
                uiModelBuilder.setShowLoadFileDialog(false);
                uiModelBuilder.setScreenMessage(CommandType.LOAD_RAW_LEADS + " " +
                        screenData.getError() + "\n" +
                        "Error Msg: " + loadRawLeadsResult.getErrorMessage() + "\n\n" +
                        screenData.getMainCommands());
                uiModelBuilder.setInFlight(false);
                break;
            case Result.ResultType.SUCCESS:
                uiModelBuilder.setShowLoadFileDialog(false);
                uiModelBuilder.setCommand(CommandType.NONE);
                uiModelBuilder.setScreenMessage(CommandType.LOAD_RAW_LEADS + " " +
                        screenData.getSuccess() + "\n\n" + screenData.getMainCommands());
                uiModelBuilder.setInFlight(false);
                break;
            default:
                //Unknown result - throw error
                throw new IllegalArgumentException("Unknown ResultType: " + loadRawLeadsResult.getType());
        }

        return uiModelBuilder.createUiModel();
    }

    private UiModel processLoadFileDialogResult(UiModel uiModel, LoadFileDialogResult loadFileDialogResult) {
        UiModel.UiModelBuilder uiModelBuilder = new UiModel.UiModelBuilder(uiModel);

        switch (loadFileDialogResult.getType()) {
            case Result.ResultType.FAILURE:
                uiModelBuilder.setCommand(CommandType.NONE);
                uiModelBuilder.setShowLoadFileDialog(false);

                if (loadFileDialogResult.isUserCanceled()) {
                    uiModelBuilder.setScreenMessage(CommandType.LOAD_FILE_DIALOG + " - " +
                            screenData.getDialogCancel() + "\n\n" +
                            screenData.getMainCommands());

                } else if (loadFileDialogResult.isFileLoadError()) {
                    uiModelBuilder.setScreenMessage(CommandType.LOAD_FILE_DIALOG + " -  " +
                            screenData.getError() + "\n" +
                            "Error Msg: " + loadFileDialogResult.getErrorMessage()+ "\n\n" +
                            screenData.getMainCommands());
                }

                uiModelBuilder.setInFlight(false);
                break;
            case Result.ResultType.SUCCESS:
                uiModelBuilder.setShowLoadFileDialog(true);
                uiModelBuilder.setCommand(loadFileDialogResult.getCommandType());
                uiModelBuilder.setScreenMessage(CommandType.LOAD_FILE_DIALOG + " - " +
                        screenData.getSuccess() + "\n\n" + screenData.getMainCommands());
                uiModelBuilder.setInFlight(false);
                break;
            case Result.ResultType.IN_FLIGHT:
            default:
                //Unknown result - throw error
                throw new IllegalArgumentException("Unknown ResultType: " + loadFileDialogResult.getType());
        }

        return uiModelBuilder.createUiModel();
    }
}
