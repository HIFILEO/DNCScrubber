package com.LEO.DNCScrubber.Scrubber.interactor;/*
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

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileController;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.action.*;
import com.LEO.DNCScrubber.Scrubber.model.result.*;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import java.util.Arrays;

/**
 * Interactor for DNC Scrubber application.
 */
public class ScrubberInteractor {
    private final LoadRawDataInteractor loadRawDataInteractor;

    @NonNull
    private final ObservableTransformer<Action, Result> transformActionIntoResults;

    public ScrubberInteractor(CsvFileController csvFileController, DatabaseGateway databaseGateway) {
        this.loadRawDataInteractor= new LoadRawDataInteractor(csvFileController, databaseGateway);

        /*
        Note the 'upstream->' represents new ObservableTransformer<T1,T1>
         */
        ObservableTransformer<ExitAction, ExitResult> transformExit = upstream ->
                upstream.flatMap((Function<ExitAction, ObservableSource<ExitResult>>)
                        this::transformExit);

        ObservableTransformer<NoSelectionAction, NoSelectionResult> transformNoSelection = upstream ->
                upstream.flatMap((Function<NoSelectionAction, ObservableSource<NoSelectionResult>>)
                        this::transformNoSelection);

        ObservableTransformer<LoadRawLeadsAction, LoadRawLeadsResult> transformLoadRawLead = upstream ->
                upstream.flatMap((Function<LoadRawLeadsAction, ObservableSource<LoadRawLeadsResult>>)
                this::transformLoadRawLead);

        ObservableTransformer<LoadFileDialogAction, LoadFileDialogResult> transformLoadFileDialog = upstream ->
                upstream.flatMap((Function<LoadFileDialogAction, ObservableSource<LoadFileDialogResult>>)
                        this::transformLoadFileDialog);

        ObservableTransformer<SaveFileDialogAction, SaveFileDialogResult> transformSaveFileDialog = upstream ->
                upstream.flatMap((Function<SaveFileDialogAction, ObservableSource<SaveFileDialogResult>>)
                        this::transformSaveFileDialog);

        transformActionIntoResults = upstream -> upstream.publish
                (new Function<Observable<Action>, ObservableSource<Result>>() {
            @Override
            public ObservableSource<Result> apply(Observable<Action> actionObservable) {
                return Observable.merge(
                    Arrays.asList(
                        actionObservable.ofType(ExitAction.class).compose(transformExit),
                        actionObservable.ofType(LoadFileDialogAction.class).compose(transformLoadFileDialog),
                        actionObservable.ofType(LoadRawLeadsAction.class).compose(transformLoadRawLead),
                        actionObservable.ofType(NoSelectionAction.class).compose(transformNoSelection),
                        actionObservable.ofType(SaveFileDialogAction.class).compose(transformSaveFileDialog)
                    )
                );
            }
        });
    }

    /**
     * Process {@link Action}.
     * @param actions - action to process.
     * @return - {@link Result} of the asynchronous event.
     */
    public Observable<Result> processAction(Observable<Action> actions) {
        return actions.compose(transformActionIntoResults);
    }

    private Observable<ExitResult> transformExit(ExitAction exitAction) {
        return Observable.just(new ExitResult());
    }

    private Observable<NoSelectionResult> transformNoSelection(NoSelectionAction noSelectionAction) {
        return Observable.just(new NoSelectionResult());
    }

    private Observable<LoadRawLeadsResult> transformLoadRawLead(LoadRawLeadsAction loadRawLeadsAction) {
        return loadRawDataInteractor.processLoadRawLeadsAction(loadRawLeadsAction);
    }

    private Observable<LoadFileDialogResult> transformLoadFileDialog(LoadFileDialogAction loadFileDialogAction) {
        return Observable.just(
                new LoadFileDialogResult(loadFileDialogAction.getCommandType(),
                        loadFileDialogAction.isUserCanceled(),
                        loadFileDialogAction.isFileLoadError(),
                        loadFileDialogAction.getErrorMessage())
        );
    }

    private Observable<SaveFileDialogResult> transformSaveFileDialog(SaveFileDialogAction saveFileDialogAction) {
        return Observable.just(
                new SaveFileDialogResult(saveFileDialogAction.getCommandType(),
                        saveFileDialogAction.isUserCanceled(),
                        saveFileDialogAction.isFileSaveError(),
                        saveFileDialogAction.getErrorMessage())
        );
    }
}
