package com.LEO.DNCScrubber.Scrubber.viewmodel;

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.interactor.ScrubberInteractor;
import com.LEO.DNCScrubber.Scrubber.model.CommandType;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.model.action.Action;
import com.LEO.DNCScrubber.Scrubber.model.action.ExitAction;
import com.LEO.DNCScrubber.Scrubber.model.action.LoadRawLeadsAction;
import com.LEO.DNCScrubber.Scrubber.model.action.NoSelectionAction;
import com.LEO.DNCScrubber.Scrubber.model.event.ExitEvent;
import com.LEO.DNCScrubber.Scrubber.model.event.LoadRawLeadsEvent;
import com.LEO.DNCScrubber.Scrubber.model.event.NoSelectionEvent;
import com.LEO.DNCScrubber.Scrubber.model.event.UiEvent;
import com.LEO.DNCScrubber.Scrubber.model.result.ExitResult;
import com.LEO.DNCScrubber.Scrubber.model.result.LoadRawLeadsResult;
import com.LEO.DNCScrubber.Scrubber.model.result.NoSelectionResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import com.LEO.DNCScrubber.Scrubber.model.uiModel.UiModel;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
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

    private Observable<UiModel> uiModelObservable;
    private Observable<UiEvent> startEventsObservable;
    private ObservableTransformer<UiEvent, Action> transformEventsIntoActions;
    private PublishRelay<UiEvent> publishRelayUiEvents = PublishRelay.create();


    /**
     * Constructor. Members are injected.
     */
    @Inject
    public DncScrubberViewModel(ScreenData screenData, CsvFileReader csvFileReader) {
        this.screenData = screenData;
        this.scrubberInteractor = new ScrubberInteractor(csvFileReader);
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
                    //Timber.i("Thread name: %s. Scan Results to UiModel", Thread.currentThread().getName());
                    //logger.debug("Scan results: Thread name {}", Thread.currentThread());
                    if (result instanceof ExitResult) {
                        return UiModel.exit();
                    } else if (result instanceof LoadRawLeadsResult) {
                        return processLoadRawLeadsResult(uiModel, (LoadRawLeadsResult) result);
                    } else if (result instanceof NoSelectionResult) {
                        return processNoSelectionResult(uiModel, (NoSelectionResult) result);
                    }

                    //Unknown result - throw error
                    throw new IllegalArgumentException("Unknown Result: " + result);
                });
                //Save history for late subscribers.
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
                        loadRawLeadsEvent -> Observable.just(new LoadRawLeadsAction(loadRawLeadsEvent.getFileName())));

        final ObservableTransformer<NoSelectionEvent, NoSelectionAction> transformNoSelection =
                upstream -> upstream.flatMap(noSelectionEvent -> Observable.just(new NoSelectionAction()));

        transformEventsIntoActions = upstream -> upstream.publish(uiEventObservable ->
                Observable.merge(
                        uiEventObservable.ofType(LoadRawLeadsEvent.class).compose(transformLoadRawLeads),
                        uiEventObservable.ofType(ExitEvent.class).compose(transformExit),
                        uiEventObservable.ofType(NoSelectionEvent.class).compose(transformNoSelection)
                )
        );
    }

    private UiModel processLoadRawLeadsResult(UiModel uiModel, LoadRawLeadsResult loadRawLeadsResult) {
        UiModel.UiModelBuilder uiModelBuilder = new UiModel.UiModelBuilder(uiModel);

        switch (loadRawLeadsResult.getType()) {
            case Result.ResultType.IN_FLIGHT:
                uiModelBuilder.setScreenMessage("");
                uiModelBuilder.setInFlight(true);
                uiModelBuilder.setPreviousCommand(CommandType.LOAD_RAW_LEADS);
                break;
            case Result.ResultType.FAILURE:
                uiModelBuilder.setPreviousCommand(CommandType.NONE);
                uiModelBuilder.setScreenMessage("\n" + CommandType.LOAD_RAW_LEADS + " " +
                        screenData.getError() + "\n" +
                        "Error Msg: " + loadRawLeadsResult.getErrorMessage() + "\n\n" +
                        screenData.getMainCommands());
                break;
            case Result.ResultType.SUCCESS:
                uiModelBuilder.setPreviousCommand(CommandType.NONE);
                uiModelBuilder.setScreenMessage("\n" + CommandType.LOAD_RAW_LEADS + " " +
                        screenData.getSuccess() + "\n\n" + screenData.getMainCommands());
                break;
            default:
                //Unknown result - throw error
                throw new IllegalArgumentException("Unknown ResultType: " + loadRawLeadsResult.getType());
        }

        return uiModelBuilder.createUiModel();
    }

    public UiModel processNoSelectionResult(UiModel uiModel, NoSelectionResult noSelectionResult) {
        UiModel.UiModelBuilder uiModelBuilder = new UiModel.UiModelBuilder(uiModel);

        uiModelBuilder.setScreenMessage(screenData.getNoSelectionMade());

        return uiModelBuilder.createUiModel();
    }
}
