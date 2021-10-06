package com.LEO.DNCScrubber.Scrubber.gateway;

import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDao;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.util.HibernateTest;
import io.reactivex.observers.TestObserver;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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

class DatabaseGatewayImplTest extends HibernateTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void writeColdRvmLead_success() {
        //
        //Arrange
        //
        TestObserver<Boolean> testObserver;

        Property property = createProperty();

        Person person = createPerson();
        person.addProperty(property);

        ColdRvmLead coldRvmLead = createColdRvmLead();
        coldRvmLead.setPerson(person);
        coldRvmLead.setProperty(property);

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        //
        //Act
        //
        testObserver = databaseGateway.writeColdRvmLead(coldRvmLead).test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        assertThat(testObserver.getEvents().get(0).get(0)).isEqualTo(true);

        //Load from DB directly ...make sure that stuff is in there
        TestObserver<ColdRvmLead> testObserver2 =
                databaseGateway.loadColdRvmLeadByNaturalId(coldRvmLead.getNaturalId()).test();
        ColdRvmLead coldRvmLeadToTest = (ColdRvmLead) testObserver2.getEvents().get(0).get(0);

        assertThat(coldRvmLead.getNaturalId()).isEqualToIgnoringCase(coldRvmLeadToTest.getNaturalId());
        assertThat(coldRvmLead.getDateWorkflowStarted()).isInSameDayAs(coldRvmLeadToTest.getDateWorkflowStarted());
        assertThat(coldRvmLead.isConversationStarted()).isEqualTo(coldRvmLeadToTest.isConversationStarted());
        assertThat(coldRvmLead.isToldToStop()).isEqualTo(coldRvmLeadToTest.isToldToStop());
        assertThat(coldRvmLead.isSold()).isEqualTo(coldRvmLeadToTest.isSold());
        assertThat(coldRvmLead.isWrongNumber()).isEqualTo(coldRvmLeadToTest.isWrongNumber());
        assertThat(coldRvmLead.isOfferMade()).isEqualTo(coldRvmLeadToTest.isOfferMade());
        assertThat(coldRvmLead.isLeadSentToAgent()).isEqualTo(coldRvmLeadToTest.isLeadSentToAgent());

        //Person
        Person personToTest = coldRvmLeadToTest.getPerson();
        assertThat(person.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        assertThat(person.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(person.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(person.getAddress().getMailingAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(person.getAddress().getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(person.getAddress().getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(person.getAddress().getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(person.getAddress().getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(person.getAddress().getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(person.getAddress().getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        //phone 1
        assertThat(person.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(person.getPhone1().getPhoneType()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(person.getPhone1().isPhoneDNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(person.getPhone1().isPhoneStop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(person.getPhone1().isPhoneLitigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(person.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        //phone 2
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();

        //phone 3
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();

        assertThat(person.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(person.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(person.getEmail3()).isEqualToIgnoringCase(personToTest.getEmail3());

        //Property
        Property propertyToTest = coldRvmLeadToTest.getProperty();
        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());

    }

    @Test
    public void writeColdRvmLead_exception() {
        //
        //Arrange
        //
        TestObserver<Boolean> testObserver;

        Property property = createProperty();

        Person person = createPerson();
        person.addProperty(property);

        ColdRvmLead coldRvmLead = createColdRvmLead();
        coldRvmLead.setPerson(person);
        coldRvmLead.setProperty(property);

        DatabaseHelper databaseHelperMock = Mockito.mock(DatabaseHelper.class);
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelperMock);

        RuntimeException runtimeException = new RuntimeException("Throw This Exception");
        when(databaseHelperMock.saveColdRvmLead(any(), any())).thenThrow(runtimeException);

        //
        //Act
        //
        testObserver = databaseGateway.writeColdRvmLead(coldRvmLead).test();

        //
        //Assert
        //
        testObserver.assertValueCount(0);
        testObserver.assertNotComplete();
        testObserver.assertError(runtimeException);
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnZeroWhenEmptyDatabase() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelperMock = Mockito.mock(DatabaseHelper.class);
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelperMock);

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.isEmpty()).isTrue();
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnZeroWhenPhone1NotNull() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);


        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.isEmpty()).isTrue();
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnZeroWhenPhone2NotNull() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        person.setPhone2(person.getPhone1());
        person.setPhone1(null);


        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.isEmpty()).isTrue();
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnZeroWhenPhone3NotNull() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        person.setPhone3(person.getPhone1());
        person.setPhone1(null);


        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.isEmpty()).isTrue();
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnZeroWhenAllPhonesNotNull() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        person.setPhone2(person.getPhone1());
        person.setPhone3(person.getPhone1());


        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.isEmpty()).isTrue();
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnOne() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        //no phone numbers in test
        person.setPhone1(null);

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);

        Person personToTest = personList.get(0);
        assertThat(person.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        assertThat(person.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(person.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(person.getAddress().getMailingAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(person.getAddress().getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(person.getAddress().getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(person.getAddress().getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(person.getAddress().getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(person.getAddress().getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(person.getAddress().getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        //phone 1
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();

        //phone 2
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();

        //phone 3
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();

        assertThat(person.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(person.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(person.getEmail3()).isEqualToIgnoringCase(personToTest.getEmail3());

        //Property
        Property propertyToTest = personToTest.getPropertyList().get(0);

        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_returnOneWhenTwo() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);


        Person person2 = new Person("Doug", "Funny", person.getAddress());
        person2.addProperty(property);
        person2.setPhone1(person.getPhone1());
        person.setPhone1(null);


        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        databaseHelper.savePerson(session, person2);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        Person personToTest = personList.get(0);
        assertThat(person.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        assertThat(person.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(person.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(person.getAddress().getMailingAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(person.getAddress().getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(person.getAddress().getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(person.getAddress().getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(person.getAddress().getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(person.getAddress().getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(person.getAddress().getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        //phone 1
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();
        assertThat(person.getPhone1()).isNull();

        //phone 2
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();

        //phone 3
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();

        assertThat(person.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(person.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(person.getEmail3()).isEqualToIgnoringCase(personToTest.getEmail3());

        //Property
        Property propertyToTest = personToTest.getPropertyList().get(0);

        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());
    }

    @Test
    public void loadPersonsWithNoPhoneNumber_return1000When1000() {
        //
        //Arrange
        //
        TestObserver<List<Person>> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        //Add Person To Database - (to avoid the selects which slow down debugging, I'm going right into db)
        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        person.setPhone1(null);

        Transaction transaction = session.beginTransaction();
        PersonDao personDao = databaseHelper.translatePersonToPersonDb(session, person);
        session.saveOrUpdate(personDao);

        for (int i = 1; i < 1000; i++) {
            PersonDao personDaoToSave = new PersonDao();
            personDaoToSave.setFirstName(personDao.getFirstName() + " - " + i);
            personDaoToSave.setLastName(personDao.getLastName() + " - " + i);
            personDaoToSave.setAddress(personDao.getAddress() + " - " + i);
            personDaoToSave.setNaturalId(personDao.getNaturalId() + " - " + i);
            session.saveOrUpdate(personDaoToSave);
        }
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonsWithNoPhoneNumber().test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        @SuppressWarnings("unchecked")
        List<Person> personList = (List<Person>) testObserver.getEvents().get(0).get(0);
        assertThat(personList.size()).isEqualTo(1000);
    }

    @Test
    public void loadPropertyByNaturalId_empty_whenDatabaseEmpty() {
        //
        //Arrange
        //
        TestObserver<Property> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        //
        //Act
        //
        testObserver = databaseGateway.loadPropertyByNaturalId("FAKEID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadPropertyByNaturalId_empty_whenIdDoNotMatch() {
        //
        //Arrange
        //
        TestObserver<Property> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.saveProperty(session, property);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPropertyByNaturalId("FAKEID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadPropertyByNaturalId_oneMatch() {
        //
        //Arrange
        //
        TestObserver<Property> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.saveProperty(session, property);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPropertyByNaturalId(property.getNaturalId()).test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        Property propertyToTest = (Property) testObserver.getEvents().get(0).get(0);
        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());
    }

    @Test
    public void loadPersonByNaturalId_empty_whenDatabaseEmpty() {
        //
        //Arrange
        //
        TestObserver<Person> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonByNaturalId("FAKEID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadPersonByNaturalId_empty_whenIdDoNotMatch() {
        //
        //Arrange
        //
        TestObserver<Person> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonByNaturalId("FAKE-ID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadPersonByNaturalId_oneMatch() {
        //
        //Arrange
        //
        TestObserver<Person> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, person);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadPersonByNaturalId(person.getNaturalId()).test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        Person personToTest = (Person) testObserver.getEvents().get(0).get(0);
        assertThat(person.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        assertThat(person.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(person.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(person.getAddress().getMailingAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(person.getAddress().getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(person.getAddress().getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(person.getAddress().getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(person.getAddress().getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(person.getAddress().getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(person.getAddress().getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        //phone 1
        assertThat(person.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(person.getPhone1().getPhoneType()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(person.getPhone1().isPhoneDNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(person.getPhone1().isPhoneStop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(person.getPhone1().isPhoneLitigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(person.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        //phone 2
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();

        //phone 3
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();

        assertThat(person.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(person.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(person.getEmail3()).isEqualToIgnoringCase(personToTest.getEmail3());

        //Property
        Property propertyToTest = personToTest.getPropertyList().get(0);
        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());
    }

    @Test
    public void loadColdRvmLeadByNaturalId_empty_whenDatabaseEmpty() {
        //
        //Arrange
        //
        TestObserver<ColdRvmLead> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        //
        //Act
        //
        testObserver = databaseGateway.loadColdRvmLeadByNaturalId("FAKEID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadColdRvmLeadByNaturalId_empty_whenIdDoNotMatch() {
        //
        //Arrange
        //
        TestObserver<ColdRvmLead> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        ColdRvmLead coldRvmLead = createColdRvmLead();
        coldRvmLead.setProperty(property);
        coldRvmLead.setPerson(person);

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.saveColdRvmLead(session, coldRvmLead);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadColdRvmLeadByNaturalId("FAKE-ID").test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(0);
        testObserver.assertComplete();
    }

    @Test
    public void loadColdRvmLeadByNaturalId_empty_oneMatch() {
        //
        //Arrange
        //
        TestObserver<ColdRvmLead> testObserver;

        DatabaseHelper databaseHelper = new DatabaseHelper();
        DatabaseGatewayImpl databaseGateway = new DatabaseGatewayImpl(hibernateUtil, databaseHelper);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);
        ColdRvmLead coldRvmLead = createColdRvmLead();
        coldRvmLead.setProperty(property);
        coldRvmLead.setPerson(person);

        //Add Person To Database
        Transaction transaction = session.beginTransaction();
        databaseHelper.saveColdRvmLead(session, coldRvmLead);
        transaction.commit();

        //
        //Act
        //
        testObserver = databaseGateway.loadColdRvmLeadByNaturalId(coldRvmLead.getNaturalId()).test();

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();

        ColdRvmLead coldRvmLeadToTest = (ColdRvmLead) testObserver.getEvents().get(0).get(0);

        assertThat(coldRvmLead.getNaturalId()).isEqualToIgnoringCase(coldRvmLeadToTest.getNaturalId());
        assertThat(coldRvmLead.getDateWorkflowStarted()).isInSameDayAs(coldRvmLeadToTest.getDateWorkflowStarted());
        assertThat(coldRvmLead.isConversationStarted()).isEqualTo(coldRvmLeadToTest.isConversationStarted());
        assertThat(coldRvmLead.isToldToStop()).isEqualTo(coldRvmLeadToTest.isToldToStop());
        assertThat(coldRvmLead.isSold()).isEqualTo(coldRvmLeadToTest.isSold());
        assertThat(coldRvmLead.isWrongNumber()).isEqualTo(coldRvmLeadToTest.isWrongNumber());
        assertThat(coldRvmLead.isOfferMade()).isEqualTo(coldRvmLeadToTest.isOfferMade());
        assertThat(coldRvmLead.isLeadSentToAgent()).isEqualTo(coldRvmLeadToTest.isLeadSentToAgent());

        //Person
        Person personToTest = coldRvmLeadToTest.getPerson();
        assertThat(person.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        assertThat(person.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(person.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(person.getAddress().getMailingAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(person.getAddress().getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(person.getAddress().getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(person.getAddress().getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(person.getAddress().getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(person.getAddress().getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(person.getAddress().getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        //phone 1
        assertThat(person.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(person.getPhone1().getPhoneType()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(person.getPhone1().isPhoneDNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(person.getPhone1().isPhoneStop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(person.getPhone1().isPhoneLitigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(person.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        //phone 2
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();
        assertThat(person.getPhone2()).isNull();

        //phone 3
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();
        assertThat(person.getPhone3()).isNull();

        assertThat(person.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(person.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(person.getEmail3()).isEqualToIgnoringCase(personToTest.getEmail3());

        //Property
        Property propertyToTest = coldRvmLeadToTest.getProperty();
        assertThat(propertyToTest.getNaturalId()).isEqualToIgnoringCase(property.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(property.getAddress().getMailingAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(property.getAddress().getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(property.getAddress().getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(property.getAddress().getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(property.getAddress().getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(property.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(property.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(property.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(property.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(property.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(property.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(property.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(property.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(property.getLotSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(property.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(property.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(property.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isInSameDayAs(property.getMlsDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(property.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isInSameDayAs(property.getDateAddedToList());
    }

    private ColdRvmLead createColdRvmLead() {
        ColdRvmLead coldRvmLead = new ColdRvmLead(createPerson(), createProperty());
        coldRvmLead.setDateWorkflowStarted(new Date());
        coldRvmLead.setToldToStop(false);
        coldRvmLead.setSold(false);
        coldRvmLead.setWrongNumber(false);
        coldRvmLead.setOfferMade(true);
        coldRvmLead.setLeadSentToAgent(true);

        return coldRvmLead;
    }

    private Person createPerson() {
        Address personAddress = new Address(
                "123 Ave person",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Person person = new Person(
                "Dan",
                "Leo",
                personAddress
        );

        Phone phone1 = new Phone();
        phone1.setPhoneDNC(false);
        phone1.setPhoneLitigation(false);
        phone1.setPhoneNumber("(959) 123-3677");
        phone1.setPhoneStop(false);
        phone1.setPhoneType("Home");
        phone1.setPhoneTelco("Verizon");
        person.setPhone1(phone1);

        person.setEmail1("testing@testing.com");
        person.setEmail2("testingsomemore@testing.com");

        return person;
    }

    private Property createProperty() {
        Address propertyAddress = new Address(
                "123 Ave property",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Property property = new Property(
                "APN#12345",
                propertyAddress
        );

        property.setOwnerOccupied(true);
        property.setCompanyName("The bacon company, LLC");
        property.setCompanyAddress("1234 bacon way");
        property.setPropertyType("Multi");
        property.setBedrooms("5");
        property.setTotalBathrooms("1");
        property.setSqft(2000);
        property.setLotSizeSqft(50000);
        property.setYearBuilt(1910);
        property.setAssessedValue(1000000);
        property.setLastSaleRecordingDate(new Date());
        property.setLastSaleAmount(500000);
        property.setTotalOpenLoans(1);
        property.setEstimatedRemainingBalance(50000);
        property.setEstimatedValue(900000);
        property.setEstimatedEquity(100000);
        property.setmLSStatus("Listed");
        property.setMlsDate(new Date());
        property.setmLSAmount("1000000");
        property.setLienAmount("12345");
        property.setDateAddedToList(new Date());

        return property;
    }
}