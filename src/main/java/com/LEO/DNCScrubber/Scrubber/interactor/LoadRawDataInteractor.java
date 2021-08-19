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
import com.LEO.DNCScrubber.Scrubber.controller.RawLeadErrorImpl;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.action.LoadRawLeadsAction;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.Scrubber.model.result.LoadRawLeadsResult;
import com.google.common.annotations.VisibleForTesting;
import io.reactivex.*;
import io.reactivex.functions.BiFunction;
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
        return Observable.create(getLoadRawLeadsObservable(loadRawLeadsAction))
                .onErrorReturn(new Function<Throwable, LoadRawLeadsResult>() {
                    @Override
                    public LoadRawLeadsResult apply(Throwable throwable) throws Exception {
                        logger.error("LoadRawLeads caught an error - return {}", throwable.getMessage());
                        return LoadRawLeadsResult.error(
                                throwable.getMessage() + (throwable.getCause() == null ? "" : throwable.getCause()),
                                loadRawLeadsAction.getCsvFile() == null
                                );
            }
        })
                .startWith(LoadRawLeadsResult.inFlight());
    }

    @VisibleForTesting
    protected LoadRawLeadsObservable getLoadRawLeadsObservable(LoadRawLeadsAction loadRawLeadsAction) {
        return new LoadRawLeadsObservable(
                this.csvFileController,
                this.databaseGateway,
                loadRawLeadsAction,
                new StoreRawLeadFlatMap(databaseGateway),
                new ProcessRawLeadErrorFlatMap(),
                new StatusCounterScanner());
    }

    /**
     * Generic Status message for processing {@link RawLead}
     */
    public abstract static class Status  {
        boolean error;
        String errorMessage;
    }

    /**
     * Specific status to process a {@link RawLead} with the database.
     */
    public static class DatabaseStatus extends Status {
        boolean newColdRvmLeadSaved;
        boolean duplicateColdRvmEntry;
        boolean duplicatePerson;
        boolean duplicateProperty;
    }

    /**
     * Specific status to process a {@link RawLead} with the CSV Reader
     */
    public static class CsvStatus extends Status {

    }

    public static class StatusCounter {
        int numberOfColdLeadsAdded=  0;
        int numberOfColdLeadDuplicates = 0;
        int numberOfErrors = 0;
        String errorMessage = "";
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
            //Address Property
            //If the property exists in the database, do nothing. Reuse it.
            //
            if (!(propertyFromDatabase instanceof EmptyProperty)) {
                logger.info("Property {} exists. Reuse for cold lead", propertyFromDatabase.getNaturalId());
                databaseStatus.duplicateProperty = true;
                coldRvmLeadToProcess.setProperty(propertyFromDatabase);
            }

            //
            //Address Person
            //If the person exists in the database, check for property link.
            //
            if (!(personFromDatabase instanceof EmptyPerson)) {
                logger.info("Person {} exists. Reuse for cold lead", personFromDatabase.getNaturalId());
                databaseStatus.duplicatePerson = true;
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
        private ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMap;
        private StoreRawLeadFlatMap storeRawLeadFlatMap;
        private StatusCounterScanner statusCounterScanner;

        /**
         * Constructor
         * @param csvFileController -
         * @param databaseGateway -
         * @param loadRawLeadsAction -
         */
        public LoadRawLeadsObservable(CsvFileController csvFileController, DatabaseGateway databaseGateway,
                                      LoadRawLeadsAction loadRawLeadsAction, StoreRawLeadFlatMap storeRawLeadFlatMap,
                                      ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMap,
                                      StatusCounterScanner statusCounterScanner) {
            this.csvFileController = csvFileController;
            this.databaseGateway = databaseGateway;
            this.loadRawLeadsAction = loadRawLeadsAction;
            this.storeRawLeadFlatMap = storeRawLeadFlatMap;
            this.processRawLeadErrorFlatMap = processRawLeadErrorFlatMap;
            this.statusCounterScanner = statusCounterScanner;
        }

        @Override
        public void subscribe(ObservableEmitter<LoadRawLeadsResult> emitter) throws Exception {
            /*
            Note - How this works:
            - Read as many entries as possible from a well formatted CSV file. Return RawLeads w/o errors.
            - Return all exceptions for missing data fields as RawLeads w/ errors
            - split the stream into the best flatmap based on error or not
            - Scan all the results so you have one Status Counter and report last element once the stream completes
            - Any MAJOR errors need to propagate through the stream.
             */
            csvFileController.readRawLeads(loadRawLeadsAction.getCsvFile())
                    .flatMap(rawLead -> {
                        if (rawLead.hasError()) {
                            return processRawLeadErrorFlatMap.apply(rawLead);
                        } else {
                            return storeRawLeadFlatMap.apply(rawLead);
                        }
                    })
                    .scan(new StatusCounter(), statusCounterScanner)
                    .lastElement()
                    .subscribe(statusCounter -> {
                        emitter.onNext(LoadRawLeadsResult.success(
                                statusCounter.numberOfColdLeadDuplicates,
                                statusCounter.numberOfColdLeadsAdded,
                                statusCounter.numberOfErrors,
                                statusCounter.errorMessage
                        ));

                        emitter.onComplete();
                    }, emitter::onError);
        }

        @VisibleForTesting
        protected void setStoreRawLeadFlatMap(StoreRawLeadFlatMap storeRawLeadFlatMap) {
            this.storeRawLeadFlatMap = storeRawLeadFlatMap;
        }

        @VisibleForTesting
        protected void setDatabaseStatusCounterScanner(StatusCounterScanner statusCounterScanner) {
            this.statusCounterScanner = statusCounterScanner;
        }
    }

    /**
     * Takes a {@link RawLeadErrorImpl} and converts it to a {@link CsvStatus}
     */
    protected static class ProcessRawLeadErrorFlatMap implements Function<RawLead, ObservableSource<CsvStatus>> {

        @Override
        public ObservableSource<CsvStatus> apply(RawLead rawLead) throws Exception {

            if (rawLead.hasError()) {
                CsvStatus csvStatus = new CsvStatus();
                csvStatus.error = true;
                csvStatus.errorMessage = rawLead.getErrorMessage();
                return Observable.just(csvStatus);
            }
            else {
                logger.warn("RawLead is not an error, return empty.");
                return Observable.empty();
            }
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

        /**
         * Constructor -
         * @param databaseGateway -
         */
        public StoreRawLeadFlatMap(DatabaseGateway databaseGateway) {
            this.databaseGateway = databaseGateway;
        }

        @Override
        public ObservableSource<DatabaseStatus> apply(RawLead rawLead) throws Exception {
            RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
            ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);
            DatabaseStatus databaseStatus = new DatabaseStatus();

            /*
            Note - looking back at this, I can see this isn't a functional reactive program as the objects
            are mutable. That's ok, shit happens. I didn't notice this until I was refactoring the business
            logic around creating keys and not wanting null / corrupted natural keys.
             */
            return databaseGateway.loadColdRvmLeadByNaturalId(coldRvmLeadToProcess.getNaturalId())
                    .flatMap(new Function<ColdRvmLead, ObservableSource<DatabaseStatus>>() {
                        @Override
                        public ObservableSource<DatabaseStatus> apply(ColdRvmLead coldRvmLead) throws Exception {
                            //ColdRvmLead exists - we don't update raw leads
                            databaseStatus.duplicateColdRvmEntry = true;
                            databaseStatus.duplicatePerson = true;
                            databaseStatus.duplicateProperty = true;

                            return Observable.just(databaseStatus);
                        }
                    }).switchIfEmpty(
                            //No ColdRvmLead, use coldRvmLeadToProcess. Fetch person and property from DB process
                            Observable.zip(
                                    databaseGateway.loadPersonByNaturalId(coldRvmLeadToProcess.getPerson().getNaturalId())
                                            .switchIfEmpty(Observable.just(EmptyPerson.createEmptyPerson())),
                                    databaseGateway.loadPropertyByNaturalId(coldRvmLeadToProcess.getProperty().getNaturalId())
                                            .switchIfEmpty(Observable.just(EmptyProperty.createEmptyProperty())),
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
                            })).onErrorReturn(new Function<Throwable, DatabaseStatus>() {
                        @Override
                        public DatabaseStatus apply(Throwable throwable) throws Exception {
                            logger.warn("StoreRawLeadFlatMap had some kind of error - {}", databaseStatus.errorMessage);
                            databaseStatus.error = true;
                            databaseStatus.errorMessage = throwable.getMessage();
                            return databaseStatus;
                        }
                    });
        }
    }

    /**
     * Scans the incoming {@link Status} and update the seeded {@link StatusCounter}.
     */
    protected static class StatusCounterScanner implements
            BiFunction<StatusCounter, Status, StatusCounter> {

        @Override
        public StatusCounter apply(StatusCounter statusCounter, Status status) {

            if (status.error) {
                statusCounter.numberOfErrors++;

                statusCounter.errorMessage = statusCounter.errorMessage + "\n"
                        + status.errorMessage;
            }

            if (status instanceof DatabaseStatus) {
                DatabaseStatus databaseStatus = (DatabaseStatus) status;

                if (databaseStatus.duplicateColdRvmEntry) {
                    statusCounter.numberOfColdLeadDuplicates++;
                } else if (databaseStatus.newColdRvmLeadSaved) {
                    statusCounter.numberOfColdLeadsAdded++;
                }
            }

            return statusCounter;
        }
    }

}
