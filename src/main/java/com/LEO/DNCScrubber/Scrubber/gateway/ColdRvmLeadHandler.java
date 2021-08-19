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
import com.LEO.DNCScrubber.Scrubber.model.data.ColdRvmLead;
import com.LEO.DNCScrubber.Scrubber.model.data.Person;
import com.LEO.DNCScrubber.Scrubber.model.data.Property;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import io.reactivex.Observable;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds all the logic for writing and reading {@link ColdRvmLead}
 */
public class ColdRvmLeadHandler {
    final static Logger logger = LoggerFactory.getLogger(ColdRvmLeadHandler.class);
    private final HibernateUtil hibernateUtil;

    /**
     * Constructor
     * @param hibernateUtil - hibernate utility class
     */
    public ColdRvmLeadHandler(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    /**
     * Write {@link ColdRvmLead} to the database.
     * @param coldRvmLead - lead to write
     * @return - {@link Observable<Boolean>} - true if save/update works, false otherwise
     */
    public Observable<Boolean> writeRawLead(ColdRvmLead coldRvmLead) {
        return Observable.create(emitter -> {

            Transaction transaction = null;
            try (Session session = hibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                /*
                Note - primary key = auto generated ID. The CSV data has no primary ID and to use "String"
                as a key is bad practice since it's almost always non-unique.

                So we are using a 'naturalID' that is an association of unique fields i the data.

                Furthermore, hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
                Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
                the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
                entry exceptions.

                So the rule of thumb is this: save from bottom up while searching for naturalKey every time.
                 */
                saveOrUpdateColdRvmLead(coldRvmLead, session);

                transaction.commit();
                emitter.onNext(true);
                logger.debug("Raw Lead Written To DB Successfully");
            } catch (Exception exception) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Raw Lead Failed To Write To DB. Error - []", exception);
                emitter.onError(exception);
            }
        });
    }


    private void fillPropertyDbFromProperty(Property property, PropertyDao propertyDao) {
        propertyDao.setNaturalId(property.getNaturalId());
        propertyDao.setAddress(property.getAddress().getMailingAddress());
        propertyDao.setUnitNumber(property.getAddress().getUnitNumber());
        propertyDao.setCity(property.getAddress().getCity());
        propertyDao.setState(property.getAddress().getState());
        propertyDao.setZip(property.getAddress().getZip());
        propertyDao.setCounty(property.getAddress().getCounty());
        propertyDao.setCountry(property.getAddress().getCountry());

        propertyDao.setaPN(property.getaPN());
        propertyDao.setOwnerOccupied(property.isOwnerOccupied());
        propertyDao.setCompanyName(property.getCompanyName());
        propertyDao.setPropertyType(property.getPropertyType());
        propertyDao.setBedrooms(property.getBedrooms());
        propertyDao.setTotalBathrooms(property.getTotalBathrooms());
        propertyDao.setSqft(property.getSqft());
        propertyDao.setLoftSizeSqft(property.getLotSizeSqft());
        propertyDao.setYearBuilt(property.getYearBuilt());
        propertyDao.setAssessedValue(property.getAssessedValue());

        propertyDao.setLastSaleAmount(property.getLastSaleAmount());
        propertyDao.setTotalOpenLoans(property.getTotalOpenLoans());
        propertyDao.setEstimatedValue(property.getEstimatedValue());
        propertyDao.setEstimatedEquity(property.getEstimatedEquity());

        propertyDao.setmLSStatus(property.getmLSStatus());

        propertyDao.setmLSDate(property.getMlsDate());

        propertyDao.setmLSAmount(property.getmLSAmount());

        propertyDao.setLienAmount(property.getLienAmount());

        propertyDao.setDateAddedToList(property.getDateAddedToList());
    }

    private void fillPersonDbFromPerson(Person person, PersonDao personDao, Session session) {
        personDao.setNaturalId(person.getNaturalId());
        personDao.setFirstName(person.getFirstName());
        personDao.setLastName(person.getLastName());

        personDao.setAddress(person.getAddress() != null ? person.getAddress().getMailingAddress() : "");
        personDao.setUnitNumber(person.getAddress() != null ? person.getAddress().getUnitNumber() : "");
        personDao.setCity(person.getAddress() != null ? person.getAddress().getCity() : "");
        personDao.setState(person.getAddress() != null ? person.getAddress().getState() : "");
        personDao.setZip(person.getAddress() != null ? person.getAddress().getZip() : "");
        personDao.setCounty(person.getAddress() != null ? person.getAddress().getCounty() : "");
        personDao.setCounty(person.getAddress() != null ? person.getAddress().getCountry() : "");

        personDao.setPhone1(person.getPhone1() != null ? person.getPhone1().getPhoneNumber() : "");
        personDao.setPhone1Type(person.getPhone1() != null ? person.getPhone1().getPhoneType() : "");
        personDao.setPhone1DNC(person.getPhone1() != null && person.getPhone1().isPhoneDNC());
        personDao.setPhone1Stop(person.getPhone1() != null && person.getPhone1().isPhoneStop());
        personDao.setPhone1Telco(person.getPhone1() != null ? person.getPhone1().getPhoneTelco() : "");

        personDao.setPhone2(person.getPhone2() != null ? person.getPhone2().getPhoneNumber() : "");
        personDao.setPhone2Type(person.getPhone2() != null ? person.getPhone2().getPhoneType() : "");
        personDao.setPhone2DNC(person.getPhone2() != null && person.getPhone2().isPhoneDNC());
        personDao.setPhone2Stop(person.getPhone2() != null && person.getPhone2().isPhoneStop());
        personDao.setPhone2Telco(person.getPhone2() != null ? person.getPhone2().getPhoneTelco() : "");

        personDao.setPhone3(person.getPhone3() != null ? person.getPhone3().getPhoneNumber() : "");
        personDao.setPhone3Type(person.getPhone3() != null ? person.getPhone3().getPhoneType() : "");
        personDao.setPhone3DNC(person.getPhone3() != null && person.getPhone3().isPhoneDNC());
        personDao.setPhone3Stop(person.getPhone3() != null && person.getPhone3().isPhoneStop());
        personDao.setPhone3Telco(person.getPhone3() != null ? person.getPhone3().getPhoneTelco() : "");

        personDao.setEmail1(person.getEmail1());
        personDao.setEmail2(person.getEmail2());
        personDao.setEmail3(person.getEmail3());

        for(Property property : person.getPropertyList()) {
            PropertyDao propertyDao = saveOrUpdateProperty(property, session);
            personDao.addProperty(propertyDao);
        }
    }

    private ColdRvmLeadDao fillColdRvmLeadDbFromColdRvmLead(ColdRvmLead coldRvmLead, ColdRvmLeadDao coldRvmLeadDao,
                                                            Session session) {
        coldRvmLeadDao.setNaturalId(coldRvmLead.getNaturalId());
        coldRvmLeadDao.setDateWorkflowStarted(coldRvmLead.getDateWorkflowStarted());
        coldRvmLeadDao.setConversationStarted(coldRvmLead.isConversationStarted());
        coldRvmLeadDao.setToldToStop(coldRvmLead.isToldToStop());
        coldRvmLeadDao.setSold(coldRvmLead.isSold());
        coldRvmLeadDao.setWrongNumber(coldRvmLead.isWrongNumber());
        coldRvmLeadDao.setOfferMade(coldRvmLead.isOfferMade());
        coldRvmLeadDao.setLeadSentToAgent(coldRvmLead.isLeadSentToAgent());

        coldRvmLeadDao.setPerson(saveOrUpdatePerson(coldRvmLead.getPerson(), session));

        coldRvmLeadDao.setProperty(saveOrUpdateProperty(coldRvmLead.getProperty(), session));

        return coldRvmLeadDao;
    }

    private PropertyDao saveOrUpdateProperty(Property property, Session session) {
        //
        //Search for property - or create new one
        //
        PropertyDao propertyDao = session.byNaturalId(PropertyDao.class)
                .using("naturalId", property.getNaturalId())
                .load();
        if (propertyDao == null) {
            propertyDao = new PropertyDao();
        }

        //
        //Fill in data
        //
        fillPropertyDbFromProperty(property, propertyDao);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(propertyDao);

        return propertyDao;
    }

    private PersonDao saveOrUpdatePerson(Person person, Session session) {
        //
        //Search for property - or create new one
        //
        PersonDao personDao = session.byNaturalId(PersonDao.class)
                .using("naturalId", person.getNaturalId())
                .load();
        if (personDao == null) {
            personDao = new PersonDao();
        }

        //
        //Fill in data
        //
        fillPersonDbFromPerson(person, personDao, session);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(personDao);

        return personDao;
    }

    private ColdRvmLeadDao saveOrUpdateColdRvmLead(ColdRvmLead coldRvmLead, Session session) {
        //
        //Search for ColdRvmLead - or create new one
        //
        ColdRvmLeadDao coldRvmLeadDao =  session.byNaturalId(ColdRvmLeadDao.class)
                .using("naturalId", coldRvmLead.getNaturalId())
                .load();
        if (coldRvmLeadDao == null) {
            coldRvmLeadDao = new ColdRvmLeadDao();
        }

        //
        //Fill in data
        //
        fillColdRvmLeadDbFromColdRvmLead(coldRvmLead, coldRvmLeadDao, session);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(coldRvmLeadDao);

        return coldRvmLeadDao;
    }
}
