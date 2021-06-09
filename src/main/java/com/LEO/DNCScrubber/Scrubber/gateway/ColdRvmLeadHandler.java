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

import com.LEO.DNCScrubber.Scrubber.gateway.model.ColdRvmLeadDb;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDb;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PropertyDb;
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


    private void fillPropertyDbFromProperty(Property property, PropertyDb propertyDb) {
        propertyDb.setNaturalId(property.getNaturalId());
        propertyDb.setAddress(property.getAddress().getMailingAddress());
        propertyDb.setUnitNumber(property.getAddress().getUnitNumber());
        propertyDb.setCity(property.getAddress().getCity());
        propertyDb.setState(property.getAddress().getState());
        propertyDb.setZip(property.getAddress().getZip());
        propertyDb.setCounty(property.getAddress().getCounty());
        propertyDb.setCountry(property.getAddress().getCountry());

        propertyDb.setaPN(property.getaPN());
        propertyDb.setOwnerOccupied(property.isOwnerOccupied());
        propertyDb.setCompanyName(property.getCompanyName());
        propertyDb.setPropertyType(property.getPropertyType());
        propertyDb.setBedrooms(property.getBedrooms());
        propertyDb.setTotalBathrooms(property.getTotalBathrooms());
        propertyDb.setSqft(property.getSqft());
        propertyDb.setLoftSizeSqft(property.getLoftSizeSqft());
        propertyDb.setYearBuilt(property.getYearBuilt());
        propertyDb.setAssessedValue(property.getAssessedValue());

        propertyDb.setLastSaleAmount(property.getLastSaleAmount());
        propertyDb.setTotalOpenLoans(property.getTotalOpenLoans());
        propertyDb.setEstimatedValue(property.getEstimatedValue());
        propertyDb.setEstimatedEquity(property.getEstimatedEquity());

        propertyDb.setmLSStatus(property.getmLSStatus());

        propertyDb.setmLSDate(property.getMlsDate());

        propertyDb.setmLSAmount(property.getmLSAmount());

        propertyDb.setLienAmount(property.getLienAmount());

        propertyDb.setDateAddedToList(property.getDateAddedToList());
    }

    private void fillPersonDbFromPerson(Person person, PersonDb personDb, Session session) {
        personDb.setNaturalId(person.getNaturalId());
        personDb.setFirstName(person.getFirstName());
        personDb.setLastName(person.getLastName());

        personDb.setAddress(person.getAddress() != null ? person.getAddress().getMailingAddress() : "");
        personDb.setUnitNumber(person.getAddress() != null ? person.getAddress().getUnitNumber() : "");
        personDb.setCity(person.getAddress() != null ? person.getAddress().getCity() : "");
        personDb.setState(person.getAddress() != null ? person.getAddress().getState() : "");
        personDb.setZip(person.getAddress() != null ? person.getAddress().getZip() : "");
        personDb.setCounty(person.getAddress() != null ? person.getAddress().getCounty() : "");
        personDb.setCounty(person.getAddress() != null ? person.getAddress().getCountry() : "");

        personDb.setPhone1(person.getPhone1() != null ? person.getPhone1().getPhoneNumber() : "");
        personDb.setPhone1Type(person.getPhone1() != null ? person.getPhone1().getPhoneType() : "");
        personDb.setPhone1DNC(person.getPhone1() != null && person.getPhone1().isPhoneDNC());
        personDb.setPhone1Stop(person.getPhone1() != null && person.getPhone1().isPhoneStop());
        personDb.setPhone1Telco(person.getPhone1() != null ? person.getPhone1().getPhoneTelco() : "");

        personDb.setPhone2(person.getPhone2() != null ? person.getPhone2().getPhoneNumber() : "");
        personDb.setPhone2Type(person.getPhone2() != null ? person.getPhone2().getPhoneType() : "");
        personDb.setPhone2DNC(person.getPhone2() != null && person.getPhone2().isPhoneDNC());
        personDb.setPhone2Stop(person.getPhone2() != null && person.getPhone2().isPhoneStop());
        personDb.setPhone2Telco(person.getPhone2() != null ? person.getPhone2().getPhoneTelco() : "");

        personDb.setPhone3(person.getPhone3() != null ? person.getPhone3().getPhoneNumber() : "");
        personDb.setPhone3Type(person.getPhone3() != null ? person.getPhone3().getPhoneType() : "");
        personDb.setPhone3DNC(person.getPhone3() != null && person.getPhone3().isPhoneDNC());
        personDb.setPhone3Stop(person.getPhone3() != null && person.getPhone3().isPhoneStop());
        personDb.setPhone3Telco(person.getPhone3() != null ? person.getPhone3().getPhoneTelco() : "");

        personDb.setEmail1(person.getEmail1());
        personDb.setEmail2(person.getEmail2());
        personDb.setEmail3(person.getEmail3());

        for(Property property : person.getPropertyList()) {
            PropertyDb propertyDb = saveOrUpdateProperty(property, session);
            personDb.addProperty(propertyDb);
        }
    }

    private ColdRvmLeadDb fillColdRvmLeadDbFromColdRvmLead(ColdRvmLead coldRvmLead, ColdRvmLeadDb coldRvmLeadDb,
                                                           Session session) {
        coldRvmLeadDb.setNaturalId(coldRvmLead.getNaturalId());
        coldRvmLeadDb.setDateWorkflowStarted(coldRvmLead.getDateWorkflowStarted());
        coldRvmLeadDb.setConversationStarted(coldRvmLead.isConversationStarted());
        coldRvmLeadDb.setToldToStop(coldRvmLead.isToldToStop());
        coldRvmLeadDb.setSold(coldRvmLead.isSold());
        coldRvmLeadDb.setWrongNumber(coldRvmLead.isWrongNumber());
        coldRvmLeadDb.setOfferMade(coldRvmLead.isOfferMade());
        coldRvmLeadDb.setLeadSentToAgent(coldRvmLead.isLeadSentToAgent());

        coldRvmLeadDb.setPerson(saveOrUpdatePerson(coldRvmLead.getPerson(), session));

        coldRvmLeadDb.setProperty(saveOrUpdateProperty(coldRvmLead.getProperty(), session));

        return coldRvmLeadDb;
    }

    private PropertyDb saveOrUpdateProperty(Property property, Session session) {
        //
        //Search for property - or create new one
        //
        PropertyDb propertyDb = session.byNaturalId(PropertyDb.class)
                .using("naturalId", property.getNaturalId())
                .load();
        if (propertyDb == null) {
            propertyDb = new PropertyDb();
        }

        //
        //Fill in data
        //
        fillPropertyDbFromProperty(property, propertyDb);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(propertyDb);

        return propertyDb;
    }

    private PersonDb saveOrUpdatePerson(Person person, Session session) {
        //
        //Search for property - or create new one
        //
        PersonDb personDb = session.byNaturalId(PersonDb.class)
                .using("naturalId", person.getNaturalId())
                .load();
        if (personDb == null) {
            personDb = new PersonDb();
        }

        //
        //Fill in data
        //
        fillPersonDbFromPerson(person, personDb, session);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(personDb);

        return personDb;
    }

    private ColdRvmLeadDb saveOrUpdateColdRvmLead(ColdRvmLead coldRvmLead, Session session) {
        //
        //Search for ColdRvmLead - or create new one
        //
        ColdRvmLeadDb coldRvmLeadDb =  session.byNaturalId(ColdRvmLeadDb.class)
                .using("naturalId", coldRvmLead.getNaturalId())
                .load();
        if (coldRvmLeadDb == null) {
            coldRvmLeadDb = new ColdRvmLeadDb();
        }

        //
        //Fill in data
        //
        fillColdRvmLeadDbFromColdRvmLead(coldRvmLead, coldRvmLeadDb, session);

        //
        //Save / Update Property
        //
        session.saveOrUpdate(coldRvmLeadDb);

        return coldRvmLeadDb;
    }
}
