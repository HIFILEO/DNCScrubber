package com.LEO.DNCScrubber.Scrubber.gateway;/*
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
import com.LEO.DNCScrubber.Scrubber.gateway.model.ColdRvmLeadDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PropertyDao;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import io.reactivex.Observable;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * {@link DatabaseGateway} implementation using Hibernate. https://hibernate.org/
 */
public class DatabaseGatewayImpl implements DatabaseGateway{
    final static Logger logger = LoggerFactory.getLogger(DatabaseGatewayImpl.class);
    private final HibernateUtil hibernateUtil;
    private final ColdRvmLeadHandler coldRvmLeadHandler;

    /**
     * Constructor
     * @param hibernateUtil - for DB interaction
     */
    public DatabaseGatewayImpl(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
        this.coldRvmLeadHandler = new ColdRvmLeadHandler(hibernateUtil);
    }

    @Override
    public Observable<Boolean> writeColdRvmLead(ColdRvmLead coldRvmLead) {
        return coldRvmLeadHandler.writeRawLead(coldRvmLead);
    }

    @Override
    public Observable<ColdRvmLead> loadColdRvmLeadByNaturalId(String naturalId) {
        Transaction transaction = null;
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            ColdRvmLeadDao coldRvmLeadDao = session.byNaturalId(ColdRvmLeadDao.class)
                    .using("naturalId", naturalId)
                    .load();
        } catch (Exception exception){

        }

            return null;
    }

    @Override
    public Observable<Person> loadPersonByNaturalId(String naturalId) {
        return null;
    }

    @Override
    public Observable<Property> loadPropertyByNaturalId(String naturalId) {
        return null;
    }

    /**
     * Convert {@link ColdRvmLeadDao} to {@link ColdRvmLead}
     * @param coldRvmLeadDao - DAO
     * @return - {@link ColdRvmLead}
     */
    private ColdRvmLead convertToColdRvmLead(ColdRvmLeadDao coldRvmLeadDao) {
        ColdRvmLead coldRvmLead = new ColdRvmLead();
        coldRvmLead.setDateWorkflowStarted(coldRvmLeadDao.getDateWorkflowStarted());
        coldRvmLead.setConversationStarted(coldRvmLeadDao.isConversationStarted());
        coldRvmLead.setToldToStop(coldRvmLeadDao.isToldToStop());
        coldRvmLead.setSold(coldRvmLeadDao.isSold());
        coldRvmLead.setWrongNumber(coldRvmLeadDao.isWrongNumber());
        coldRvmLead.setOfferMade(coldRvmLeadDao.isOfferMade());
        coldRvmLead.setLeadSentToAgent(coldRvmLeadDao.isLeadSentToAgent());
        coldRvmLead.setPerson(convertToPerson(coldRvmLeadDao.getPerson()));
        coldRvmLead.setProperty(covertToProperty(coldRvmLeadDao.getProperty()));

        return coldRvmLead;
    }

    /**
     * Convert {@link PersonDao} to {@link Person}
     * @param personDao - DAO
     * @return - {@link Person}
     */
    private Person convertToPerson(PersonDao personDao) {
        Address address = new Address();
        address.setMailingAddress(personDao.getAddress());
        address.setUnitNumber(personDao.getUnitNumber());
        address.setCity(personDao.getCity());
        address.setState(personDao.getState());
        address.setZip(personDao.getZip());
        address.setCounty(personDao.getCounty());
        address.setCountry(personDao.getCountry());

        Phone phone1 = new Phone();
        phone1.setPhoneNumber(personDao.getPhone1());
        phone1.setPhoneType(personDao.getPhone1Type());
        phone1.setPhoneDNC(personDao.isPhone1DNC());
        phone1.setPhoneStop(personDao.isPhone1Stop());
        phone1.setPhoneLitigation(personDao.isPhone1Litigation());
        phone1.setPhoneTelco(personDao.getPhone1Telco());

        Phone phone2 = new Phone();
        phone2.setPhoneNumber(personDao.getPhone2());
        phone2.setPhoneType(personDao.getPhone2Type());
        phone2.setPhoneDNC(personDao.isPhone2DNC());
        phone2.setPhoneStop(personDao.isPhone2Stop());
        phone2.setPhoneLitigation(personDao.isPhone2Litigation());
        phone2.setPhoneTelco(personDao.getPhone2Telco());

        Phone phone3 = new Phone();
        phone3.setPhoneNumber(personDao.getPhone3());
        phone3.setPhoneType(personDao.getPhone3Type());
        phone3.setPhoneDNC(personDao.isPhone3DNC());
        phone3.setPhoneStop(personDao.isPhone3Stop());
        phone3.setPhoneLitigation(personDao.isPhone3Litigation());
        phone3.setPhoneTelco(personDao.getPhone3Telco());

        Person person = new Person();
        person.setFirstName(personDao.getFirstName());
        person.setLastName(personDao.getLastName());
        person.setAddress(address);
        person.setPhone1(phone1);
        person.setPhone2(phone2);
        person.setPhone3(phone3);
        person.setEmail1(personDao.getEmail1());
        person.setEmail2(personDao.getEmail2());
        person.setEmail3(personDao.getEmail3());

        for (PropertyDao propertyDao : personDao.getProperties()) {
            person.addProperty(covertToProperty(propertyDao));
        }

        return person;
    }

    /**
     * Convert {@link Property } to {@link Property}
     * @param propertyDao
     * @return
     */
    private Property covertToProperty(PropertyDao propertyDao) {
        Address address = new Address();
        address.setMailingAddress(propertyDao.getAddress());
        address.setUnitNumber(propertyDao.getUnitNumber());
        address.setCity(propertyDao.getCity());
        address.setState(propertyDao.getState());
        address.setZip(propertyDao.getZip());
        address.setCounty(propertyDao.getCounty());
        address.setCountry(propertyDao.getCountry());

        Property property = new Property();
        property.setAddress(address);
        property.setaPN(propertyDao.getaPN());
        property.setOwnerOccupied(propertyDao.isOwnerOccupied());
        property.setCompanyName(propertyDao.getCompanyName());
        property.setCompanyAddress(propertyDao.getCompanyAddress());
        property.setPropertyType(propertyDao.getPropertyType());
        property.setBedrooms(propertyDao.getBedrooms());
        property.setTotalBathrooms(propertyDao.getTotalBathrooms());
        property.setSqft(propertyDao.getSqft());
        property.setLoftSizeSqft(propertyDao.getLoftSizeSqft());
        property.setYearBuilt(propertyDao.getYearBuilt());
        property.setAssessedValue(propertyDao.getAssessedValue());
        property.setLastSaleRecordingDate(propertyDao.getLastSaleRecordingDate());
        property.setLastSaleAmount(propertyDao.getLastSaleAmount());
        property.setTotalOpenLoans(propertyDao.getTotalOpenLoans());
        property.setEstimatedRemainingBalance(propertyDao.getEstimatedRemainingBalance());
        property.setEstimatedValue(propertyDao.getEstimatedValue());
        property.setEstimatedEquity(propertyDao.getEstimatedEquity());
        property.setmLSStatus(propertyDao.getmLSStatus());
        property.setMlsDate(propertyDao.getmLSDate());
        property.setmLSAmount(propertyDao.getmLSAmount());
        property.setLienAmount(propertyDao.getLienAmount());
        property.setDateAddedToList(propertyDao.getDateAddedToList());

        return property;
    }
}
