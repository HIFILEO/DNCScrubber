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
import com.google.common.annotations.VisibleForTesting;
import org.hibernate.Session;

import java.util.Optional;

/**
 * The purpose of this class is to help translate business objects to database objects.
 */
public class DatabaseHelper {

    /**
     * Saves {@link ColdRvmLead} to the database.
     *
     * Simplifies the need to search for primary ID that's auto generated before saving. Transactions must
     * be committed outside this call.
     *
     *  Furthermore, hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
     *  Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
     *  the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
     *  entry exceptions.
     *
     * @param session - current {@link Session} to execute transaction
     * @param coldRvmLead - {@link ColdRvmLead} to save into database
     * @return {@link ColdRvmLeadDao} that was saved to database
     */
    public ColdRvmLeadDao saveColdRvmLead(Session session, ColdRvmLead coldRvmLead) {
        //
        //Search for ColdRvmLeadDao - or create new one
        //
        ColdRvmLeadDao coldRvmLeadDao = this.findColdRvmLeadByNaturalId(session, coldRvmLead.getNaturalId())
                .orElseGet(ColdRvmLeadDao::new);

        //
        //Fill in data
        //
        fillColdRvmLeadDbFromColdRvmLead(session, coldRvmLead, coldRvmLeadDao);

        //
        //Save / Update ColdRvmLead (Remember, recursive calls to save were done in fill*())
        //
        session.saveOrUpdate(coldRvmLeadDao);

        return coldRvmLeadDao;
    }

    /**
     * Saves {@link Person} to the database.
     *
     * Simplifies the need to search for primary ID that's auto generated before saving. Transactions must
     * be committed outside this call.
     *
     *  Furthermore, hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
     *  Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
     *  the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
     *  entry exceptions.
     *
     * @param session - current {@link Session} to execute transaction
     * @param person - {@link Person} to save into database
     * @return {@link PersonDao} that was saved to database
     */
    public PersonDao savePerson(Session session, Person person) {
        //
        //Search for Person - or create new one
        //
        PersonDao personDao = this.findPersonByNaturalId(session, person.getNaturalId()).orElseGet(PersonDao::new);

        //
        //Fill in data
        //
        fillPersonDbFromPerson(session, person, personDao);

        //
        //Save / Update Property (Remember, recursive calls to save were done in fill*())
        //
        session.saveOrUpdate(personDao);

        return personDao;

    }

    /**
     * Saves {@link Property} to the database.
     *
     * Simplifies the need to search for primary ID that's auto generated before saving. Transactions must
     * be committed outside this call.
     *
     *  Furthermore, hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
     *  Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
     *  the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
     *  entry exceptions.
     *
     * @param session - current {@link Session} to execute transaction
     * @param property - {@link Property} to save into database
     * @return {@link PropertyDao} that was saved to database
     */
    public PropertyDao saveProperty(Session session, Property property) {
        //
        //Search for property - or create new one
        //
        PropertyDao propertyDao = findPropertyByNaturalId(session, property.getNaturalId()).orElseGet(PropertyDao::new);

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

    /**
     * Find the {@link PropertyDao} associated with the Natural ID.
     *
     * DAO returned is in Persistent Object State.
     * @param session - current session to run the query on
     * @param naturalId - naturalID to search for property
     * @return - {@link PropertyDao} from Database when present.
     */
    public Optional<PropertyDao> findPropertyByNaturalId(Session session, String naturalId) {
        PropertyDao propertyDao = session.byNaturalId(PropertyDao.class)
                .using("naturalId", naturalId)
                .load();

        return Optional.ofNullable(propertyDao);
    }

    /**
     * Find the {@link PersonDao} associated with the Natural ID.
     *
     * DAO returned is in Persistent Object State.
     * @param session - current session to run the query on
     * @param naturalId - naturalID to search for property
     * @return - {@link PersonDao} from Database when present.
     */
    public Optional<PersonDao> findPersonByNaturalId(Session session, String naturalId) {
        PersonDao personDao = session.byNaturalId(PersonDao.class)
                .using("naturalId", naturalId)
                .load();

        return Optional.ofNullable(personDao);
    }

    /**
     * Find the {@link ColdRvmLeadDao} associated with the Natural ID.
     *
     * DAO returned is in Persistent Object State.
     * @param session - current session to run the query on
     * @param naturalId - naturalID to search for property
     * @return - {@link ColdRvmLeadDao} from Database when present.
     */
    public Optional<ColdRvmLeadDao> findColdRvmLeadByNaturalId(Session session, String naturalId) {
        ColdRvmLeadDao coldRvmLeadDao = session.byNaturalId(ColdRvmLeadDao.class)
                .using("naturalId", naturalId)
                .load();

        return Optional.ofNullable(coldRvmLeadDao);
    }

    /**
     * Translate {@link PropertyDao} to {@link Property}
     * @param propertyDao - {@link PropertyDao} to translate
     * @return - new filled in {@link Property}
     */
    public Property translatePropertyDbToProperty(PropertyDao propertyDao) {
        Address address = new Address(
                propertyDao.getAddress(),
                propertyDao.getUnitNumber(),
                propertyDao.getCity(),
                propertyDao.getState(),
                propertyDao.getZip(),
                propertyDao.getCounty(),
                propertyDao.getCountry()
        );

        Property property = new Property(propertyDao.getaPN(), address);
        property.setOwnerOccupied(propertyDao.isOwnerOccupied());
        property.setCompanyName(propertyDao.getCompanyName());
        property.setCompanyAddress(propertyDao.getCompanyAddress());
        property.setPropertyType(propertyDao.getPropertyType());
        property.setBedrooms(propertyDao.getBedrooms());
        property.setTotalBathrooms(propertyDao.getTotalBathrooms());
        property.setSqft(propertyDao.getSqft());
        property.setLotSizeSqft(propertyDao.getLoftSizeSqft());
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

    /**
     * Translate {@link PersonDao} to {@link Person}
     * @param personDao - {@link PersonDao} to translate
     * @return - new filled in {@link Person}
     */
    public Person translatePersonDbToPerson(PersonDao personDao) {
        Address address = new Address(
                personDao.getAddress(),
                personDao.getUnitNumber(),
                personDao.getCity(),
                personDao.getState(),
                personDao.getZip(),
                personDao.getCounty(),
                personDao.getCountry()
        );

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

        Person person = new Person(
                personDao.getFirstName(),
                personDao.getLastName(),
                address);
        person.setPhone1(phone1);
        person.setPhone2(phone2);
        person.setPhone3(phone3);
        person.setEmail1(personDao.getEmail1());
        person.setEmail2(personDao.getEmail2());
        person.setEmail3(personDao.getEmail3());

        for (PropertyDao propertyDao : personDao.getProperties()) {
            person.addProperty(translatePropertyDbToProperty(propertyDao));
        }

        return person;
    }

    /**
     * Translate {@link ColdRvmLeadDao} to {@link ColdRvmLead}
     * @param coldRvmLeadDao - {@link ColdRvmLeadDao} to translate
     * @return - new filled in {@link ColdRvmLead}
     */
    public ColdRvmLead translateColdRvmLeadDbToColdRvmLead(ColdRvmLeadDao coldRvmLeadDao) {
        Person person = translatePersonDbToPerson(coldRvmLeadDao.getPerson());
        Property property = translatePropertyDbToProperty(coldRvmLeadDao.getProperty());

        ColdRvmLead coldRvmLead = new ColdRvmLead(person, property);
        coldRvmLead.setDateWorkflowStarted(coldRvmLeadDao.getDateWorkflowStarted());
        coldRvmLead.setConversationStarted(coldRvmLeadDao.isConversationStarted());
        coldRvmLead.setToldToStop(coldRvmLeadDao.isToldToStop());
        coldRvmLead.setSold(coldRvmLeadDao.isSold());
        coldRvmLead.setWrongNumber(coldRvmLeadDao.isWrongNumber());
        coldRvmLead.setOfferMade(coldRvmLeadDao.isOfferMade());
        coldRvmLead.setLeadSentToAgent(coldRvmLeadDao.isLeadSentToAgent());

        return coldRvmLead;
    }

    /**
     * Translate {@link Property} into {@link PropertyDao}
     *
     * Only for testing. Instead, directly save business logic classes and translate in those functions. This
     * avoids the problem of DAO sessions expiring and/or missing primary key IDs.
     *
     * @param session - current {@link Session} to perform DB executions on
     * @param property - {@link Property} to translate for database
     * @return - translated {@link PropertyDao}
     */
    @VisibleForTesting
    protected PropertyDao translatePropertyToPropertyDao(Session session, Property property) {
        /*
            Hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
             Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
            the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
            entry exceptions.
         */
        PropertyDao propertyDao;
        Optional<PropertyDao> optionalPropertyDao = findPropertyByNaturalId(session, property.getNaturalId());

        if (optionalPropertyDao.isPresent()) {
            propertyDao = optionalPropertyDao.get();
        } else {
            propertyDao = new PropertyDao();
        }

        fillPropertyDbFromProperty(property, propertyDao);
        return propertyDao;
    }

    /**
     * Translate {@link Person} into {@link PersonDao}
     *
     * Only for testing. Instead, directly save business logic classes and translate in those functions. This
     * avoids the problem of DAO sessions expiring and/or missing primary key IDs.
     *
     * @param session - current {@link Session} to perform DB executions on
     * @param person - {@link Person} to translate for database
     * @return - translated {@link PersonDao}
     */
    @VisibleForTesting
    protected PersonDao translatePersonToPersonDb(Session session, Person person) {
        /*
            Hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
             Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
            the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
            entry exceptions.
         */
        PersonDao personDao;
        Optional<PersonDao> personDaoOptional = findPersonByNaturalId(session, person.getNaturalId());

        if (personDaoOptional.isPresent()) {
            personDao = personDaoOptional.get();
        } else {
            personDao = new PersonDao();
        }

        fillPersonDbFromPerson(session, person, personDao);
        return personDao;
    }

    /**
     * Translate {@link ColdRvmLead} into {@link ColdRvmLeadDao}
     *
     * Only for testing. Instead, directly save business logic classes and translate in those functions. This
     * avoids the problem of DAO sessions expiring and/or missing primary key IDs.
     *
     * @param session - current {@link Session} to perform DB executions on
     * @param coldRvmLead - {@link ColdRvmLead} to translate for database
     * @return - translated {@link ColdRvmLeadDao}
     */
    @VisibleForTesting
    protected ColdRvmLeadDao translateColdRvmLeadToColdRvmLeadDb(Session session, ColdRvmLead coldRvmLead) {
        /*
            Hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
             Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
            the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
            entry exceptions.
         */
        ColdRvmLeadDao coldRvmLeadDao;
        Optional<ColdRvmLeadDao> coldRvmLeadDaoOptional = findColdRvmLeadByNaturalId(session, coldRvmLead.getNaturalId());

        coldRvmLeadDao = coldRvmLeadDaoOptional.orElseGet(ColdRvmLeadDao::new);

        fillColdRvmLeadDbFromColdRvmLead(session, coldRvmLead, coldRvmLeadDao);
        return coldRvmLeadDao;
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
        propertyDao.setCompanyAddress(property.getCompanyAddress());
        propertyDao.setPropertyType(property.getPropertyType());
        propertyDao.setBedrooms(property.getBedrooms());
        propertyDao.setTotalBathrooms(property.getTotalBathrooms());
        propertyDao.setSqft(property.getSqft());
        propertyDao.setLoftSizeSqft(property.getLotSizeSqft());
        propertyDao.setYearBuilt(property.getYearBuilt());
        propertyDao.setAssessedValue(property.getAssessedValue());

        propertyDao.setLastSaleRecordingDate(property.getLastSaleRecordingDate());
        propertyDao.setLastSaleAmount(property.getLastSaleAmount());
        propertyDao.setTotalOpenLoans(property.getTotalOpenLoans());
        propertyDao.setEstimatedRemainingBalance(property.getEstimatedRemainingBalance());
        propertyDao.setEstimatedValue(property.getEstimatedValue());
        propertyDao.setEstimatedEquity(property.getEstimatedEquity());

        propertyDao.setmLSStatus(property.getmLSStatus());

        propertyDao.setmLSDate(property.getMlsDate());

        propertyDao.setmLSAmount(property.getmLSAmount());

        propertyDao.setLienAmount(property.getLienAmount());

        propertyDao.setDateAddedToList(property.getDateAddedToList());
    }

    private void fillPersonDbFromPerson(Session session, Person person, PersonDao personDao) {
        personDao.setNaturalId(person.getNaturalId());
        personDao.setFirstName(person.getFirstName());
        personDao.setLastName(person.getLastName());

        personDao.setAddress(person.getAddress() != null ? person.getAddress().getMailingAddress() : "");
        personDao.setUnitNumber(person.getAddress() != null ? person.getAddress().getUnitNumber() : "");
        personDao.setCity(person.getAddress() != null ? person.getAddress().getCity() : "");
        personDao.setState(person.getAddress() != null ? person.getAddress().getState() : "");
        personDao.setZip(person.getAddress() != null ? person.getAddress().getZip() : "");
        personDao.setCounty(person.getAddress() != null ? person.getAddress().getCounty() : "");
        personDao.setCountry(person.getAddress() != null ? person.getAddress().getCountry() : "");

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
            PropertyDao propertyDao = saveProperty(session, property);

            //add propertyDao to personDao
            personDao.addProperty(propertyDao);
        }
    }

    private void fillColdRvmLeadDbFromColdRvmLead(Session session, ColdRvmLead coldRvmLead, ColdRvmLeadDao coldRvmLeadDao) {
        coldRvmLeadDao.setNaturalId(coldRvmLead.getNaturalId());
        coldRvmLeadDao.setDateWorkflowStarted(coldRvmLead.getDateWorkflowStarted());
        coldRvmLeadDao.setConversationStarted(coldRvmLead.isConversationStarted());
        coldRvmLeadDao.setToldToStop(coldRvmLead.isToldToStop());
        coldRvmLeadDao.setSold(coldRvmLead.isSold());
        coldRvmLeadDao.setWrongNumber(coldRvmLead.isWrongNumber());
        coldRvmLeadDao.setOfferMade(coldRvmLead.isOfferMade());
        coldRvmLeadDao.setLeadSentToAgent(coldRvmLead.isLeadSentToAgent());

        //Person - You have to save recursive in order to fill
        coldRvmLeadDao.setPerson(savePerson(session, coldRvmLead.getPerson()));

        //Property - You have to save recursive in order to fill
        coldRvmLeadDao.setProperty(saveProperty(session, coldRvmLead.getProperty()));
    }
}
