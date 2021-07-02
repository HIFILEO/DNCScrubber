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
import com.sun.org.apache.xpath.internal.functions.Function2Args;
import io.reactivex.*;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import org.assertj.core.util.VisibleForTesting;
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
        //4 - I want a message that tells me how many were saved successfully.
        return Observable.create(new ObservableOnSubscribe<LoadRawLeadsResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadRawLeadsResult> emitter) throws Exception {

                csvFileController.readRawLeads(loadRawLeadsAction.getCsvFile())
                    .flatMap(new Function<RawLead, ObservableSource<DatabaseStatus>>() {
                        @Override
                        public ObservableSource<DatabaseStatus> apply(RawLead rawLead) throws Exception {
                            RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
                            ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);
                            DatabaseStatus databaseStatus = new DatabaseStatus();

                            /*
                            Business Rules
                            1 - If the cold lead exists, do nothing. We don't update from raw leads. Return
                            2 - If the property exists in the database, do nothing. Reuse it. We don't update from raw leads.
                            3 - If the person exists in the database, check if they own the property to process.
                                - if they own the property, reuse person and property and create new ColdRvmLead since it does not exist
                                - if they don't own the property, add the property to person and ColdRvmLead then save to database.
                             */
                            return databaseGateway.loadColdRvmLeadByNaturalId(coldRvmLeadToProcess.getNaturalId())
                                .flatMap(new Function<ColdRvmLead, ObservableSource<DatabaseStatus>>() {
                                    @Override
                                    public ObservableSource<DatabaseStatus> apply(ColdRvmLead coldRvmLead) throws Exception {
                                        //ColdRvmLead exists - we don't update raw leads
                                        DatabaseStatus databaseStatus = new DatabaseStatus();
                                        databaseStatus.duplicateColdRvmEntry = true;
                                        databaseStatus.duplicatePerson = true;
                                        databaseStatus.duplicateProperty = true;

                                        return Observable.just(databaseStatus);
                                    }
                                }).switchIfEmpty(new ObservableSource<DatabaseStatus>() {
                                    @Override
                                    public void subscribe(Observer<? super DatabaseStatus> observer) {
                                        //No ColdRvmLead, use coldRvmLeadToProcess. Fetch person and property from DB process
                                        Observable.zip(
                                            databaseGateway.loadPersonByNaturalId(coldRvmLeadToProcess.getPerson().getNaturalId()).switchIfEmpty(Observable.just(new Person())),
                                            databaseGateway.loadPropertyByNaturalId(coldRvmLeadToProcess.getProperty().getNaturalId()).switchIfEmpty(Observable.just(new Property())),
                                            new ColdRvmLeadProcessor(coldRvmLeadToProcess, databaseStatus)
                                        ).flatMap(new Function<ColdRvmLead, ObservableSource<Boolean>>() {
                                            @Override
                                            public ObservableSource<Boolean> apply(ColdRvmLead coldRvmLead) throws Exception {
                                                return databaseGateway.writeColdRvmLead(coldRvmLead);
                                            }
                                        }).flatMap(new Function<Boolean, ObservableSource<DatabaseStatus>>() {
                                            @Override
                                            public ObservableSource<DatabaseStatus> apply(Boolean aBoolean) throws Exception {
                                                if (aBoolean) {
                                                    databaseStatus.newColdRvmLeadSaved = true;
                                                } else {
                                                    databaseStatus.newColdRvmLeadSaved = false;
                                                }

                                                return Observable.just(databaseStatus);
                                            }
                                        });
                                    }
                                });
                        }
                    }).scan(new DatabaseStatusCounter(), new BiFunction<DatabaseStatusCounter, DatabaseStatus, DatabaseStatusCounter>() {
                        @Override
                        public DatabaseStatusCounter apply(DatabaseStatusCounter databaseStatusCounter, DatabaseStatus databaseStatus2) throws Exception {
                            //TODO - you need logic here to convert database status to counter
                            return databaseStatusCounter;
                        }
                    }).lastElement()
                    .subscribe(new Consumer<DatabaseStatusCounter>() {
                        @Override
                        public void accept(DatabaseStatusCounter databaseStatusCounter) throws Exception {
                            emitter.onNext(new LoadRawLeadsResult(Result.ResultType.SUCCESS, false,
                                    "", false, false));
                            emitter.onComplete();
                        }
                    });
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

//
//        return Completable.create(new CompletableOnSubscribe() {
//            @Override
//            public void subscribe(CompletableEmitter emitter) throws Exception {
//                //load raw lead
//                csvFileController.readRawLeads(loadRawLeadsAction.getCsvFile())
//                //for each raw lead, save to database - return DatabaseStatus
//                        .flatMap(new Function<RawLead, ObservableSource<DatabaseStatus>>() {
//                            @Override
//                            public ObservableSource<DatabaseStatus> apply(RawLead rawLead) throws Exception {
//
//                            }
//                        })
//
//                       csvFileReader.readRawLeadData(loadRawLeadsAction.getCsvFile())
//                               .count()
//                               .subscribe(numberOfItemsEmitted -> emitter.onComplete(),
//                                       emitter::onError
//                               );
//
//                /*
//                Business Rules
//                1 - If the cold lead exists, do nothing. We don't update from raw leads.
//                2 - If the property exists in the database, do nothing. Reuse it. We don't update from raw leads.
//                3 - If the person exists in the database, check if they own the property. If so, we don't update.
//                 */
//
//
//
//
//
//                //Test
//                Address address = new Address();
//                address.setMailingAddress("52 Roosevelt Street");
//                Property property = new Property();
//                property.setAddress(address);
//
//                Phone phone = new Phone();
//                phone.setPhoneNumber("631-766-5134");
//                Person person = new Person();
//                person.setFirstName("Dan");
//                person.setLastName("Leonardis");
//                person.setPhone1(phone);
//                person.addProperty(property);
//
//                ColdRvmLead coldRvmLead = new ColdRvmLead();
//                coldRvmLead.setDateWorkflowStarted(new Date());
//                coldRvmLead.setPerson(person);
//                coldRvmLead.setProperty(property);
//
//                databaseGateway.writeColdRvmLead(coldRvmLead).subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        Thread.sleep(5000);
//                        emitter.onComplete();
//                    }
//                });
//            }
//        }).andThen(new ObservableSource<LoadRawLeadsResult>() {
//            @Override
//            public void subscribe(Observer<? super LoadRawLeadsResult> observer) {
//                observer.onNext(new LoadRawLeadsResult(Result.ResultType.SUCCESS, false,
//                        "", false, false));
//            }
//        }).onErrorReturn(new Function<Throwable, LoadRawLeadsResult>() {
//            @Override
//            public LoadRawLeadsResult apply(Throwable throwable) throws Exception {
//                return new LoadRawLeadsResult(Result.ResultType.FAILURE,
//                        false,
//                        throwable.getMessage(),
//                        false,
//                        loadRawLeadsAction.getCsvFile() == null);
//            }
//        })
//                .startWith(LoadRawLeadsResult.inFlight(true));
    }

    public static class DatabaseStatus {
        boolean newColdRvmLeadSaved;
        boolean duplicateColdRvmEntry;
        boolean duplicatePerson;
        boolean duplicateProperty;
    }

    public static class DatabaseStatusCounter {
        int numberOfColdLeadsAdded;
        int numberOfColdLeadDuplicates;
    }

    /**
     * Handle the creation of the ColdRvmLead based on entries in the Database and coldRvmLead passed in via the
     * constructor.
     *
     * Business Rules
     * 1 - If the property exists in the database, do nothing. Reuse it. We don't update from raw leads.
     * 2 - If the person exists in the database, check if they own the property to process.
     *     - if they own the property, reuse person and property and create new ColdRvmLead since it does not exist
     *     - if they don't own the property, add the property to person and ColdRvmLead then save to database.
     *
     */
    @VisibleForTesting
    protected static class ColdRvmLeadProcessor implements BiFunction<Person, Property, ColdRvmLead> {
        private ColdRvmLead coldRvmLeadToProcess;
        private DatabaseStatus databaseStatus;

        public ColdRvmLeadProcessor(ColdRvmLead coldRvmLead, DatabaseStatus databaseStatus) {
            coldRvmLeadToProcess = coldRvmLead;
            this.databaseStatus = databaseStatus;
        }

        @Override
        public ColdRvmLead apply(Person personFromDatabase, Property propertyFromDatabase) throws Exception {
            //
            //Setup
            //
            databaseStatus.newColdRvmLeadSaved = true;

            //
            //Address Property
            //If the property exists in the database, do nothing. Reuse it.
            //
            if (!propertyFromDatabase.getNaturalId().isEmpty()) {
                logger.info("Property {} exists. Reuse for cold lead", propertyFromDatabase.getNaturalId());
                databaseStatus.duplicatePerson = true;
                coldRvmLeadToProcess.setProperty(propertyFromDatabase);
            }

            //
            //Address Person
            //
            if (!personFromDatabase.getNaturalId().isEmpty()) {
                logger.info("Person {} exists. Reuse for cold lead", personFromDatabase.getNaturalId());
                databaseStatus.duplicateProperty = true;
                //check property and see if they own it, if not add it.
                boolean propertyFound = false;
                for(Property propertyInPerson : personFromDatabase.getPropertyList()) {
                    if ( propertyInPerson.getNaturalId().equalsIgnoreCase(coldRvmLeadToProcess.getProperty().getNaturalId())) {
                        propertyFound = true;
                        break;
                    }
                }

                if (!propertyFound) {
                    logger.info("Add property to Person.");
                    personFromDatabase.addProperty(coldRvmLeadToProcess.getProperty());
                }
                coldRvmLeadToProcess.setPerson(personFromDatabase);
            }

            return coldRvmLeadToProcess;
        }
    }
}
