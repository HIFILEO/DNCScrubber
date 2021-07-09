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

import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
    public void DatabaseStatusCounterScanner_addDuplicate() throws Exception {
        //
        //Arrange
        //
        LoadRawDataInteractor.DatabaseStatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.DatabaseStatusCounterScanner();

        LoadRawDataInteractor.DatabaseStatusCounter databaseStatusCounter =
                new LoadRawDataInteractor.DatabaseStatusCounter();
        databaseStatusCounter.numberOfColdLeadDuplicates = 10;
        databaseStatusCounter.numberOfColdLeadsAdded = 9;

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        databaseStatus.duplicateColdRvmEntry = true;

        //
        //Act
        //
        LoadRawDataInteractor.DatabaseStatusCounter counterToTest =
                databaseStatusCounterScanner.apply(databaseStatusCounter, databaseStatus);

        //
        //Assert
        //
        assertThat(counterToTest.numberOfColdLeadDuplicates).isEqualTo(11);
        assertThat(counterToTest.numberOfColdLeadsAdded).isEqualTo(9);
    }

    @Test
    public void DatabaseStatusCounterScanner_addNew() throws Exception {
        //
        //Arrange
        //
        LoadRawDataInteractor.DatabaseStatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.DatabaseStatusCounterScanner();

        LoadRawDataInteractor.DatabaseStatusCounter databaseStatusCounter =
                new LoadRawDataInteractor.DatabaseStatusCounter();
        databaseStatusCounter.numberOfColdLeadDuplicates = 10;
        databaseStatusCounter.numberOfColdLeadsAdded = 9;

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        databaseStatus.duplicateColdRvmEntry = false;
        databaseStatus.newColdRvmLeadSaved = true;

        //
        //Act
        //
        LoadRawDataInteractor.DatabaseStatusCounter counterToTest =
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

        RawLead rawLead = new RawLeadImpl();
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

        RawLead rawLead = new RawLeadImpl();
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

        RawLead rawLead = new RawLeadImpl();
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
    public void ColdRvmLeadProcessor_databaseEmpty() throws Exception {
        //
        //Arrange
        //
        Person personFromDatabase = new Person();
        Property propertyFromDatabase = new Property();

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
        Address address = new Address();
        address.setMailingAddress("123 Ave");

        Person personFromDatabase = new Person();

        Property propertyFromDatabase = new Property();
        propertyFromDatabase.setAddress(address);
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
        Address address = new Address();
        address.setMailingAddress("123 Ave");

        Property propertyFromDatabase = new Property();
        propertyFromDatabase.setAddress(address);

        Person personFromDatabase2 = new Person();
        personFromDatabase2.setFirstName("Dan");
        personFromDatabase2.setLastName("Leo");
        personFromDatabase2.setAddress(address);
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
        Address address = new Address();
        address.setMailingAddress("123 Ave");

        Person personFromDatabase = new Person();
        personFromDatabase.setFirstName("Dan");
        personFromDatabase.setLastName("Leo");
        personFromDatabase.setAddress(address);

        Property propertyFromDatabase = new Property();
        propertyFromDatabase.setAddress(address);

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

    class RawLeadImpl implements RawLead {

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