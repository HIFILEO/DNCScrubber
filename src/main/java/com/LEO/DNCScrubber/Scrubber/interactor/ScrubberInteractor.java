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

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileReader;
import com.LEO.DNCScrubber.Scrubber.model.action.Action;
import com.LEO.DNCScrubber.Scrubber.model.action.ExitAction;
import com.LEO.DNCScrubber.Scrubber.model.action.LoadRawLeadsAction;
import com.LEO.DNCScrubber.Scrubber.model.action.NoSelectionAction;
import com.LEO.DNCScrubber.Scrubber.model.result.ExitResult;
import com.LEO.DNCScrubber.Scrubber.model.result.LoadRawLeadsResult;
import com.LEO.DNCScrubber.Scrubber.model.result.NoSelectionResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Interactor for DNC Scrubber application.
 */
public class ScrubberInteractor {
    private final CsvFileReader csvFileReader;

    @NonNull
    private final ObservableTransformer<ExitAction, ExitResult> transformExit;

    @NonNull
    private final ObservableTransformer<LoadRawLeadsAction, LoadRawLeadsResult> transformLoadRawLead;

    @NonNull
    private final ObservableTransformer<NoSelectionAction, NoSelectionResult> transformNoSelection;
    @NonNull
    private final ObservableTransformer<Action, Result> transformActionIntoResults;

    public ScrubberInteractor(CsvFileReader csvFileReader) {
        this.csvFileReader = csvFileReader;

        /*
        Note the 'upstream->' represents new ObservableTransformer<T1,T1>
         */
        transformExit = upstream -> {
            return upstream.flatMap(new Function<ExitAction, ObservableSource<ExitResult>>() {
                @Override
                public ObservableSource<ExitResult> apply(ExitAction exitAction) throws Exception {
                    return  Observable.just(new ExitResult());
                }
            });
        };

        transformNoSelection = upstream -> upstream.flatMap((Function<NoSelectionAction, ObservableSource<NoSelectionResult>>)
                noSelectionAction -> Observable.just(new NoSelectionResult()));

        transformLoadRawLead = upstream -> {
           return upstream.flatMap((Function<LoadRawLeadsAction, ObservableSource<LoadRawLeadsResult>>) loadRawLeadsAction -> {

               //1 - create a completable - you only care if you load file and save or throw errors. No values needed.
               //2  - andThen - change the completable back into Observable
               //3 - start with so things update on the UI.
               return Completable.create(new CompletableOnSubscribe() {
                   @Override
                   public void subscribe(CompletableEmitter emitter) throws Exception {
//                       csvFileReader.readRawLeadData("new_haven_test.csv")
//                               .count()
//                               .subscribe(numberOfItemsEmitted -> emitter.onComplete(),
//                                       emitter::onError
//                               );

                       Thread.sleep(5000);
                       emitter.onComplete();
                   }
               }).andThen(new ObservableSource<LoadRawLeadsResult>() {
                   @Override
                   public void subscribe(Observer<? super LoadRawLeadsResult> observer) {
                       observer.onNext(new LoadRawLeadsResult(Result.ResultType.SUCCESS, false,
                               ""));
                   }
               }).onErrorReturn(new Function<Throwable, LoadRawLeadsResult>() {
                   @Override
                   public LoadRawLeadsResult apply(Throwable throwable) throws Exception {
                       return new LoadRawLeadsResult(Result.ResultType.FAILURE, false,
                               throwable.getMessage());
                   }
               })
                       .startWith(LoadRawLeadsResult.inFlight(true));
           });
       };

        transformActionIntoResults = upstream -> upstream.publish
                (new Function<Observable<Action>, ObservableSource<Result>>() {
            @Override
            public ObservableSource<Result> apply(Observable<Action> actionObservable) {
                return Observable.merge(
                        actionObservable.ofType(ExitAction.class).compose(transformExit),
                        actionObservable.ofType(LoadRawLeadsAction.class).compose(transformLoadRawLead),
                        actionObservable.ofType(NoSelectionAction.class).compose(transformNoSelection)
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
}
