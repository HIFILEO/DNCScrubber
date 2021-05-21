package com.LEO.DNCScrubber.Scrubber.model;/*
Copyright 2021 Braavos Holdings, LLC

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


import com.LEO.DNCScrubber.Scrubber.model.uiModel.ScreenInfo;
import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Data to be shown on the screen.
 *
 * Developer note - I decided to load from recourses outside the business logic architecture since this information
 * is only useful for the UI. It simplifies the back and forth logic just for printing data to the screen.
 */
public class ScreenData {
    final static Logger logger = LoggerFactory.getLogger(DncScrubberViewController.class);
    private final Object monitor = new Object();
    private ScreenInfo screenInfo;

    public ScreenData(Gson gson) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                /*
                Note - PITA trying to get a file to load from resources. Only BufferReader worked after
                multiple tries. Problems with IntelliJ as well it seems but can't confirm.
                 */
                String fileName = "screenInfo";

                ClassLoader classLoader = getClass().getClassLoader();
                StringBuilder resultStringBuilder = new StringBuilder();
                try (BufferedReader br
                             = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resultStringBuilder.append(line).append("\n");
                    }
                }
                screenInfo = gson.fromJson(resultStringBuilder.toString(), ScreenInfo.class);
                emitter.onComplete();
            }})
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //No-Op
                    }

                    @Override
                    public void onNext(String s) {
                        //No-Op
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error("Failed to load resources: Error %s: ", e);

                        //Crash program if you can't load from resources
                       throw new RuntimeException(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        //release monitor
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }
                });
    }

    /**
     * Get the main commands to show to the screen.
     *
     * Note - blocking call until resources are loaded. Will crash program if resources don't load.
     * @return - main commands to show
     */
    public String getMainCommands() {
        sync();
        return screenInfo.mainCommands;
    }

    /**
     * Get the load raw leads error
     *
     * Note - blocking call until resources are loaded. Will crash program if resources don't load.
     * @return - load raw leads error
     */
    public String getError() {
        sync();
        return screenInfo.error;
    }

    public String getSuccess() {
        sync();
        return screenInfo.success;
    }

    public String getFileNameMessage() {
        sync();
        return screenInfo.fileNameMessage;
    }

    public String[] getCommands() {
        sync();
        return screenInfo.commands;
    }

    public String getNoSelectionMade() {
        sync();
        return screenInfo.noSelectionMade;
    }

    /**
     * Syncs the monitor and calling thread for data to load.
     */
    private void sync() {
        synchronized (monitor) {
            while (screenInfo == null) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    logger.error("Wait interrupted. %S ", e);
                }
            }
        }
    }
}
