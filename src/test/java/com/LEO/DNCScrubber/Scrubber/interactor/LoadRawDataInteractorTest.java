package com.LEO.DNCScrubber.Scrubber.interactor;
/*
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
import com.LEO.DNCScrubber.Scrubber.controller.model.RawLeadCsvImpl;
import com.LEO.DNCScrubber.Scrubber.controller.model.RawLeadErrorImpl;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.action.LoadRawLeadsAction;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.Scrubber.model.result.LoadRawLeadsResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import com.google.common.annotations.VisibleForTesting;
import io.reactivex.*;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoadRawDataInteractorTest extends RxJavaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void StatusCounterScanner_addDuplicate() throws Exception {
        //
        //Arrange
        //
        LoadRawDataInteractor.StatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.StatusCounterScanner();

        LoadRawDataInteractor.StatusCounter databaseStatusCounter =
                new LoadRawDataInteractor.StatusCounter();
        databaseStatusCounter.numberOfColdLeadDuplicates = 10;
        databaseStatusCounter.numberOfColdLeadsAdded = 9;

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        databaseStatus.duplicateColdRvmEntry = true;

        //
        //Act
        //
        LoadRawDataInteractor.StatusCounter counterToTest =
                databaseStatusCounterScanner.apply(databaseStatusCounter, databaseStatus);

        //
        //Assert
        //
        assertThat(counterToTest.numberOfColdLeadDuplicates).isEqualTo(11);
        assertThat(counterToTest.numberOfColdLeadsAdded).isEqualTo(9);
    }

    @Test
    public void StatusCounterScanner_addNew() throws Exception {
        //
        //Arrange
        //
        LoadRawDataInteractor.StatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.StatusCounterScanner();

        LoadRawDataInteractor.StatusCounter databaseStatusCounter =
                new LoadRawDataInteractor.StatusCounter();
        databaseStatusCounter.numberOfColdLeadDuplicates = 10;
        databaseStatusCounter.numberOfColdLeadsAdded = 9;

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        databaseStatus.duplicateColdRvmEntry = false;
        databaseStatus.newColdRvmLeadSaved = true;

        //
        //Act
        //
        LoadRawDataInteractor.StatusCounter counterToTest =
                databaseStatusCounterScanner.apply(databaseStatusCounter, databaseStatus);

        //
        //Assert
        //
        assertThat(counterToTest.numberOfColdLeadDuplicates).isEqualTo(10);
        assertThat(counterToTest.numberOfColdLeadsAdded).isEqualTo(10);
    }

    @Test
    public void StoreRawLeadFlatMap_isDuplicateColdRvmLead() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawDataInteractor.DatabaseStatus> testObserver;

        RawLead rawLead = new RawLeadImplTest();
        RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
        ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);

        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        when(databaseGatewayMock.loadColdRvmLeadByNaturalId(anyString()))
                .thenReturn(Observable.just(coldRvmLeadToProcess));

        //Note - you need to build the stream so can't be null
        when(databaseGatewayMock.loadPersonByNaturalId(anyString()))
                .thenReturn(Observable.empty());
        when(databaseGatewayMock.loadPropertyByNaturalId(anyString()))
                .thenReturn(Observable.empty());

        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMap =
                new LoadRawDataInteractor.StoreRawLeadFlatMap(databaseGatewayMock);

        //
        //Act
        //
        //Note - you need to convert an ObservableSource to a single in order to test
        testObserver = Single.fromObservable(storeRawLeadFlatMap.apply(rawLead)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        LoadRawDataInteractor.DatabaseStatus databaseStatus = (LoadRawDataInteractor.DatabaseStatus)
                testObserver.getEvents().get(0).get(0);
        assertThat(databaseStatus).isNotNull();
        assertThat(databaseStatus.duplicateColdRvmEntry).isTrue();
        assertThat(databaseStatus.duplicatePerson).isTrue();
        assertThat(databaseStatus.duplicateProperty).isTrue();
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
    }

    @Test
    public void StoreRawLeadFlatMap_isNewColdRvmLead() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawDataInteractor.DatabaseStatus> testObserver;

        RawLead rawLead = new RawLeadImplTest();
        RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
        ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);

        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        when(databaseGatewayMock.loadColdRvmLeadByNaturalId(anyString()))
                .thenReturn(Observable.empty());

        when(databaseGatewayMock.writeColdRvmLead(any())).thenReturn(Observable.just(true));

        //Note - you need to build the stream so can't be null
        when(databaseGatewayMock.loadPersonByNaturalId(anyString()))
                .thenReturn(Observable.empty());
        when(databaseGatewayMock.loadPropertyByNaturalId(anyString()))
                .thenReturn(Observable.empty());

        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMap =
                new LoadRawDataInteractor.StoreRawLeadFlatMap(databaseGatewayMock);

        //
        //Act
        //
        //Note - you need to convert an ObservableSource to a single in order to test
        testObserver = Single.fromObservable(storeRawLeadFlatMap.apply(rawLead)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        //assert we saved to database - Note - what we saved covered in other tests
        verify(databaseGatewayMock, Mockito.times(1)).writeColdRvmLead(any());

        LoadRawDataInteractor.DatabaseStatus databaseStatus = (LoadRawDataInteractor.DatabaseStatus)
                testObserver.getEvents().get(0).get(0);
        assertThat(databaseStatus).isNotNull();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isFalse();
        assertThat(databaseStatus.duplicateProperty).isFalse();
        assertThat(databaseStatus.newColdRvmLeadSaved).isTrue();
    }

    @Test
    public void StoreRawLeadFlatMap_databaseThrowError() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawDataInteractor.DatabaseStatus> testObserver;

        final String testing123 = "testing 123";

        RawLead rawLead = new RawLeadImplTest();
        RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();
        ColdRvmLead coldRvmLeadToProcess = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLead);

        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        when(databaseGatewayMock.loadColdRvmLeadByNaturalId(anyString()))
                .thenReturn(Observable.empty());

        when(databaseGatewayMock.writeColdRvmLead(any())).thenReturn(Observable.error(new Throwable(testing123)));

        //Note - you need to build the stream so can't be null
        when(databaseGatewayMock.loadPersonByNaturalId(anyString()))
                .thenReturn(Observable.empty());
        when(databaseGatewayMock.loadPropertyByNaturalId(anyString()))
                .thenReturn(Observable.empty());

        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMap =
                new LoadRawDataInteractor.StoreRawLeadFlatMap(databaseGatewayMock);

        //
        //Act
        //
        //Note - you need to convert an ObservableSource to a single in order to test
        testObserver = Single.fromObservable(storeRawLeadFlatMap.apply(rawLead)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        //assert we saved to database - Note - what we saved covered in other tests
        verify(databaseGatewayMock, Mockito.times(1)).writeColdRvmLead(any());

        LoadRawDataInteractor.DatabaseStatus databaseStatus = (LoadRawDataInteractor.DatabaseStatus)
                testObserver.getEvents().get(0).get(0);
        assertThat(databaseStatus).isNotNull();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isFalse();
        assertThat(databaseStatus.duplicateProperty).isFalse();
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
        assertThat(databaseStatus.error).isTrue();
        assertThat(databaseStatus.errorMessage).isEqualTo(testing123);
    }

    @Test
    public void ProcessRawLeadErrorFlatMap_validStatus() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawDataInteractor.CsvStatus> testObserver;
        final String ERROR_MSG = "This is just a test";

        RawLead rawLead = new RawLeadErrorImpl(ERROR_MSG);

        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMap =
                new LoadRawDataInteractor.ProcessRawLeadErrorFlatMap();

        //
        //Act
        //
        //Note - you need to convert an ObservableSource to a single in order to test
        testObserver = Single.fromObservable(processRawLeadErrorFlatMap.apply(rawLead)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        LoadRawDataInteractor.CsvStatus csvStatus = (LoadRawDataInteractor.CsvStatus)
                testObserver.getEvents().get(0).get(0);

        assertThat(csvStatus).isNotNull();
        assertThat(csvStatus.error).isTrue();
        assertThat(csvStatus.errorMessage).isEqualToIgnoringCase(ERROR_MSG);
    }

    @Test
    public void ProcessRawLeadErrorFlatMap_emptyResponse() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawDataInteractor.CsvStatus> testObserver;

        RawLead rawLead = new RawLeadImplTest();

        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMap =
                new LoadRawDataInteractor.ProcessRawLeadErrorFlatMap();

        //
        //Act
        //
        //Note - you need to convert an ObservableSource to an Observable
        testObserver = Observable.wrap(processRawLeadErrorFlatMap.apply(rawLead)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        //Note - empty will be a terminal event.
        testObserver.assertNoErrors();
        testObserver.assertNoValues();
        testObserver.assertComplete();
    }

    @Test
    public void ColdRvmLeadProcessor_databaseEmpty() throws Exception {
        //
        //Arrange
        //
        EmptyPerson emptyPerson = EmptyPerson.createEmptyPerson();
        EmptyProperty emptyProperty = EmptyProperty.createEmptyProperty();

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();

        ColdRvmLead coldRvmLeadMock = Mockito.mock(ColdRvmLead.class);

        LoadRawDataInteractor.ColdRvmLeadProcessor coldRvmLeadProcessor =
                new LoadRawDataInteractor.ColdRvmLeadProcessor(coldRvmLeadMock, databaseStatus);

        //
        //Act
        //
        ColdRvmLead coldRvmLeadReturned = coldRvmLeadProcessor.apply(emptyPerson, emptyProperty);

        //
        //Assert
        //
        //test ColdRvmLead
        assertThat(coldRvmLeadReturned).isEqualTo(coldRvmLeadMock);
        verifyZeroInteractions(coldRvmLeadReturned);

        //test databaseStatus
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isFalse();
        assertThat(databaseStatus.duplicateProperty).isFalse();
    }

    @Test
    public void ColdRvmLeadProcessor_PropertyExistsPersonNew() throws Exception {
        //
        //Arrange
        //
        Person personFromDatabase = EmptyPerson.createEmptyPerson();

        Address propertyAddress = new Address(
                "123 Ave property",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Property propertyFromDatabase = new Property(
                "APN#12345",
                propertyAddress
        );
        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();

        ColdRvmLead coldRvmLeadMock = Mockito.mock(ColdRvmLead.class);

        LoadRawDataInteractor.ColdRvmLeadProcessor coldRvmLeadProcessor =
                new LoadRawDataInteractor.ColdRvmLeadProcessor(coldRvmLeadMock, databaseStatus);

        //
        //Act
        //
        ColdRvmLead coldRvmLeadReturned = coldRvmLeadProcessor.apply(personFromDatabase, propertyFromDatabase);

        //
        //Assert
        //
        //Same objects should be returned
        assertThat(coldRvmLeadReturned).isEqualTo(coldRvmLeadMock);

        //Add the database property
        verify(coldRvmLeadMock, Mockito.times(1)).setProperty(propertyFromDatabase);

        //No addition from Person
        verify(coldRvmLeadMock, Mockito.times(0)).setPerson(any());

        //test databaseStatus
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isFalse();
        assertThat(databaseStatus.duplicateProperty).isTrue();
        assertThat(databaseStatus.error).isFalse();
    }

    @Test
    public void ColdRvmLeadProcessor_PropertyExistsPersonExists_WithProperty() throws Exception {
        //
        //Arrange
        //
        Address propertyAddress = new Address(
                "123 Ave property",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Property propertyFromDatabase = new Property(
                "APN#1234",
                propertyAddress
        );

        Address personAddress = new Address(
                "123 Ave person",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Person personFromDatabase2 = new Person(
                "Dan",
                "Leo",
                personAddress
        );
        personFromDatabase2.addProperty(propertyFromDatabase);
        Person personFromDatabaseSpy = Mockito.spy(personFromDatabase2);

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();

        ColdRvmLead coldRvmLeadMock = Mockito.mock(ColdRvmLead.class);

        when(coldRvmLeadMock.getProperty()).thenReturn(propertyFromDatabase);

        LoadRawDataInteractor.ColdRvmLeadProcessor coldRvmLeadProcessor =
                new LoadRawDataInteractor.ColdRvmLeadProcessor(coldRvmLeadMock, databaseStatus);

        //
        //Act
        //
        ColdRvmLead coldRvmLeadReturned = coldRvmLeadProcessor.apply(personFromDatabaseSpy, propertyFromDatabase);

        //
        //Assert
        //
        //Same objects should be returned
        assertThat(coldRvmLeadReturned).isEqualTo(coldRvmLeadMock);

        //Add the database property
        verify(coldRvmLeadMock, Mockito.times(1)).setProperty(propertyFromDatabase);

        //Add the database person
        verify(coldRvmLeadMock, Mockito.times(1)).setPerson(personFromDatabaseSpy);

        //Add property not called
        verify(personFromDatabaseSpy, Mockito.times(0)).addProperty(any());

        //No new property added
        assertThat(personFromDatabaseSpy.getPropertyList().size()).isEqualTo(1);

        //test databaseStatus
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isTrue();
        assertThat(databaseStatus.duplicateProperty).isTrue();
    }

    @Test
    public void ColdRvmLeadProcessor_PropertyExistsPersonExists_WithoutProperty() throws Exception {
        //
        //Arrange
        //
        Address personAddress = new Address(
                "123 Ave person",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Person personFromDatabase = new Person(
                "Dan",
                "Leo",
                personAddress
        );

        Address propertyAddress = new Address(
                "123 Ave property",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Property propertyFromDatabase = new Property(
                "APN#12345",
                propertyAddress
        );

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();

        ColdRvmLead coldRvmLeadMock = Mockito.mock(ColdRvmLead.class);

        when(coldRvmLeadMock.getProperty()).thenReturn(propertyFromDatabase);

        LoadRawDataInteractor.ColdRvmLeadProcessor coldRvmLeadProcessor =
                new LoadRawDataInteractor.ColdRvmLeadProcessor(coldRvmLeadMock, databaseStatus);

        //
        //Act
        //
        ColdRvmLead coldRvmLeadReturned = coldRvmLeadProcessor.apply(personFromDatabase, propertyFromDatabase);

        //
        //Assert
        //
        //Same objects should be returned
        assertThat(coldRvmLeadReturned).isEqualTo(coldRvmLeadMock);

        //Add the database property
        verify(coldRvmLeadMock, Mockito.times(1)).setProperty(propertyFromDatabase);

        //Add the database person
        verify(coldRvmLeadMock, Mockito.times(1)).setPerson(personFromDatabase);

        //new property added
        assertThat(personFromDatabase.getPropertyList().size()).isEqualTo(1);

        //test databaseStatus
        assertThat(databaseStatus.newColdRvmLeadSaved).isFalse();
        assertThat(databaseStatus.duplicateColdRvmEntry).isFalse();
        assertThat(databaseStatus.duplicatePerson).isTrue();
        assertThat(databaseStatus.duplicateProperty).isTrue();
    }

    @Test
    public void LoadRawLeadsObservable_LoadColdLead_Success() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;

        //Mocks Bitch
        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMapMock = Mockito.mock(LoadRawDataInteractor.StoreRawLeadFlatMap.class);
        LoadRawDataInteractor.StatusCounterScanner databaseStatusCounterScannerMock = Mockito.mock((LoadRawDataInteractor.StatusCounterScanner.class));
        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMapMock = Mockito.mock((LoadRawDataInteractor.ProcessRawLeadErrorFlatMap.class));

        //Data Bitch
        RawLead rawLead = new RawLeadImplTest();
        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        LoadRawDataInteractor.StatusCounter databaseStatusCounter = new LoadRawDataInteractor.StatusCounter();
        databaseStatusCounter.numberOfColdLeadsAdded = 1;

        //When it up
        when(csvFileControllerMock.readRawLeads(any())).thenReturn(Observable.just(rawLead));

        when(storeRawLeadFlatMapMock.apply(rawLead)).thenReturn(Observable.just(databaseStatus));

        when(loadRawLeadsActionMock.getCsvFile()).thenReturn(new File("Do Nothing"));

        when(databaseStatusCounterScannerMock.apply(any(), any())).thenReturn(databaseStatusCounter);

        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservable
                = new LoadRawDataInteractor.LoadRawLeadsObservable(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsActionMock,
                storeRawLeadFlatMapMock,
                processRawLeadErrorFlatMapMock,
                databaseStatusCounterScannerMock
        );

        //
        //Act
        //
        testObserver = Single.fromObservable(Observable.create(loadRawLeadsObservable)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        LoadRawLeadsResult loadRawLeadsResult = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(0);
        assertThat(loadRawLeadsResult.getType()).isEqualTo(Result.ResultType.SUCCESS);
        assertThat(loadRawLeadsResult.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult.getErrorMessage()).isNullOrEmpty();
        assertThat(loadRawLeadsResult.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadsSaved()).isEqualTo(1);
    }

    @Test
    public void LoadRawLeadsObservable_LoadColdLead_Success_MultipleEntries() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;

        //Mocks Bitch
        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMapMock = Mockito.mock(LoadRawDataInteractor.StoreRawLeadFlatMap.class);
        LoadRawDataInteractor.StatusCounterScanner statusCounterScannerMock = Mockito.mock((LoadRawDataInteractor.StatusCounterScanner.class));
        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMapMock = Mockito.mock((LoadRawDataInteractor.ProcessRawLeadErrorFlatMap.class));

        //Data Bitch
        RawLead rawLead = new RawLeadImplTest();
        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        LoadRawDataInteractor.StatusCounter statusCounter = new LoadRawDataInteractor.StatusCounter();
        statusCounter.numberOfColdLeadsAdded = 2;

        //When it up
        //Return two leads ...
        when(csvFileControllerMock.readRawLeads(any())).thenReturn(Observable.just(rawLead, rawLead));

        when(storeRawLeadFlatMapMock.apply(rawLead)).thenReturn(Observable.just(databaseStatus));

        when(loadRawLeadsActionMock.getCsvFile()).thenReturn(new File("Do Nothing"));

        when(statusCounterScannerMock.apply(any(), any())).thenReturn(statusCounter);

        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservable
                = new LoadRawDataInteractor.LoadRawLeadsObservable(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsActionMock,
                storeRawLeadFlatMapMock,
                processRawLeadErrorFlatMapMock,
                statusCounterScannerMock
        );

        //
        //Act
        //
        testObserver = Single.fromObservable(Observable.create(loadRawLeadsObservable)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        //verify scanner was called twice
        verify(statusCounterScannerMock, Mockito.times(2)).apply(any(), any());

        LoadRawLeadsResult loadRawLeadsResult = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(0);
        assertThat(loadRawLeadsResult.getType()).isEqualTo(Result.ResultType.SUCCESS);
        assertThat(loadRawLeadsResult.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult.getErrorMessage()).isNullOrEmpty();
        assertThat(loadRawLeadsResult.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadsSaved()).isEqualTo(2);
    }

    @Test
    public void LoadRawLeadsObservable_LoadColdLead_Error() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;
        Throwable throwable = new Throwable("Error capturing CSV header");

        //Mocks Bitch
        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMapMock = Mockito.mock(LoadRawDataInteractor.StoreRawLeadFlatMap.class);
        LoadRawDataInteractor.StatusCounterScanner databaseStatusCounterScannerMock = Mockito.mock((LoadRawDataInteractor.StatusCounterScanner.class));
        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMapMock = Mockito.mock((LoadRawDataInteractor.ProcessRawLeadErrorFlatMap.class));

        //Data Bitch
        RawLead rawLead = new RawLeadImplTest();
        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        LoadRawDataInteractor.StatusCounter databaseStatusCounter = new LoadRawDataInteractor.StatusCounter();
        databaseStatusCounter.numberOfColdLeadsAdded = 2;

        //When it up
        //Return two leads ...
        when(csvFileControllerMock.readRawLeads(any())).thenReturn(Observable.error(throwable));

        when(storeRawLeadFlatMapMock.apply(rawLead)).thenReturn(Observable.just(databaseStatus));

        when(loadRawLeadsActionMock.getCsvFile()).thenReturn(new File("Do Nothing"));

        when(databaseStatusCounterScannerMock.apply(any(), any())).thenReturn(databaseStatusCounter);

        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservable
                = new LoadRawDataInteractor.LoadRawLeadsObservable(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsActionMock,
                storeRawLeadFlatMapMock,
                processRawLeadErrorFlatMapMock,
                databaseStatusCounterScannerMock
        );

        //
        //Act
        //
        testObserver = Single.fromObservable(Observable.create(loadRawLeadsObservable)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertValueCount(0);
        testObserver.assertError(throwable);

        //verify scanner was called twice
        verify(storeRawLeadFlatMapMock, Mockito.times(0)).apply(any());
        verify(databaseStatusCounterScannerMock, Mockito.times(0)).apply(any(), any());
    }

    @Test
    public void LoadRawLeadsObservable_LoadColdLead_RawLeadError() throws Exception {
        /*
        Here we test you loaded the file correctly the but the RawLead was malformed in the file. Here we return
        RawLead isError = true.
         */

        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;

        //Mocks Bitch
        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        LoadRawDataInteractor.StoreRawLeadFlatMap storeRawLeadFlatMapMock = Mockito.mock(LoadRawDataInteractor.StoreRawLeadFlatMap.class);
        LoadRawDataInteractor.StatusCounterScanner statusCounterScannerMock = Mockito.mock((LoadRawDataInteractor.StatusCounterScanner.class));
        LoadRawDataInteractor.ProcessRawLeadErrorFlatMap processRawLeadErrorFlatMapMock = Mockito.mock((LoadRawDataInteractor.ProcessRawLeadErrorFlatMap.class));

        //Data Bitch
        final String ERROR_MSG = "OMG I have a problem!";
        RawLead rawLead = new RawLeadErrorImpl(ERROR_MSG);

        LoadRawDataInteractor.CsvStatus csvStatus = new LoadRawDataInteractor.CsvStatus();
        csvStatus.error = true;
        csvStatus.errorMessage = ERROR_MSG;

        LoadRawDataInteractor.StatusCounter statusCounter = new LoadRawDataInteractor.StatusCounter();
        statusCounter.errorMessage = ERROR_MSG;
        statusCounter.numberOfErrors = 1;

        //When it up
        when(csvFileControllerMock.readRawLeads(any())).thenReturn(Observable.just(rawLead));

        when(processRawLeadErrorFlatMapMock.apply(rawLead)).thenReturn(Observable.just(csvStatus));

        when(loadRawLeadsActionMock.getCsvFile()).thenReturn(new File("Do Nothing"));

        when(statusCounterScannerMock.apply(any(), any())).thenReturn(statusCounter);

        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservable
                = new LoadRawDataInteractor.LoadRawLeadsObservable(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsActionMock,
                storeRawLeadFlatMapMock,
                processRawLeadErrorFlatMapMock,
                statusCounterScannerMock
        );

        //
        //Act
        //
        testObserver = Single.fromObservable(Observable.create(loadRawLeadsObservable)).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        LoadRawLeadsResult loadRawLeadsResult = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(0);
        assertThat(loadRawLeadsResult.getType()).isEqualTo(Result.ResultType.SUCCESS);
        assertThat(loadRawLeadsResult.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult.getErrorMessage()).isEqualToIgnoringCase(ERROR_MSG);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadErrors()).isEqualTo(1);
        assertThat(loadRawLeadsResult.getNumberOfColdLeadsSaved()).isEqualTo(0);

        verify(storeRawLeadFlatMapMock, Mockito.times(0)).apply(rawLead);
    }

    @Test
    public void processLoadRawLeadsAction_success() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;

        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservableMock =
                Mockito.mock(LoadRawDataInteractor.LoadRawLeadsObservable.class);

        LoadRawDataInteractorChildTest loadRawDataInteractorChildTest = new LoadRawDataInteractorChildTest(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsObservableMock
        );

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                final Object[] args = invocation.getArguments();

                ObservableEmitter<LoadRawLeadsResult> emitter = (ObservableEmitter<LoadRawLeadsResult>) args[0];

                emitter.onNext(LoadRawLeadsResult.success(0,5,0, ""));
                emitter.onComplete();
                return null;
            }
        }).when(loadRawLeadsObservableMock).subscribe(any());

        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        //
        //Act
        //
        testObserver = loadRawDataInteractorChildTest.processLoadRawLeadsAction(loadRawLeadsActionMock).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);
        testObserver.assertComplete();

        //Get first result
        LoadRawLeadsResult loadRawLeadsResult_InProgress = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(0);
        assertThat(loadRawLeadsResult_InProgress.getType()).isEqualTo(Result.ResultType.IN_FLIGHT);
        assertThat(loadRawLeadsResult_InProgress.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult_InProgress.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult_InProgress.getErrorMessage()).isNullOrEmpty();
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadsSaved()).isEqualTo(0);

        LoadRawLeadsResult loadRawLeadsResult_Success = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(1);
        assertThat(loadRawLeadsResult_Success.getType()).isEqualTo(Result.ResultType.SUCCESS);
        assertThat(loadRawLeadsResult_Success.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult_Success.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult_Success.getErrorMessage()).isNullOrEmpty();
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadsSaved()).isEqualTo(5);
    }

    @Test
    public void processLoadRawLeadsAction_error() throws Exception {
        //
        //Arrange
        //
        TestObserver<LoadRawLeadsResult> testObserver;

        CsvFileController csvFileControllerMock = Mockito.mock(CsvFileController.class);
        DatabaseGateway databaseGatewayMock = Mockito.mock(DatabaseGateway.class);
        LoadRawDataInteractor.LoadRawLeadsObservable loadRawLeadsObservableMock =
                Mockito.mock(LoadRawDataInteractor.LoadRawLeadsObservable.class);

        LoadRawDataInteractorChildTest loadRawDataInteractorChildTest = new LoadRawDataInteractorChildTest(
                csvFileControllerMock,
                databaseGatewayMock,
                loadRawLeadsObservableMock
        );

        final String errorMessage = "Throw an Error Fool!";

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                final Object[] args = invocation.getArguments();

                ObservableEmitter<LoadRawLeadsResult> emitter = (ObservableEmitter<LoadRawLeadsResult>) args[0];

                emitter.onError(new Throwable(errorMessage));
                return null;
            }
        }).when(loadRawLeadsObservableMock).subscribe(any());

        LoadRawLeadsAction loadRawLeadsActionMock = Mockito.mock(LoadRawLeadsAction.class);
        when(loadRawLeadsActionMock.getCsvFile()).thenReturn(null);

        //
        //Act
        //
        testObserver = loadRawDataInteractorChildTest.processLoadRawLeadsAction(loadRawLeadsActionMock).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);
        testObserver.assertComplete();

        //Get first result
        LoadRawLeadsResult loadRawLeadsResult_InProgress = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(0);
        assertThat(loadRawLeadsResult_InProgress.getType()).isEqualTo(Result.ResultType.IN_FLIGHT);
        assertThat(loadRawLeadsResult_InProgress.isFileLoadError()).isFalse();
        assertThat(loadRawLeadsResult_InProgress.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult_InProgress.getErrorMessage()).isNullOrEmpty();
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult_InProgress.getNumberOfColdLeadsSaved()).isEqualTo(0);

        LoadRawLeadsResult loadRawLeadsResult_Success = (LoadRawLeadsResult) testObserver.getEvents().get(0).get(1);
        assertThat(loadRawLeadsResult_Success.getType()).isEqualTo(Result.ResultType.FAILURE);
        assertThat(loadRawLeadsResult_Success.isFileLoadError()).isTrue();
        assertThat(loadRawLeadsResult_Success.isUserCanceled()).isFalse();
        assertThat(loadRawLeadsResult_Success.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadDuplicates()).isEqualTo(0);
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadErrors()).isEqualTo(0);
        assertThat(loadRawLeadsResult_Success.getNumberOfColdLeadsSaved()).isEqualTo(0);
    }

    static class LoadRawDataInteractorChildTest extends LoadRawDataInteractor {
        private final LoadRawLeadsObservable loadRawLeadsObservable;

        /**
         * Constructor
         *
         * @param csvFileController - CSV File Reader
         * @param databaseGateway
         */
        public LoadRawDataInteractorChildTest(CsvFileController csvFileController, DatabaseGateway databaseGateway,
                                           LoadRawLeadsObservable loadRawLeadsObservable) {
            super(csvFileController, databaseGateway);
            this.loadRawLeadsObservable = loadRawLeadsObservable;
        }

        @VisibleForTesting
        protected LoadRawLeadsObservable getLoadRawLeadsObservable(LoadRawLeadsAction loadRawLeadsAction) {
            return this.loadRawLeadsObservable;
        }
    }

    class RawLeadImplTest extends RawLeadCsvImpl {

        @Override
        public String getAddress() {
            return "43 Foreston Street";
        }

        @Override
        public String getUnitNumber() {
            return "";
        }

        @Override
        public String getCity() {
            return "Manorville";
        }

        @Override
        public String getState() {
            return "Ny";
        }

        @Override
        public String getZip() {
            return "11949";
        }

        @Override
        public String getCounty() {
            return "Suffolk";
        }

        @Override
        public String getAPN() {
            return "12345";
        }

        @Override
        public boolean isOwnerOccupied() {
            return true;
        }

        @Override
        public String getOwnerOneFirstName() {
            return "Daniel";
        }

        @Override
        public String getOwnerOneLastName() {
            return "Leonardis";
        }

        @Override
        public String getCompanyName() {
            return "Bean Back LLC";
        }

        @Override
        public String getCompanyAddress() {
            return "41 Foreston Circle";
        }

        @Override
        public String getOwnerTwoFirstName() {
            return "Kathryn";
        }

        @Override
        public String getOwnerTwoLastName() {
            return "Leonardis";
        }

        @Override
        public String getMailingCareOfName() {
            return "N/A";
        }

        @Override
        public String getMailingAddress() {
            return "150 whalers cove";
        }

        @Override
        public String getMailingUnitNumber() {
            return "";
        }

        @Override
        public String getMailingCity() {
            return "Babylon";
        }

        @Override
        public String getMailingState() {
            return "NY";
        }

        @Override
        public String getMailingZip() {
            return "11702";
        }

        @Override
        public String getMailingCounty() {
            return "Suffolk";
        }

        @Override
        public String getDoNotMail() {
            return "";
        }

        @Override
        public String getPropertyType() {
            return "";
        }

        @Override
        public float getBedrooms() {
            return 0;
        }

        @Override
        public float getTotalBathrooms() {
            return 0;
        }

        @Override
        public int getBuildingSqft() {
            return 0;
        }

        @Override
        public int getLotSizeSqft() {
            return 0;
        }

        @Override
        public int getEffectiveYearBuilt() {
            return 0;
        }

        @Override
        public int getTotalAssessedValue() {
            return 0;
        }

        @Override
        public Date getLastSaleRecordingDate() {
            return null;
        }

        @Override
        public int getLastSaleAmount() {
            return 0;
        }

        @Override
        public int getTotalOpenLoans() {
            return 0;
        }

        @Override
        public int getEstRemainingBalanceOfOpenLoans() {
            return 0;
        }

        @Override
        public int getEstValue() {
            return 0;
        }

        @Override
        public float getEstLoanToValue() {
            return 0;
        }

        @Override
        public int getEstEquity() {
            return 0;
        }

        @Override
        public String getMLSStatus() {
            return "";
        }

        @Override
        public Date getMLSDate() {
            return new Date();
        }

        @Override
        public int getMLSAmount() {
            return 0;
        }

        @Override
        public int getLienAmount() {
            return 0;
        }

        @Override
        public int getMarketingLists() {
            return 0;
        }

        @Override
        public Date getDateAddedToList() {
            return new Date();
        }
    }
}