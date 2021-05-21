/*
Copyright 2021 Braavos Holdings LLC

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
package com.LEO.DNCScrubber.core.application;

import com.LEO.DNCScrubber.Scrubber.viewconntroller.DncScrubberViewController;
import com.LEO.DNCScrubber.core.dagger.ApplicationComponent;
import com.LEO.DNCScrubber.core.dagger.DaggerApplicationComponent;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DncScrubberApplication {
    final static Logger logger = LoggerFactory.getLogger(DncScrubberApplication.class);
    private ApplicationComponent component;

    @Inject
    DncScrubberViewController dncScrubberViewController;

    public static void main(String args[]) {
        logger.info("Starting DNCScrubberApplication");
        new DncScrubberApplication();
        logger.info("Main Application Terminated.");
    }

    public DncScrubberApplication() {
        setupComponent();
        runProgram();
    }

    /**
     * Setup the Dagger2 component graph.
     */
    private void setupComponent() {
        if (component == null) {
            component = DaggerApplicationComponent.builder()
                    .application(this)
                    .build();
            component.inject(this);
        } else {
            logger.debug("setupComponent() called but ApplicationComponent already set");
        }
    }

    private void runProgram(){
        dncScrubberViewController.runUI()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        logger.debug("doOnComplete - inside app class thread " + Thread.currentThread().getName());
                    }
                })
                .blockingSubscribe(
                new Observer<String>() {
                    Disposable d;
                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equalsIgnoreCase("exit")) {
                            logger.info("Exit Received on thread {}. Terminate Program", Thread.currentThread().getName());
                            d.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error("Application threw an error: ", e);
                        d.dispose();
                    }

                    @Override
                    public void onComplete() {
                        logger.debug("onComplete - thread = " + Thread.currentThread().getName());
                        logger.info("UI application completed.");
                    }
                }
        );
    }
}
