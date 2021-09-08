package com.LEO.DNCScrubber.Scrubber.viewconntroller;

import com.LEO.DNCScrubber.Scrubber.model.CommandType;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.model.event.*;
import com.LEO.DNCScrubber.Scrubber.model.uiModel.UiModel;
import com.LEO.DNCScrubber.Scrubber.view.DncScrubberMainView;
import com.LEO.DNCScrubber.Scrubber.view.FileChooserView;
import com.LEO.DNCScrubber.Scrubber.view.FileSaverView;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.*;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DncScrubberViewController {
    final static Logger logger = LoggerFactory.getLogger(DncScrubberViewController.class);

    private final DncScrubberMainView dncScrubberMainView = new DncScrubberMainView();
    private final ScreenData screenData;
    private final Scheduler uiScheduler;

    private final PublishSubject<String> mainThreadSubject = PublishSubject.create();
    private final DncScrubberViewModel dncScrubberViewModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final ExecutorService uiExecutorService;

    public DncScrubberViewController(DncScrubberViewModel dncScrubberViewModel, ScreenData screenData) {
        uiExecutorService = Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("UI-Thread");
                    return thread;
                });

        uiScheduler = Schedulers.from(uiExecutorService);
        this.screenData = screenData;
        this.dncScrubberViewModel = dncScrubberViewModel;
    }

    /**
     * Run the UI
     * @return Observable<String> with updates from UI thread. Returns EXIT to terminate program</String>
     */
    public Observable<String> runUI() {
        createUi();
        bind();

        /*
        Note - since you don't have access to java main thread as a RX Scheduler, it's best to run
        the UI in another dedicated thread. Android does this behind the scenes and gives developers
        the UI thread which has also been dictated as MAIN thread since this UI thread is used
        to pain the UI and will slow things down when not left mostly idle.

        So for this program, runUI will kick off a dedicated UI thread which I wrap into a scheduler for all UI updates.
         */
        return mainThreadSubject
                .observeOn(uiScheduler)
                .doOnDispose(this::cleanUpAndShutdown)
                .doOnComplete(this::cleanUpAndShutdown);
    }

    /**
     * Clean up and shutdown the UI.
     */
    private void cleanUpAndShutdown() {
        logger.info("cleanUpAndShutdown UI - thread " + Thread.currentThread().getName());
        compositeDisposable.dispose();

        uiScheduler.shutdown();

        //Note - as per design, you need to shut down the executor when creating a custom scheduler.
        //https://github.com/ReactiveX/RxJava/issues/6027
        uiExecutorService.shutdown();

        //Note - Good practice to wait.
        try {
            if (!uiExecutorService.awaitTermination(1500, TimeUnit.MILLISECONDS)) {
                logger.warn("uiExecutorService didn't terminate gracefully. Timeout occurred");
            }
        } catch (InterruptedException e) {
            logger.warn("Main thread interrupted while waiting for threads to close:[]", e);
        }

        logger.info("cleanUpAndShutdown() completed " + Thread.currentThread().getName());
    }

    /**
     * Bind to all data in
     */
    private void bind() {
        //
        //Bind Window - When window closes, kill program.
        //
        dncScrubberMainView.frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                dncScrubberViewModel.processUiEvent(new ExitEvent());
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        //
        //Bind Button
        //
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<ActionEvent>() {

            @Override
            public void subscribe(ObservableEmitter<ActionEvent> emitter) throws Exception {
                dncScrubberMainView.executeCommandButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        emitter.onNext(actionEvent);
                    }
                });
            }
        })
            .debounce(250, TimeUnit.MILLISECONDS)
            .subscribeOn(uiScheduler)
            .subscribe(new Consumer<ActionEvent>() {
                @Override
                public void accept(ActionEvent actionEvent) throws Exception {
                    //Switch off ScreenInfo (File)
                    switch (dncScrubberMainView.commandList.getSelectedIndex()) {
                        //Nothing Selected
                        case -1:
                            dncScrubberViewModel.processUiEvent(new NoSelectionEvent());
                            break;
                        //Load Raw Leads
                        case 0:
                            dncScrubberViewModel.processUiEvent(
                                    new LoadFileDialogEvent(CommandType.LOAD_RAW_LEADS,
                                            false, false, ""));
                            break;
                        //Save Raw Leads To Skip Trace
                        case 1:
                            dncScrubberViewModel.processUiEvent(
                                    new SaveFileDialogEvent(CommandType.SAVE_LEADS_TO_SKIP_TRACE,
                                            false, false, ""));
                            break;
                        default:
                            throw new Exception("Button not bound to correct action. Throwing error.");
                    }
                }
            }));

        //
        //Bind to UiModel
        //
        compositeDisposable.add(dncScrubberViewModel.getUiModels()
            .subscribeOn(uiScheduler)
            .subscribe(this::processUiModel,
                    throwable -> {
                        throw new UnsupportedOperationException("Errors from Model Unsupported: "
                                + throwable.getLocalizedMessage());
                    })
        );
    }

    private void createUi() {
        //Call the JFrame setup in the auto generated code.
        dncScrubberMainView.createUi();

        //Since these values are static, setting them here and no in the UIModel
        dncScrubberMainView.commandList.setListData(screenData.getCommands());
    }

    /**
     * Bind to {@link com.LEO.DNCScrubber.Scrubber.model.uiModel.UiModel}.
     * @param uiModel - the {@link UiModel} from {@link DncScrubberViewModel} that backs the UI.
     */
    private void processUiModel(UiModel uiModel) {
        /*
        Note - Keep the logic here as SIMPLE as possible.
         */

        //
        //Update Screen
        //
        //exit
        if (uiModel.isExit()) {
            mainThreadSubject.onNext("exit");
        }

        //Screen Text
        if (uiModel.getScreenMessage() != null && !uiModel.getScreenMessage().isEmpty()) {
            dncScrubberMainView.outputTextArea.setText(uiModel.getScreenMessage());
        }

        //Progress Bar
        if (uiModel.isInFlight()) {
            dncScrubberMainView.inProgressCardPanel.setVisible(false);
            dncScrubberMainView.inProgressLabel.setVisible(true);
            dncScrubberMainView.executeCommandButton.setEnabled(false);
        } else {
            dncScrubberMainView.inProgressCardPanel.setVisible(true);
            dncScrubberMainView.inProgressLabel.setVisible(false);
            dncScrubberMainView.executeCommandButton.setEnabled(true);
        }

        //Load File Dialog
        if (uiModel.isShowLoadFileDialog()) {
            Single.create(new SingleOnSubscribe<UiEvent>() {
                @Override
                public void subscribe(SingleEmitter<UiEvent> emitter) throws Exception {
                    FileChooserView fileChooserView = new FileChooserView();
                    fileChooserView.showChooser();

                    File file = fileChooserView.getFile();
                    if (file != null) {
                        switch (uiModel.getCommand()) {
                            case LOAD_RAW_LEADS:
                                emitter.onSuccess(new LoadRawLeadsEvent(file));
                                break;
                            case EXIT:
                            case LOAD_FILE_DIALOG:
                            default:
                                throw new Exception("Wrong command Type! Should never happen!");
                        }
                    } else {
                        emitter.onSuccess(new LoadFileDialogEvent
                                (CommandType.NONE,
                                        fileChooserView.isUserCanceled(),
                                        fileChooserView.isFileLoadError(),
                                        fileChooserView.getErrorMessage()));
                    }
                }
            }).subscribe(new Consumer<UiEvent>() {
                @Override
                public void accept(UiEvent uiEvent) throws Exception {
                    dncScrubberViewModel.processUiEvent(uiEvent);
                }
            });
        }

        //Save File Dialog
        if (uiModel.isShowSaveFileDialog()) {
            Single.create(new SingleOnSubscribe<UiEvent>() {
                @Override
                public void subscribe(SingleEmitter<UiEvent> emitter) throws Exception {
                    FileSaverView fileSaverView = new FileSaverView();
                    fileSaverView.showSavDialog();

                    File file = fileSaverView.getFile();
                    if (file != null) {
                        switch (uiModel.getCommand()) {
                            case SAVE_LEADS_TO_SKIP_TRACE:
                                emitter.onSuccess(new ExportLeadsToSkipTraceEvent(file));
                                break;
                            default:
                                throw new Exception("Wrong command Type! Should never happen!");
                        }
                    } else {
                        emitter.onSuccess(new SaveFileDialogEvent
                                (CommandType.NONE,
                                        fileSaverView.isUserCanceled(),
                                        fileSaverView.isFileLoadError(),
                                        fileSaverView.getErrorMessage()));
                    }
                }
            }).subscribe(new Consumer<UiEvent>() {
                @Override
                public void accept(UiEvent uiEvent) throws Exception {
                    dncScrubberViewModel.processUiEvent(uiEvent);
                }
            });
        }
    }


}
