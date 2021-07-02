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
import com.google.common.annotations.VisibleForTesting;
import io.reactivex.*;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return Observable.create(
                new LoadRawLeadsObservable(this.csvFileController, this.databaseGateway, loadRawLeadsAction))
                .onErrorReturn(new Function<Throwable, LoadRawLeadsResult>() {
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
                    if (propertyInPerson.getNaturalId().equalsIgnoreCase(
                            coldRvmLeadToProcess.getProperty().getNaturalId())) {
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

    /**
     * Load raw leads from csv file, then apply business rules before saving to database.
     */
    @VisibleForTesting
    protected static class LoadRawLeadsObservable implements ObservableOnSubscribe<LoadRawLeadsResult> {
        private final CsvFileController csvFileController;
        private final DatabaseGateway databaseGateway;
        private final LoadRawLeadsAction loadRawLeadsAction;
        private StoreRawLeadFlatMap storeRawLeadFlatMap;
        private DatabaseStatusCounterScanner databaseStatusCounterScanner = new DatabaseStatusCounterScanner();

        /**
         * Constructor
         * @param csvFileController -
         * @param databaseGateway -
         * @param loadRawLeadsAction -
         */
        public LoadRawLeadsObservable(CsvFileController csvFileController, DatabaseGateway databaseGateway,
                                      LoadRawLeadsAction loadRawLeadsAction) {
            this.csvFileController = csvFileController;
            this.databaseGateway = databaseGateway;
            this.loadRawLeadsAction = loadRawLeadsAction;
            storeRawLeadFlatMap = new StoreRawLeadFlatMap(databaseGateway);
        }

        @Override
        public void subscribe(ObservableEmitter<LoadRawLeadsResult> emitter) throws Exception {
            csvFileController.readRawLeads(loadRawLeadsAction.getCsvFile())
                    .flatMap(storeRawLeadFlatMap)
                    .scan(new DatabaseStatusCounter(), databaseStatusCounterScanner)
                    .lastElement()
                    .subscribe(new Consumer<DatabaseStatusCounter>() {
                        @Override
                        public void accept(DatabaseStatusCounter databaseStatusCounter) throws Exception {
                            emitter.onNext(new LoadRawLeadsResult(Result.ResultType.SUCCESS, false,
                                    "", false, false));
                            emitter.onComplete();
                        }
                    });
        }

        @VisibleForTesting
        protected void setStoreRawLeadFlatMap(StoreRawLeadFlatMap storeRawLeadFlatMap) {
            this.storeRawLeadFlatMap = storeRawLeadFlatMap;
        }

        @VisibleForTesting
        protected void setDatabaseStatusCounterScanner(DatabaseStatusCounterScanner databaseStatusCounterScanner) {
            this.databaseStatusCounterScanner = databaseStatusCounterScanner;
        }
    }

    /**
     * Takes a raw lead and processes it to be stored into the database.
     *
     * Business Rules
     * 1 - If the cold lead exists, do nothing. We don't update from raw leads. Return
     * 2 - If the property exists in the database, do nothing. Reuse it. We don't update from raw leads.
     * 3 - If the person exists in the database, check if they own the property to process.
     *     - if they own the property, reuse person and property and create new ColdRvmLead since it does not exist
     *     - if they don't own the property, add the property to person and ColdRvmLead then save to database.
     *
     */
    @VisibleForTesting
    protected static class StoreRawLeadFlatMap implements Function<RawLead, ObservableSource<DatabaseStatus>> {
        private final DatabaseGateway databaseGateway;

        public StoreRawLeadFlatMap(DatabaseGateway databaseGateway) {
            this.databaseGateway = databaseGateway;
        }

        @Override
        public ObservableSource<DatabaseStatus> apply(RawLead rawLead) throws Exception {
            RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
            ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);
            DatabaseStatus databaseStatus = new DatabaseStatus();

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
                    }).switchIfEmpty(
                            //No ColdRvmLead, use coldRvmLeadToProcess. Fetch person and property from DB process
                            Observable.zip(
                                    databaseGateway.loadPersonByNaturalId(coldRvmLeadToProcess.getPerson().getNaturalId())
                                            .switchIfEmpty(Observable.just(new Person())),
                                    databaseGateway.loadPropertyByNaturalId(coldRvmLeadToProcess.getProperty().getNaturalId())
                                            .switchIfEmpty(Observable.just(new Property())),
                                    new ColdRvmLeadProcessor(coldRvmLeadToProcess, databaseStatus)
                            ).flatMap(new Function<ColdRvmLead, ObservableSource<Boolean>>() {
                                @Override
                                public ObservableSource<Boolean> apply(ColdRvmLead coldRvmLead) throws Exception {
                                    return databaseGateway.writeColdRvmLead(coldRvmLead);
                                }
                            }).flatMap(new Function<Boolean, ObservableSource<DatabaseStatus>>() {
                                @Override
                                public ObservableSource<DatabaseStatus> apply(Boolean aBoolean) throws Exception {
                                    databaseStatus.newColdRvmLeadSaved = aBoolean;
                                    return Observable.just(databaseStatus);
                                }
                            }));
        }
    }

    /**
     * Scans the incoming {@link DatabaseStatus} and update the seeded {@link DatabaseStatusCounter}.
     */
    protected static class DatabaseStatusCounterScanner implements
            BiFunction<DatabaseStatusCounter, DatabaseStatus, DatabaseStatusCounter> {

        @Override
        public DatabaseStatusCounter apply(DatabaseStatusCounter databaseStatusCounter, DatabaseStatus databaseStatus) {
            if (databaseStatus.duplicateColdRvmEntry) {
                databaseStatusCounter.numberOfColdLeadDuplicates++;
            } else if (databaseStatus.newColdRvmLeadSaved) {
                databaseStatusCounter.numberOfColdLeadsAdded++;
            }

            return databaseStatusCounter;
        }
    }

}
