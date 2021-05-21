package com.LEO.DNCScrubber.Scrubber.viewconntroller;

import com.LEO.DNCScrubber.Scrubber.model.CommandType;
import com.LEO.DNCScrubber.Scrubber.model.ScreenData;
import com.LEO.DNCScrubber.Scrubber.model.event.ExitEvent;
import com.LEO.DNCScrubber.Scrubber.model.event.LoadRawLeadsEvent;
import com.LEO.DNCScrubber.Scrubber.model.event.NoSelectionEvent;
import com.LEO.DNCScrubber.Scrubber.model.uiModel.UiModel;
import com.LEO.DNCScrubber.Scrubber.view.DncScrubberMainView;
import com.LEO.DNCScrubber.Scrubber.viewmodel.DncScrubberViewModel;
import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DncScrubberViewController {
    final static Logger logger = LoggerFactory.getLogger(DncScrubberViewController.class);

    private final DncScrubberMainView dncScrubberMainView = new DncScrubberMainView();
    private final ScreenData screenData;
    private final Scheduler uiScheduler;
    private final Scheduler keyboardScheduler;

    private final PublishSubject<String> mainThreadSubject = PublishSubject.create();
    private final PublishSubject<String> printToScreenSubject = PublishSubject.create();
    private final PublishSubject<String> keyboardSubject = PublishSubject.create();

    private final DncScrubberViewModel dncScrubberViewModel;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable keyboardCommandsDisposable;
    private Disposable keyboardFileNameDisposable;
    private Disposable processingDisposable;

    private final ExecutorService uiExecutorService;
    private final ExecutorService keyboardExecutorService;

    public DncScrubberViewController(DncScrubberViewModel dncScrubberViewModel, ScreenData screenData) {
        uiExecutorService = Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("UI-Thread");
                    return thread;
                });

        keyboardExecutorService = Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("Keyboard-IO-Thread");
                    return thread;
                });

        uiScheduler = Schedulers.from(uiExecutorService);
        keyboardScheduler = Schedulers.from(keyboardExecutorService);
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
        keyboardScheduler.shutdown();

        //Note - as per design, you need to shut down the executor when creating a custom scheduler.
        //https://github.com/ReactiveX/RxJava/issues/6027
        uiExecutorService.shutdown();

        //Note - you need to interrupt the thread in the observer since it's predicated on a sleep for
        //busy waiting on keyboard input.
        keyboardExecutorService.shutdownNow();

        //Note - Good practice to wait.
        try {
            if (!uiExecutorService.awaitTermination(1500, TimeUnit.MILLISECONDS)) {
                logger.warn("uiExecutorService didn't terminate gracefully. Timeout occurred");
            }
            if (keyboardExecutorService.awaitTermination(1500, TimeUnit.MILLISECONDS)) {
                logger.warn("keyboardExecutorService didn't terminate gracefully. Timeout occurred");
            }
        } catch (InterruptedException e) {
            logger.warn("Main thread interrupted while waiting for threads to close:[]", e);
        }
    }

    private void bindKeyboardCommands() {
        //
        //Guard
        //
        if (keyboardCommandsDisposable != null) {
            return;
        }

        //
        //Connect keyboard
        //
        keyboardCommandsDisposable = keyboardSubject
            //observe down-
            .observeOn(uiScheduler)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe(string -> {
                if (string.equalsIgnoreCase("0")) {
                    dncScrubberViewModel.processUiEvent(new ExitEvent());
                } else if (string.equalsIgnoreCase("1")) {
                    dncScrubberViewModel.processUiEvent(new LoadRawLeadsEvent(""));
                } else {
                    printToScreenSubject.onNext("Please enter a valid entry... \n");
                }

            }, throwable -> {
                logger.error("Keyboard listener threw an error: " + throwable);
                throw new Exception("Keyboard failed", throwable);
            });
    }

    private void unbindKeyboardCommands() {
        if (keyboardCommandsDisposable != null) {
            keyboardCommandsDisposable.dispose();
            compositeDisposable.delete(keyboardCommandsDisposable);
        }
        keyboardCommandsDisposable = null;
    }

    private void bindKeyboardFileName(final CommandType previousCommandType) {
        //
        //Guard
        //
        if (keyboardFileNameDisposable != null) {
            return;
        }

        //
        //Connect keyboard
        //
        keyboardFileNameDisposable = keyboardSubject
                //observe down-
                .observeOn(uiScheduler)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(fileName -> {
                    switch (previousCommandType) {
                        case LOAD_RAW_LEADS:
                            dncScrubberViewModel.processUiEvent(new LoadRawLeadsEvent(fileName));
                        default:
                            throw new Exception("No previous command to track event.");
                    }
                }, throwable -> {
                    logger.error("Keyboard listener threw an error: " + throwable);
                    throw new Exception("Keyboard failed", throwable);
                });
    }

    private void unbindKeyboardFileName() {
        if (keyboardFileNameDisposable != null) {
            keyboardFileNameDisposable.dispose();
            compositeDisposable.delete(keyboardFileNameDisposable);
        }
        keyboardFileNameDisposable = null;
    }

    private void bindScreenProcessing() {
        processingDisposable = Observable.interval(500L, TimeUnit.MILLISECONDS)
                .flatMap(new Function<Long, ObservableSource<String>>() {
                    int interval = 0;

                    @Override
                    public ObservableSource<String> apply(Long aLong) {
                        String stringToReturn;
                        if (interval >= 20) {
                            stringToReturn = "\n";
                            interval = 0;
                        } else {
                            stringToReturn = "***";
                            interval++;
                        }

                        return Observable.just(stringToReturn);
                    }
                })
                //observe down-
                .observeOn(uiScheduler)
                .subscribe(printToScreenSubject::onNext);
        compositeDisposable.add(processingDisposable);
    }

    private void unbindScreenProcessing() {
        if (processingDisposable != null) {
            processingDisposable.dispose();
            compositeDisposable.delete(processingDisposable);
        }
    }

    /**
     * Bind to all data in
     */
    private void bind() {
        //
        //Bind to print screen subject
        //
        compositeDisposable.add(printToScreenSubject
                .observeOn(uiScheduler)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.equalsIgnoreCase("***")) {
                            System.out.print(s);
                        } else {
                            System.out.println(s);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        logger.debug("Complete print subject subscription");
                    }
                }));

        //
        //Bind to UiModel
        //
        compositeDisposable.add(dncScrubberViewModel.getUiModels()
                .subscribeOn(uiScheduler)
                .subscribe(
                        this::processUiModel,
                        throwable -> {
                            throw new UnsupportedOperationException("Errors from Model Unsupported: "
                                    + throwable.getLocalizedMessage());
                        })
        );

        //
        //Bind To Keyboard
        //Note - You ran into issues with opening and closing the system.in scanner. It's recommended you open one
        //for the duration of the program. You also ran into the problem that reading a stream was a BLOCKING call.
        //To avoid this 'hanging' waiting for system input that you can't interrupt, you needed to wrap everything
        //in a BufferedReader that is busy waiting for keyboard input.
        //
        //I don't like busy-waiting, but it is what it is.
        //
        compositeDisposable.add(Observable.<String>create(subscriber -> {
            BufferedReader keyboardBufferedReader = new BufferedReader(new InputStreamReader(System.in));
            boolean terminate = false;
            do {
                try {
                    // wait until we have data to complete a readLine()
                    while (!keyboardBufferedReader.ready()) {
                        //Note - Code Smell, solution?
                        Thread.sleep(200);
                    }
                    String input = keyboardBufferedReader.readLine();
                    subscriber.onNext(input);
                } catch (InterruptedException e) {
                    keyboardBufferedReader.close();
                    terminate = true;
                    if (!subscriber.isDisposed()) {
                        logger.error("Keyboard input threw an expected error [].  Subject not disposed gracefully", e);
                        subscriber.onError(e);
                    }
                }
            } while (!terminate || !subscriber.isDisposed());

            keyboardBufferedReader.close();
        })
                //subscribe - up
                .subscribeOn(keyboardScheduler)
                /*
                    Note - https://github.com/ReactiveX/RxJava/issues/4438
                    You need to wrap this in a disposable so the logic during a shutdown
                    does not throw exceptions o the Future<> contract.
                 */
                .subscribe(keyboardSubject::onNext));

    }

    private void createUi() {
        //Call the JFrame setup in the auto generated code.
        dncScrubberMainView.createUi();

        //Since these values are static, setting them here and no in the UIModel
        dncScrubberMainView.commandList.setListData(screenData.getCommands());

        //When window closes, kill program.
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

        //Bind Button
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
                        List<String> selectedItems = (List<String>) dncScrubberMainView.commandList.getSelectedValuesList();
                        if (selectedItems.isEmpty()) {
                            dncScrubberViewModel.processUiEvent(new NoSelectionEvent());
                        } else {
                            //Load Raw Leads
                            if (selectedItems.get(0).equalsIgnoreCase(screenData.getCommands()[0])) {
                                
                            }
                        }
                    }
                }));



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
        if (uiModel.getScreenMessage() != null && !uiModel.getScreenMessage().isEmpty()) {
            dncScrubberMainView.outputTextArea.setText(uiModel.getScreenMessage());

            unbindScreenProcessing();
            printToScreenSubject.onNext(uiModel.getScreenMessage());

            if (uiModel.getPreviousCommand() == CommandType.NONE) {
                bindKeyboardCommands();
                unbindKeyboardFileName();
            } else {
                unbindKeyboardCommands();
                bindKeyboardFileName(uiModel.getPreviousCommand());
            }
        } else if (uiModel.isInFlight()) {
            unbindKeyboardFileName();
            unbindKeyboardCommands();
            bindScreenProcessing();
        } else {
            //exit program
            mainThreadSubject.onNext("exit");
        }
    }


}
