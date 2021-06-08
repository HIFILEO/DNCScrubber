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
import com.LEO.DNCScrubber.Scrubber.model.action.LoadRawLeadsAction;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.Scrubber.model.result.LoadRawLeadsResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import io.reactivex.*;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LoadRawDataInteractor {
    final static Logger logger = LoggerFactory.getLogger(LoadRawDataInteractor.class);
    private final CsvFileController csvFileController;
    private final DatabaseGateway databaseGateway;

    /**
     * Constructor
     * @param csvFileController - CSV File Reader
     */
    public LoadRawDataInteractor(CsvFileController csvFileController, DatabaseGateway databaseGateway) {
        this.csvFileController = csvFileController;
        this.databaseGateway = databaseGateway;
    }

    /**
     * Process the {@link LoadRawLeadsAction}
     * @param loadRawLeadsAction - action to process
     * @return Observable for the chain
     */
    public Observable<LoadRawLeadsResult> processLoadRawLeadsAction(LoadRawLeadsAction loadRawLeadsAction) {

        //1 - create a completable - you only care if you load file and save or throw errors. No values needed.
        //2  - andThen - change the completable back into Observable
        //3 - start with so things update on the UI.
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {

//                       csvFileReader.readRawLeadData(loadRawLeadsAction.getCsvFile())
//                               .count()
//                               .subscribe(numberOfItemsEmitted -> emitter.onComplete(),
//                                       emitter::onError
//                               );

                //Test
                Address address = new Address();
                address.setMailingAddress("52 Roosevelt Street");
                Property property = new Property();
                property.setAddress(address);

                Phone phone = new Phone();
                phone.setPhoneNumber("631-766-5134");
                Person person = new Person();
                person.setFirstName("Dan");
                person.setLastName("Leonardis");
                person.setPhone1(phone);
                person.addProperty(property);

                ColdRvmLead coldRvmLead = new ColdRvmLead();
                coldRvmLead.setDateWorkflowStarted(new Date());
                coldRvmLead.setPerson(person);
                coldRvmLead.setProperty(property);

                databaseGateway.writeRawLead(coldRvmLead).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Thread.sleep(5000);
                        emitter.onComplete();
                    }
                });



            }
        }).andThen(new ObservableSource<LoadRawLeadsResult>() {
            @Override
            public void subscribe(Observer<? super LoadRawLeadsResult> observer) {
                observer.onNext(new LoadRawLeadsResult(Result.ResultType.SUCCESS, false,
                        "", false, false));
            }
        }).onErrorReturn(new Function<Throwable, LoadRawLeadsResult>() {
            @Override
            public LoadRawLeadsResult apply(Throwable throwable) throws Exception {
                return new LoadRawLeadsResult(Result.ResultType.FAILURE,
                        false,
                        throwable.getMessage(),
                        false,
                        loadRawLeadsAction.getCsvFile() == null);
            }
        })
                .startWith(LoadRawLeadsResult.inFlight(true));
    }


}
