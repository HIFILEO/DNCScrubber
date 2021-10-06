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
package com.LEO.DNCScrubber.Scrubber.gateway;

import com.LEO.DNCScrubber.Scrubber.gateway.model.ColdRvmLeadDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PropertyDao;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;

import com.LEO.DNCScrubber.util.HibernateTest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Note - to truly test a DAO, you have to write a test against the DB. If you mock everything, you never know
 * if your DAO is set up correct. Although slow, this ensures accuracy.
 */
class DatabaseHelperTest extends HibernateTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void saveColdRvmLead_newColdRvmLead() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();
        ColdRvmLead coldRvmLeadToTest = createColdRvmLead();
        Person personToTest = createPerson();
        Property propertyToTest = createProperty();

        personToTest.addProperty(propertyToTest);
        coldRvmLeadToTest.setPerson(personToTest);
        coldRvmLeadToTest.setProperty(propertyToTest);

        //
        //Act
        //
        Transaction transaction = session.beginTransaction();
        ColdRvmLeadDao coldRvmLeadDao = databaseHelper.saveColdRvmLead(session, coldRvmLeadToTest);
        transaction.commit();

        //
        //Assert
        //
        //ColdRvmLead check
        /* Most important part of test */
        assertThat(coldRvmLeadDao.getId()).isNotNull();

        assertThat(coldRvmLeadDao.getNaturalId()).isEqualToIgnoringCase(coldRvmLeadToTest.getNaturalId());
        assertThat(coldRvmLeadDao.getDateWorkflowStarted()).isEqualTo(coldRvmLeadToTest.getDateWorkflowStarted());
        assertThat(coldRvmLeadDao.isConversationStarted()).isEqualTo(coldRvmLeadToTest.isConversationStarted());
        assertThat(coldRvmLeadDao.isToldToStop()).isEqualTo(coldRvmLeadToTest.isToldToStop());
        assertThat(coldRvmLeadDao.isSold()).isEqualTo(coldRvmLeadToTest.isSold());
        assertThat(coldRvmLeadDao.isWrongNumber()).isEqualTo(coldRvmLeadToTest.isWrongNumber());
        assertThat(coldRvmLeadDao.isOfferMade()).isEqualTo(coldRvmLeadToTest.isOfferMade());
        assertThat(coldRvmLeadDao.isLeadSentToAgent()).isEqualTo(coldRvmLeadToTest.isLeadSentToAgent());

        //Person Check
        PersonDao personDao = coldRvmLeadDao.getPerson();
        assertThat(personDao.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        /* Most important part of test */
        assertThat(personDao.getId()).isNotNull();

        assertThat(personDao.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(personDao.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(personDao.getAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(personDao.getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(personDao.getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(personDao.getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(personDao.getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(personDao.getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(personDao.getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        assertThat(personDao.getPhone1()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(personDao.getPhone1Type()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(personDao.isPhone1DNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(personDao.isPhone1Stop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(personDao.isPhone1Litigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(personDao.getPhone1Telco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2Type()).isNullOrEmpty();
        assertThat(personDao.isPhone2DNC()).isFalse();
        assertThat(personDao.isPhone2Stop()).isFalse();
        assertThat(personDao.isPhone2Litigation()).isFalse();
        assertThat(personDao.getPhone2Telco()).isNullOrEmpty();

        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3Type()).isNullOrEmpty();
        assertThat(personDao.isPhone3DNC()).isFalse();
        assertThat(personDao.isPhone3Stop()).isFalse();
        assertThat(personDao.isPhone3Litigation()).isFalse();
        assertThat(personDao.getPhone3Telco()).isNullOrEmpty();

        assertThat(personDao.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(personDao.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(personDao.getEmail3()).isNullOrEmpty();

        //Property Check (from ColdRvmLead)
        PropertyDao propertyDao = coldRvmLeadDao.getProperty();
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getLastSaleRecordingDate()).isInSameDayAs(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getmLSDate()).isInSameDayAs(propertyToTest.getMlsDate());
        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getDateAddedToList()).isInSameDayAs(propertyToTest.getDateAddedToList());

    }

    @Test
    public void savePerson_newPersonInDb() throws Exception {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Person personToTest = createPerson();
        Property propertyToTest = createProperty();
        personToTest.addProperty(propertyToTest);

        Transaction transaction = session.beginTransaction();
        databaseHelper.savePerson(session, personToTest);
        transaction.commit();

        //
        //Act
        //
        Session localSession = hibernateUtil.getSessionFactory().openSession();
        transaction = localSession.beginTransaction();
        PersonDao personDao = databaseHelper.findPersonByNaturalId(localSession, personToTest.getNaturalId())
                .orElseThrow(() -> new Exception("Person not found in database - throw error "));
        transaction.commit();

        //
        //Assert
        //
        //Person Check
        assertThat(personDao.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        /* Most important part of test */
        assertThat(personDao.getId()).isNotNull();

        assertThat(personDao.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(personDao.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(personDao.getAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(personDao.getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(personDao.getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(personDao.getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(personDao.getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(personDao.getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(personDao.getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        assertThat(personDao.getPhone1()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(personDao.getPhone1Type()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(personDao.isPhone1DNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(personDao.isPhone1Stop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(personDao.isPhone1Litigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(personDao.getPhone1Telco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2Type()).isNullOrEmpty();
        assertThat(personDao.isPhone2DNC()).isFalse();
        assertThat(personDao.isPhone2Stop()).isFalse();
        assertThat(personDao.isPhone2Litigation()).isFalse();
        assertThat(personDao.getPhone2Telco()).isNullOrEmpty();

        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3Type()).isNullOrEmpty();
        assertThat(personDao.isPhone3DNC()).isFalse();
        assertThat(personDao.isPhone3Stop()).isFalse();
        assertThat(personDao.isPhone3Litigation()).isFalse();
        assertThat(personDao.getPhone3Telco()).isNullOrEmpty();

        assertThat(personDao.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(personDao.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(personDao.getEmail3()).isNullOrEmpty();

        //Property Check
        PropertyDao propertyDao = personDao.getProperties().get(0);
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getLastSaleRecordingDate()).isInSameDayAs(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getmLSDate()).isInSameDayAs(propertyToTest.getMlsDate());
        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getDateAddedToList()).isInSameDayAs(propertyToTest.getDateAddedToList());
    }

    @Test
    public void savePerson_newPerson() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Person personToTest = createPerson();
        Property propertyToTest = createProperty();
        personToTest.addProperty(propertyToTest);

        //
        //Act
        //
        Transaction transaction = session.beginTransaction();
        PersonDao personDao = databaseHelper.savePerson(session, personToTest);
        transaction.commit();

        //
        //Assert
        //
        //Person Check
        assertThat(personDao.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());

        /* Most important part of test */
        assertThat(personDao.getId()).isNotNull();

        assertThat(personDao.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(personDao.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(personDao.getAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(personDao.getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(personDao.getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(personDao.getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(personDao.getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(personDao.getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(personDao.getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        assertThat(personDao.getPhone1()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(personDao.getPhone1Type()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(personDao.isPhone1DNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(personDao.isPhone1Stop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(personDao.isPhone1Litigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(personDao.getPhone1Telco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2Type()).isNullOrEmpty();
        assertThat(personDao.isPhone2DNC()).isFalse();
        assertThat(personDao.isPhone2Stop()).isFalse();
        assertThat(personDao.isPhone2Litigation()).isFalse();
        assertThat(personDao.getPhone2Telco()).isNullOrEmpty();

        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3Type()).isNullOrEmpty();
        assertThat(personDao.isPhone3DNC()).isFalse();
        assertThat(personDao.isPhone3Stop()).isFalse();
        assertThat(personDao.isPhone3Litigation()).isFalse();
        assertThat(personDao.getPhone3Telco()).isNullOrEmpty();

        assertThat(personDao.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(personDao.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(personDao.getEmail3()).isNullOrEmpty();

        //Property Check
        PropertyDao propertyDao = personDao.getProperties().get(0);
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getLastSaleRecordingDate()).isInSameDayAs(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getmLSDate()).isInSameDayAs(propertyToTest.getMlsDate());
        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getDateAddedToList()).isInSameDayAs(propertyToTest.getDateAddedToList());
    }

    @Test
    public void saveProperty_newProperty() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Property propertyToTest = createProperty();

        //
        //Act
        //
        Transaction transaction = session.beginTransaction();
        PropertyDao propertyDao = databaseHelper.saveProperty(session, propertyToTest);
        transaction.commit();

        //
        //Assert
        //
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        /* Most important part of test - once a property is saved to DB, you get an ID! */
        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());
        assertThat(propertyDao.getAssessedValue()).isEqualTo(propertyToTest.getAssessedValue());

        assertThat(propertyDao.getLastSaleRecordingDate()).isEqualTo(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());
        assertThat(propertyDao.getmLSDate()).isEqualTo(propertyToTest.getMlsDate());

        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());
        assertThat(propertyDao.getDateAddedToList()).isEqualTo(propertyToTest.getDateAddedToList());
    }

    @Test
    public void saveProperty_newPropertyInDb() throws Exception {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Property propertyToTest = createProperty();

        Transaction transaction = session.beginTransaction();
        databaseHelper.saveProperty(session, propertyToTest);
        transaction.commit();

        //
        //Act
        //
        Session localSession = hibernateUtil.getSessionFactory().openSession();
        transaction = localSession.beginTransaction();
        PropertyDao propertyDao = databaseHelper.findPropertyByNaturalId(localSession, propertyToTest.getNaturalId())
                .orElseThrow(() -> new Exception("Property not found in database - throw error "));
        transaction.commit();

        //
        //Assert
        //
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        /* Most important part of test */
        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getLastSaleRecordingDate()).isInSameDayAs(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getmLSDate()).isInSameDayAs(propertyToTest.getMlsDate());
        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());

        /*remember - {@link PropertyDao:<<62>>} - you don't get time precision*/
        assertThat(propertyDao.getDateAddedToList()).isInSameDayAs(propertyToTest.getDateAddedToList());
    }

    @Test
    public void translatePropertyToPropertyDao_newProp() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelperSpy = spy(new DatabaseHelper());
        Property propertyToTest = createProperty();

        doReturn(Optional.empty()).when(databaseHelperSpy).findPropertyByNaturalId(any(), any());

        //
        //Act
        //
        PropertyDao propertyDao = databaseHelperSpy.translatePropertyToPropertyDao(session, propertyToTest);

        //
        //Assert
        //
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        assertThat(propertyDao.getId()).isNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());
        assertThat(propertyDao.getAssessedValue()).isEqualTo(propertyToTest.getAssessedValue());

        assertThat(propertyDao.getLastSaleRecordingDate()).isEqualTo(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());
        assertThat(propertyDao.getmLSDate()).isEqualTo(propertyToTest.getMlsDate());

        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());
        assertThat(propertyDao.getDateAddedToList()).isEqualTo(propertyToTest.getDateAddedToList());
    }

    @Test
    public void translatePropertyToPropertyDao_existingProp() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelperSpy = spy(new DatabaseHelper());

        Property propertyToTest = createProperty();

        //NOTE - CAN'T SET THE ID - test if object same
        PropertyDao propertyDaoInDb = new PropertyDao();

        doReturn(Optional.of(propertyDaoInDb)).when(databaseHelperSpy).findPropertyByNaturalId(any(), eq(propertyToTest.getNaturalId()));

        //
        //Act
        //
        PropertyDao propertyDao = databaseHelperSpy.translatePropertyToPropertyDao(session, propertyToTest);

        //
        //Assert
        //
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());
        assertThat(propertyDao).isSameAs(propertyDaoInDb);
    }

    @Test
    public void translatePersonToPersonDb_newPersonNewProperty() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelperSpy = spy(new DatabaseHelper());

        Person personToTest = createPerson();
        Property propertyToTest = createProperty();

        personToTest.addProperty(propertyToTest);

        doReturn(Optional.empty()).when(databaseHelperSpy).findPropertyByNaturalId(any(), any());
        doReturn(Optional.empty()).when(databaseHelperSpy).findPersonByNaturalId(any(), any());

        //
        //Act
        //
        PersonDao personDao = databaseHelperSpy.translatePersonToPersonDb(session, personToTest);

        //
        //Assert
        //

        //Person Check
        assertThat(personDao.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());
        assertThat(personDao.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(personDao.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(personDao.getAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(personDao.getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(personDao.getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(personDao.getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(personDao.getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(personDao.getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(personDao.getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        assertThat(personDao.getPhone1()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(personDao.getPhone1Type()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(personDao.isPhone1DNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(personDao.isPhone1Stop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(personDao.isPhone1Litigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(personDao.getPhone1Telco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2Type()).isNullOrEmpty();
        assertThat(personDao.isPhone2DNC()).isFalse();
        assertThat(personDao.isPhone2Stop()).isFalse();
        assertThat(personDao.isPhone2Litigation()).isFalse();
        assertThat(personDao.getPhone2Telco()).isNullOrEmpty();

        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3Type()).isNullOrEmpty();
        assertThat(personDao.isPhone3DNC()).isFalse();
        assertThat(personDao.isPhone3Stop()).isFalse();
        assertThat(personDao.isPhone3Litigation()).isFalse();
        assertThat(personDao.getPhone3Telco()).isNullOrEmpty();

        assertThat(personDao.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(personDao.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(personDao.getEmail3()).isNullOrEmpty();

        //Property Check
        PropertyDao propertyDao = personDao.getProperties().get(0);
        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());

        assertThat(propertyDao.getId()).isNotNull();

        assertThat(personDao.getProperties().get(0)).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());
        assertThat(propertyDao.getAssessedValue()).isEqualTo(propertyToTest.getAssessedValue());

        assertThat(propertyDao.getLastSaleRecordingDate()).isEqualTo(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());
        assertThat(propertyDao.getmLSDate()).isEqualTo(propertyToTest.getMlsDate());

        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());
        assertThat(propertyDao.getDateAddedToList()).isEqualTo(propertyToTest.getDateAddedToList());
    }

    @Test
    public void translateColdRvmLeadToColdRvmLeadDb_newEverything() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelperSpy = spy(new DatabaseHelper());

        ColdRvmLead coldRvmLead = createColdRvmLead();

        doReturn(Optional.empty()).when(databaseHelperSpy).findPropertyByNaturalId(any(), any());
        doReturn(Optional.empty()).when(databaseHelperSpy).findPersonByNaturalId(any(), any());
        doReturn(Optional.empty()).when(databaseHelperSpy).findColdRvmLeadByNaturalId(any(), any());

        //
        //Act
        //
        ColdRvmLeadDao coldRvmLeadDao = databaseHelperSpy.translateColdRvmLeadToColdRvmLeadDb(session, coldRvmLead);

        //
        //Assert
        //

        //ColdRvmLead check
        assertThat(coldRvmLeadDao.getNaturalId()).isEqualToIgnoringCase(coldRvmLeadDao.getNaturalId());
        assertThat(coldRvmLeadDao.getDateWorkflowStarted()).isEqualTo(coldRvmLead.getDateWorkflowStarted());
        assertThat(coldRvmLeadDao.isConversationStarted()).isEqualTo(coldRvmLead.isConversationStarted());
        assertThat(coldRvmLeadDao.isToldToStop()).isEqualTo(coldRvmLead.isToldToStop());
        assertThat(coldRvmLeadDao.isSold()).isEqualTo(coldRvmLead.isSold());
        assertThat(coldRvmLeadDao.isWrongNumber()).isEqualTo(coldRvmLead.isWrongNumber());
        assertThat(coldRvmLeadDao.isOfferMade()).isEqualTo(coldRvmLead.isOfferMade());
        assertThat(coldRvmLeadDao.isLeadSentToAgent()).isEqualTo(coldRvmLead.isLeadSentToAgent());

        //Person Check
        PersonDao personDao = coldRvmLeadDao.getPerson();
        Person personToTest = coldRvmLead.getPerson();

        //Person Check
        assertThat(personDao.getNaturalId()).isEqualToIgnoringCase(personToTest.getNaturalId());
        assertThat(personDao.getFirstName()).isEqualToIgnoringCase(personToTest.getFirstName());
        assertThat(personDao.getLastName()).isEqualToIgnoringCase(personToTest.getLastName());
        assertThat(personDao.getAddress()).isEqualToIgnoringCase(personToTest.getAddress().getMailingAddress());
        assertThat(personDao.getUnitNumber()).isEqualToIgnoringCase(personToTest.getAddress().getUnitNumber());
        assertThat(personDao.getCity()).isEqualToIgnoringCase(personToTest.getAddress().getCity());
        assertThat(personDao.getState()).isEqualToIgnoringCase(personToTest.getAddress().getState());
        assertThat(personDao.getZip()).isEqualToIgnoringCase(personToTest.getAddress().getZip());
        assertThat(personDao.getCounty()).isEqualToIgnoringCase(personToTest.getAddress().getCounty());
        assertThat(personDao.getCountry()).isEqualToIgnoringCase(personToTest.getAddress().getCountry());

        assertThat(personDao.getPhone1()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneNumber());
        assertThat(personDao.getPhone1Type()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneType());
        assertThat(personDao.isPhone1DNC()).isEqualTo(personToTest.getPhone1().isPhoneDNC());
        assertThat(personDao.isPhone1Stop()).isEqualTo(personToTest.getPhone1().isPhoneStop());
        assertThat(personDao.isPhone1Litigation()).isEqualTo(personToTest.getPhone1().isPhoneLitigation());
        assertThat(personDao.getPhone1Telco()).isEqualToIgnoringCase(personToTest.getPhone1().getPhoneTelco());

        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2()).isNullOrEmpty();
        assertThat(personDao.getPhone2Type()).isNullOrEmpty();
        assertThat(personDao.isPhone2DNC()).isFalse();
        assertThat(personDao.isPhone2Stop()).isFalse();
        assertThat(personDao.isPhone2Litigation()).isFalse();
        assertThat(personDao.getPhone2Telco()).isNullOrEmpty();

        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3()).isNullOrEmpty();
        assertThat(personDao.getPhone3Type()).isNullOrEmpty();
        assertThat(personDao.isPhone3DNC()).isFalse();
        assertThat(personDao.isPhone3Stop()).isFalse();
        assertThat(personDao.isPhone3Litigation()).isFalse();
        assertThat(personDao.getPhone3Telco()).isNullOrEmpty();

        assertThat(personDao.getEmail1()).isEqualToIgnoringCase(personToTest.getEmail1());
        assertThat(personDao.getEmail2()).isEqualToIgnoringCase(personToTest.getEmail2());
        assertThat(personDao.getEmail3()).isNullOrEmpty();

        //Property Check
        PropertyDao propertyDao = coldRvmLeadDao.getProperty();
        Property propertyToTest = coldRvmLead.getProperty();

        assertThat(propertyDao.getNaturalId()).isEqualToIgnoringCase(propertyToTest.getNaturalId());

        assertThat(propertyDao.getId()).isNotNull();

        //address
        assertThat(propertyDao.getAddress()).isEqualToIgnoringCase(propertyToTest.getAddress().getMailingAddress());
        assertThat(propertyDao.getUnitNumber()).isEqualToIgnoringCase(propertyToTest.getAddress().getUnitNumber());
        assertThat(propertyDao.getCity()).isEqualToIgnoringCase(propertyToTest.getAddress().getCity());
        assertThat(propertyDao.getState()).isEqualToIgnoringCase(propertyToTest.getAddress().getState());
        assertThat(propertyDao.getZip()).isEqualToIgnoringCase(propertyToTest.getAddress().getZip());
        assertThat(propertyDao.getCounty()).isEqualToIgnoringCase(propertyToTest.getAddress().getCounty());
        assertThat(propertyDao.getCountry()).isEqualToIgnoringCase(propertyToTest.getAddress().getCountry());

        assertThat(propertyDao.getaPN()).isEqualToIgnoringCase(propertyToTest.getaPN());
        assertThat(propertyDao.isOwnerOccupied()).isEqualTo(propertyToTest.isOwnerOccupied());
        assertThat(propertyDao.getCompanyName()).isEqualToIgnoringCase(propertyToTest.getCompanyName());
        assertThat(propertyDao.getCompanyAddress()).isEqualToIgnoringCase(propertyToTest.getCompanyAddress());

        assertThat(propertyDao.getPropertyType()).isEqualToIgnoringCase(propertyToTest.getPropertyType());
        assertThat(propertyDao.getBedrooms()).isEqualToIgnoringCase(propertyToTest.getBedrooms());
        assertThat(propertyDao.getTotalBathrooms()).isEqualToIgnoringCase(propertyToTest.getTotalBathrooms());
        assertThat(propertyDao.getSqft()).isEqualTo(propertyToTest.getSqft());
        assertThat(propertyDao.getLoftSizeSqft()).isEqualTo(propertyToTest.getLotSizeSqft());
        assertThat(propertyDao.getYearBuilt()).isEqualTo(propertyToTest.getYearBuilt());
        assertThat(propertyDao.getAssessedValue()).isEqualTo(propertyToTest.getAssessedValue());

        assertThat(propertyDao.getLastSaleRecordingDate()).isEqualTo(propertyToTest.getLastSaleRecordingDate());

        assertThat(propertyDao.getLastSaleAmount()).isEqualTo(propertyToTest.getLastSaleAmount());
        assertThat(propertyDao.getTotalOpenLoans()).isEqualTo(propertyToTest.getTotalOpenLoans());
        assertThat(propertyDao.getEstimatedRemainingBalance()).isEqualTo(propertyToTest.getEstimatedRemainingBalance());
        assertThat(propertyDao.getEstimatedValue()).isEqualTo(propertyToTest.getEstimatedValue());
        assertThat(propertyDao.getEstimatedEquity()).isEqualTo(propertyToTest.getEstimatedEquity());

        assertThat(propertyDao.getmLSStatus()).isEqualTo(propertyToTest.getmLSStatus());
        assertThat(propertyDao.getmLSDate()).isEqualTo(propertyToTest.getMlsDate());

        assertThat(propertyDao.getmLSAmount()).isEqualTo(propertyToTest.getmLSAmount());
        assertThat(propertyDao.getLienAmount()).isEqualTo(propertyToTest.getLienAmount());
        assertThat(propertyDao.getDateAddedToList()).isEqualTo(propertyToTest.getDateAddedToList());
    }

    @Test
    public void translatePropertyDbToProperty() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelperSpy = spy(new DatabaseHelper());

        PropertyDao propertyDao = createPropertyDao();

        //
        //Act
        //
        Property propertyToTest = databaseHelperSpy.translatePropertyDbToProperty(propertyDao);

        //
        //Assert
        //
        //Note - natural ID should be derived by the Business Logic.
        assertThat(propertyToTest.getNaturalId()).isNotEqualToIgnoringCase(propertyDao.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(propertyDao.getAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(propertyDao.getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(propertyDao.getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(propertyDao.getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(propertyDao.getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(propertyDao.getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(propertyDao.getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(propertyDao.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(propertyDao.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(propertyDao.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(propertyDao.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(propertyDao.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(propertyDao.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(propertyDao.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(propertyDao.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(propertyDao.getLoftSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(propertyDao.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(propertyDao.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(propertyDao.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isEqualTo(propertyDao.getmLSDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(propertyDao.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isEqualTo(propertyDao.getDateAddedToList());
    }

    @Test
    public void translatePersonDbToPerson() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        PersonDao personDao = createPersonDao();

        //
        //Act
        //
        Person personToTest = databaseHelper.translatePersonDbToPerson(personDao);

        //
        //Assert
        //
        assertThat(personToTest.getNaturalId()).isNotEqualToIgnoringCase(personDao.getNaturalId());

        assertThat(personToTest.getFirstName()).isEqualToIgnoringCase(personDao.getFirstName());
        assertThat(personToTest.getLastName()).isEqualToIgnoringCase(personDao.getLastName());
        assertThat(personToTest.getAddress().getMailingAddress()).isEqualToIgnoringCase(personDao.getAddress());
        assertThat(personToTest.getAddress().getUnitNumber()).isEqualToIgnoringCase(personDao.getUnitNumber());
        assertThat(personToTest.getAddress().getCity()).isEqualToIgnoringCase(personDao.getCity());
        assertThat(personToTest.getAddress().getState()).isEqualToIgnoringCase(personDao.getState());
        assertThat(personToTest.getAddress().getZip()).isEqualToIgnoringCase(personDao.getZip());
        assertThat(personToTest.getAddress().getCounty()).isEqualToIgnoringCase(personDao.getCounty());
        assertThat(personToTest.getAddress().getCountry()).isEqualToIgnoringCase(personDao.getCountry());

        //phone 1
        assertThat(personToTest.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone1());
        assertThat(personToTest.getPhone1().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone1Type());
        assertThat(personToTest.getPhone1().isPhoneDNC()).isEqualTo(personDao.isPhone1DNC());
        assertThat(personToTest.getPhone1().isPhoneStop()).isEqualTo(personDao.isPhone1Stop());
        assertThat(personToTest.getPhone1().isPhoneLitigation()).isEqualTo(personDao.isPhone1Litigation());
        assertThat(personToTest.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone1Telco());

        //phone 2
        assertThat(personToTest.getPhone2().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone2());
        assertThat(personToTest.getPhone2().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone2Type());
        assertThat(personToTest.getPhone2().isPhoneDNC()).isEqualTo(personDao.isPhone2DNC());
        assertThat(personToTest.getPhone2().isPhoneStop()).isEqualTo(personDao.isPhone2Stop());
        assertThat(personToTest.getPhone2().isPhoneLitigation()).isEqualTo(personDao.isPhone2Litigation());
        assertThat(personToTest.getPhone2().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone2Telco());

        //phone 3
        assertThat(personToTest.getPhone3().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone3());
        assertThat(personToTest.getPhone3().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone3Type());
        assertThat(personToTest.getPhone3().isPhoneDNC()).isEqualTo(personDao.isPhone3DNC());
        assertThat(personToTest.getPhone3().isPhoneStop()).isEqualTo(personDao.isPhone3Stop());
        assertThat(personToTest.getPhone3().isPhoneLitigation()).isEqualTo(personDao.isPhone3Litigation());
        assertThat(personToTest.getPhone3().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone3Telco());

        assertThat(personToTest.getEmail1()).isEqualToIgnoringCase(personDao.getEmail1());
        assertThat(personToTest.getEmail2()).isEqualToIgnoringCase(personDao.getEmail2());
        assertThat(personToTest.getEmail3()).isEqualToIgnoringCase(personDao.getEmail3());

        //Property
        Property propertyToTest = personToTest.getPropertyList().get(0);
        PropertyDao propertyDao = personDao.getProperties().get(0);

        assertThat(propertyToTest.getNaturalId()).isNotEqualToIgnoringCase(propertyDao.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(propertyDao.getAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(propertyDao.getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(propertyDao.getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(propertyDao.getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(propertyDao.getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(propertyDao.getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(propertyDao.getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(propertyDao.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(propertyDao.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(propertyDao.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(propertyDao.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(propertyDao.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(propertyDao.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(propertyDao.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(propertyDao.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(propertyDao.getLoftSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(propertyDao.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(propertyDao.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(propertyDao.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isEqualTo(propertyDao.getmLSDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(propertyDao.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isEqualTo(propertyDao.getDateAddedToList());
    }

    @Test
    public void translateColdRvmLeadDbToColdRvmLead() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        ColdRvmLeadDao coldRvmLeadDao = createColdRvmLeadDao();

        //
        //Act
        //
        ColdRvmLead coldRvmLeadToTest = databaseHelper.translateColdRvmLeadDbToColdRvmLead(coldRvmLeadDao);

        //
        //Assert
        //
        //natural IDs should not match
        assertThat(coldRvmLeadToTest.getNaturalId()).isNotEqualToIgnoringCase(coldRvmLeadDao.getNaturalId());

        assertThat(coldRvmLeadToTest.getDateWorkflowStarted()).isEqualTo(coldRvmLeadDao.getDateWorkflowStarted());
        assertThat(coldRvmLeadToTest.isConversationStarted()).isEqualTo(coldRvmLeadDao.isConversationStarted());
        assertThat(coldRvmLeadToTest.isToldToStop()).isEqualTo(coldRvmLeadDao.isToldToStop());
        assertThat(coldRvmLeadToTest.isSold()).isEqualTo(coldRvmLeadDao.isSold());
        assertThat(coldRvmLeadToTest.isWrongNumber()).isEqualTo(coldRvmLeadDao.isWrongNumber());
        assertThat(coldRvmLeadToTest.isOfferMade()).isEqualTo(coldRvmLeadDao.isOfferMade());
        assertThat(coldRvmLeadToTest.isLeadSentToAgent()).isEqualTo(coldRvmLeadDao.isLeadSentToAgent());

        //Person
        Person personToTest = coldRvmLeadToTest.getPerson();
        PersonDao personDao = coldRvmLeadDao.getPerson();

        assertThat(personToTest.getNaturalId()).isNotEqualToIgnoringCase(personDao.getNaturalId());

        assertThat(personToTest.getFirstName()).isEqualToIgnoringCase(personDao.getFirstName());
        assertThat(personToTest.getLastName()).isEqualToIgnoringCase(personDao.getLastName());
        assertThat(personToTest.getAddress().getMailingAddress()).isEqualToIgnoringCase(personDao.getAddress());
        assertThat(personToTest.getAddress().getUnitNumber()).isEqualToIgnoringCase(personDao.getUnitNumber());
        assertThat(personToTest.getAddress().getCity()).isEqualToIgnoringCase(personDao.getCity());
        assertThat(personToTest.getAddress().getState()).isEqualToIgnoringCase(personDao.getState());
        assertThat(personToTest.getAddress().getZip()).isEqualToIgnoringCase(personDao.getZip());
        assertThat(personToTest.getAddress().getCounty()).isEqualToIgnoringCase(personDao.getCounty());
        assertThat(personToTest.getAddress().getCountry()).isEqualToIgnoringCase(personDao.getCountry());

        //phone 1
        assertThat(personToTest.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone1());
        assertThat(personToTest.getPhone1().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone1Type());
        assertThat(personToTest.getPhone1().isPhoneDNC()).isEqualTo(personDao.isPhone1DNC());
        assertThat(personToTest.getPhone1().isPhoneStop()).isEqualTo(personDao.isPhone1Stop());
        assertThat(personToTest.getPhone1().isPhoneLitigation()).isEqualTo(personDao.isPhone1Litigation());
        assertThat(personToTest.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone1Telco());

        //phone 2
        assertThat(personToTest.getPhone2().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone2());
        assertThat(personToTest.getPhone2().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone2Type());
        assertThat(personToTest.getPhone2().isPhoneDNC()).isEqualTo(personDao.isPhone2DNC());
        assertThat(personToTest.getPhone2().isPhoneStop()).isEqualTo(personDao.isPhone2Stop());
        assertThat(personToTest.getPhone2().isPhoneLitigation()).isEqualTo(personDao.isPhone2Litigation());
        assertThat(personToTest.getPhone2().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone2Telco());

        //phone 3
        assertThat(personToTest.getPhone3().getPhoneNumber()).isEqualToIgnoringCase(personDao.getPhone3());
        assertThat(personToTest.getPhone3().getPhoneType()).isEqualToIgnoringCase(personDao.getPhone3Type());
        assertThat(personToTest.getPhone3().isPhoneDNC()).isEqualTo(personDao.isPhone3DNC());
        assertThat(personToTest.getPhone3().isPhoneStop()).isEqualTo(personDao.isPhone3Stop());
        assertThat(personToTest.getPhone3().isPhoneLitigation()).isEqualTo(personDao.isPhone3Litigation());
        assertThat(personToTest.getPhone3().getPhoneTelco()).isEqualToIgnoringCase(personDao.getPhone3Telco());

        assertThat(personToTest.getEmail1()).isEqualToIgnoringCase(personDao.getEmail1());
        assertThat(personToTest.getEmail2()).isEqualToIgnoringCase(personDao.getEmail2());
        assertThat(personToTest.getEmail3()).isEqualToIgnoringCase(personDao.getEmail3());

        //Property
        Property propertyToTest = coldRvmLeadToTest.getProperty();
        PropertyDao propertyDao = coldRvmLeadDao.getProperty();

        assertThat(propertyToTest.getNaturalId()).isNotEqualToIgnoringCase(propertyDao.getNaturalId());

        assertThat(propertyToTest.getAddress().getMailingAddress()).isEqualTo(propertyDao.getAddress());
        assertThat(propertyToTest.getAddress().getUnitNumber()).isEqualTo(propertyDao.getUnitNumber());
        assertThat(propertyToTest.getAddress().getCity()).isEqualToIgnoringCase(propertyDao.getCity());
        assertThat(propertyToTest.getAddress().getState()).isEqualToIgnoringCase(propertyDao.getState());
        assertThat(propertyToTest.getAddress().getZip()).isEqualToIgnoringCase(propertyDao.getZip());
        assertThat(propertyToTest.getAddress().getCounty()).isEqualToIgnoringCase(propertyDao.getCounty());
        assertThat(propertyToTest.getAddress().getCountry()).isEqualToIgnoringCase(propertyDao.getCountry());

        assertThat(propertyToTest.getaPN()).isEqualToIgnoringCase(propertyDao.getaPN());
        assertThat(propertyToTest.isOwnerOccupied()).isEqualTo(propertyDao.isOwnerOccupied());
        assertThat(propertyToTest.getCompanyName()).isEqualToIgnoringCase(propertyDao.getCompanyName());
        assertThat(propertyToTest.getCompanyAddress()).isEqualToIgnoringCase(propertyDao.getCompanyAddress());
        assertThat(propertyToTest.getPropertyType()).isEqualToIgnoringCase(propertyDao.getPropertyType());
        assertThat(propertyToTest.getBedrooms()).isEqualToIgnoringCase(propertyDao.getBedrooms());
        assertThat(propertyToTest.getTotalBathrooms()).isEqualToIgnoringCase(propertyDao.getTotalBathrooms());
        assertThat(propertyToTest.getSqft()).isEqualTo(propertyDao.getSqft());
        assertThat(propertyToTest.getLotSizeSqft()).isEqualTo(propertyDao.getLoftSizeSqft());
        assertThat(propertyToTest.getYearBuilt()).isEqualTo(propertyDao.getYearBuilt());
        assertThat(propertyToTest.getAssessedValue()).isEqualTo(propertyDao.getAssessedValue());
        assertThat(propertyToTest.getmLSStatus()).isEqualToIgnoringCase(propertyDao.getmLSStatus());
        assertThat(propertyToTest.getMlsDate()).isEqualTo(propertyDao.getmLSDate());
        assertThat(propertyToTest.getmLSAmount()).isEqualToIgnoringCase(propertyDao.getmLSAmount());
        assertThat(propertyToTest.getDateAddedToList()).isEqualTo(propertyDao.getDateAddedToList());
    }

    @Test
    public void findPropertyByNaturalId_findProperty() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        PropertyDao propertyDao = new PropertyDao();
        propertyDao.setNaturalId(naturalId);
        propertyDao.setAddress("100 White House Road");

        //Insert Property into database
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(propertyDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PropertyDao> optionalPropertyDao = databaseHelper.findPropertyByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalPropertyDao.isPresent()).isTrue();
        assertThat(optionalPropertyDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
    }

    @Test
    public void findPropertyByNaturalId_findPropertyPersistentObject() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        PropertyDao propertyDao = new PropertyDao();
        propertyDao.setNaturalId(naturalId);
        propertyDao.setAddress("100 White House Road");

        //Insert Property into database
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(propertyDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PropertyDao> optionalPropertyDao = databaseHelper.findPropertyByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalPropertyDao.isPresent()).isTrue();
        assertThat(optionalPropertyDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
    }

    @Test
    public void findPropertyByNaturalId_noProperty() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PropertyDao> optionalPropertyDao = databaseHelper.findPropertyByNaturalId(session, "1234");

        //
        //Assert
        //
        assertThat(optionalPropertyDao.isPresent()).isFalse();
    }

    @Test
    public void findPropertyByNaturalId_noPropertyPersistentObject() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PropertyDao> optionalPropertyDao = databaseHelper.findPropertyByNaturalId(session, "1234");

        //
        //Assert
        //
        assertThat(optionalPropertyDao.isPresent()).isFalse();
    }

    @Test
    public void findPersonByNaturalId_throwDbException() {
        //
        //Arrange
        //
        HibernateUtil hibernateUtilMock = Mockito.mock(HibernateUtil.class);
        when(hibernateUtilMock.getSessionFactory()).thenReturn(null);

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, "1111");

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isFalse();
    }

    @Test
    public void findPersonByNaturalId_findPerson() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        PersonDao personDao = new PersonDao();

        personDao.setNaturalId(naturalId);
        personDao.setFirstName("Big Dan");
        personDao.setLastName("LEO");
        personDao.setAddress("100 White House Road");

        //Insert Person into database
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(personDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isTrue();
        assertThat(optionalPersonDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
    }

    @Test
    public void findPersonByNaturalId_findPropertyPersistentObject() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        final String firstName = "Big Dan";
        PersonDao personDao = new PersonDao();

        personDao.setNaturalId(naturalId);
        personDao.setFirstName(firstName);
        personDao.setLastName("LEO");
        personDao.setAddress("100 White House Road");

        //Insert Person into database
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(personDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isTrue();
        assertThat(optionalPersonDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalPersonDao.get().getFirstName()).isEqualToIgnoringCase(firstName);
    }

    @Test
    public void findPersonByNaturalId_noPerson() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, "1234");

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isFalse();
    }

    @Test
    public void findPersonByNaturalId_noPersonPersistentObject() {
        //
        //Arrange
        //
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, "1234");

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isFalse();
    }

    @Test
    public void findPersonByNaturalId_withProperty() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        final String firstName = "Big Dan";
        final String address = "100 White House Road";

        PropertyDao propertyDao = new PropertyDao();
        propertyDao.setNaturalId(naturalId);
        propertyDao.setAddress(address);

        PersonDao personDao = new PersonDao();
        personDao.setNaturalId(naturalId);
        personDao.setFirstName(firstName);
        personDao.setLastName("LEO");
        personDao.setAddress(address);
        personDao.addProperty(propertyDao);

        //Insert person and property into database - remember you have to save property first so you don't fk this up
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(propertyDao);
        session.saveOrUpdate(personDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<PersonDao> optionalPersonDao = databaseHelper.findPersonByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalPersonDao.isPresent()).isTrue();
        assertThat(optionalPersonDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalPersonDao.get().getFirstName()).isEqualToIgnoringCase(firstName);

        assertThat(optionalPersonDao.get().getProperties().get(0).getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalPersonDao.get().getProperties().get(0).getAddress()).isEqualToIgnoringCase(address);
    }

    @Test
    public void findColdRvmLeadByNaturalId_findColdRvm() {
        //
        //Arrange
        //
        final String naturalId = "1111";
        final String firstName = "Big Dan";
        final String address = "100 White House Road";

        PropertyDao propertyDao = new PropertyDao();
        propertyDao.setNaturalId(naturalId);
        propertyDao.setAddress(address);

        PersonDao personDao = new PersonDao();
        personDao.setNaturalId(naturalId);
        personDao.setFirstName(firstName);
        personDao.setLastName("LEO");
        personDao.setAddress(address);
        personDao.addProperty(propertyDao);

        ColdRvmLeadDao coldRvmLeadDao = new ColdRvmLeadDao();
        coldRvmLeadDao.setNaturalId(naturalId);
        coldRvmLeadDao.setDateWorkflowStarted(new Date());
        coldRvmLeadDao.setConversationStarted(true);
        coldRvmLeadDao.setToldToStop(false);
        coldRvmLeadDao.setWrongNumber(false);
        coldRvmLeadDao.setOfferMade(false);
        coldRvmLeadDao.setLeadSentToAgent(false);

        coldRvmLeadDao.setPerson(personDao);
        coldRvmLeadDao.setProperty(propertyDao);

        //Insert person and property into database - remember you have to save property first so you don't fk this up
        Transaction transaction;
        transaction = session.beginTransaction();
        session.saveOrUpdate(propertyDao);
        session.saveOrUpdate(personDao);
        session.saveOrUpdate(coldRvmLeadDao);
        transaction.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper();

        //
        //Act
        //
        Optional<ColdRvmLeadDao> optionalColdRvmLeadDao = databaseHelper.findColdRvmLeadByNaturalId(session, naturalId);

        //
        //Assert
        //
        assertThat(optionalColdRvmLeadDao.isPresent()).isTrue();
        assertThat(optionalColdRvmLeadDao.get().getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalColdRvmLeadDao.get().isConversationStarted()).isTrue();
        assertThat(optionalColdRvmLeadDao.get().isToldToStop()).isFalse();
        assertThat(optionalColdRvmLeadDao.get().isSold()).isFalse();
        assertThat(optionalColdRvmLeadDao.get().isWrongNumber()).isFalse();
        assertThat(optionalColdRvmLeadDao.get().isOfferMade()).isFalse();
        assertThat(optionalColdRvmLeadDao.get().isLeadSentToAgent()).isFalse();

        assertThat(optionalColdRvmLeadDao.get().getPerson().getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalColdRvmLeadDao.get().getPerson().getFirstName()).isEqualToIgnoringCase(firstName);

        assertThat(optionalColdRvmLeadDao.get().getProperty().getNaturalId()).isEqualToIgnoringCase(naturalId);
        assertThat(optionalColdRvmLeadDao.get().getProperty().getAddress()).isEqualToIgnoringCase(address);
    }

    private ColdRvmLeadDao createColdRvmLeadDao() {
        ColdRvmLeadDao coldRvmLeadDao = new ColdRvmLeadDao();
        coldRvmLeadDao.setNaturalId("1234");

        coldRvmLeadDao.setDateWorkflowStarted(new Date());
        coldRvmLeadDao.setConversationStarted(true);
        coldRvmLeadDao.setToldToStop(false);
        coldRvmLeadDao.setWrongNumber(true);
        coldRvmLeadDao.setOfferMade(true);
        coldRvmLeadDao.setLeadSentToAgent(true);

        PropertyDao propertyDao = createPropertyDao();

        PersonDao personDao = createPersonDao();

        coldRvmLeadDao.setProperty(propertyDao);
        coldRvmLeadDao.setPerson(personDao);

        return coldRvmLeadDao;
    }

    private PropertyDao createPropertyDao() {
        PropertyDao propertyDao = new PropertyDao();
        propertyDao.setNaturalId("1111");
        propertyDao.setAddress("Address");
        propertyDao.setUnitNumber("1");
        propertyDao.setCity("New York");
        propertyDao.setState("NY");
        propertyDao.setZip("11234");
        propertyDao.setCounty("New York County");
        propertyDao.setCountry("US");
        propertyDao.setaPN("APN:12345");
        propertyDao.setOwnerOccupied(true);
        propertyDao.setCompanyName("Company Name,LLC");
        propertyDao.setCompanyAddress("Company Address");
        propertyDao.setPropertyType("Multifamily");
        propertyDao.setBedrooms("5");
        propertyDao.setTotalBathrooms("2");
        propertyDao.setSqft(1000);
        propertyDao.setLoftSizeSqft(1500);
        propertyDao.setYearBuilt(1900);
        propertyDao.setAssessedValue(1000000);
        propertyDao.setLastSaleRecordingDate(new Date());
        propertyDao.setLastSaleAmount(500000);
        propertyDao.setTotalOpenLoans(1);
        propertyDao.setEstimatedRemainingBalance(100000);
        propertyDao.setEstimatedValue(15000000);
        propertyDao.setEstimatedEquity(450000);
        propertyDao.setmLSStatus("MLS Status");
        propertyDao.setmLSAmount("MLS Amount");
        propertyDao.setLienAmount("MLS Lien Amount");
        propertyDao.setDateAddedToList(new Date());

        return propertyDao;
    }

    private PersonDao createPersonDao() {
        PersonDao personDao = new PersonDao();
        personDao.setNaturalId("1111");
        personDao.setFirstName("First Name Test");
        personDao.setLastName("Last Name Test");
        personDao.setAddress("Address Test");
        personDao.setUnitNumber("1");
        personDao.setCity("City Test");
        personDao.setState("NY");
        personDao.setZip("12345");
        personDao.setCounty("New York");
        personDao.setCountry("US");

        personDao.setPhone1("217-123-4567");
        personDao.setPhone1Type("Telco");
        personDao.setPhone1DNC(false);
        personDao.setPhone1Stop(false);
        personDao.setPhone1Litigation(true);
        personDao.setPhone1Telco("Verizon");

        personDao.setPhone2("217-123-4557");
        personDao.setPhone2Type("Telcoz");
        personDao.setPhone2DNC(true);
        personDao.setPhone2Stop(false);
        personDao.setPhone2Litigation(true);
        personDao.setPhone2Telco("AT&T");

        personDao.setPhone3("217-123-4567");
        personDao.setPhone3Type("Telco");
        personDao.setPhone3DNC(false);
        personDao.setPhone3Stop(false);
        personDao.setPhone3Litigation(true);
        personDao.setPhone3Telco("Verizon");

        personDao.setEmail1("Email 1");
        personDao.setEmail2("Email 2");
        personDao.setEmail3("Email 3");

        personDao.addProperty(createPropertyDao());

        return personDao;
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