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
 * {@link DatabaseGateway} implementation using Hibernate. https://hibernate.org/
 */
public class DatabaseGatewayImpl implements DatabaseGateway{
    final static Logger logger = LoggerFactory.getLogger(DatabaseGatewayImpl.class);
    private final HibernateUtil hibernateUtil;

    /**
     * Constructor
     * @param hibernateUtil - for DB interaction
     */
    public DatabaseGatewayImpl(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public Observable<Boolean> writeRawLead(ColdRvmLead coldRvmLead) {
        return Observable.create(emitter -> {
            ColdRvmLeadDb coldRvmLeadDb = convertToColdRvmLeadDB(coldRvmLead);

            Transaction transaction = null;
            try (Session session = hibernateUtil.getSessionFactory().openSession()) {
                // start a transaction
                transaction = session.beginTransaction();
                // save the student objects
                session.save(coldRvmLeadDb);
                // commit transaction
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

    private PropertyDb convertToPropertyDb(Property property) {
        PropertyDb propertyDb = new PropertyDb();
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

        return propertyDb;
    }

    private PersonDb convertToPersonDb(Person person) {
        PersonDb personDb = new PersonDb();
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
            personDb.addProperty(this.convertToPropertyDb(property));
        }

        return personDb;
    }

    private ColdRvmLeadDb convertToColdRvmLeadDB(ColdRvmLead coldRvmLead) {
        ColdRvmLeadDb coldRvmLeadDb = new ColdRvmLeadDb();

        coldRvmLeadDb.setDateWorkflowStarted(coldRvmLead.getDateWorkflowStarted());
        coldRvmLeadDb.setConversationStarted(coldRvmLead.isConversationStarted());
        coldRvmLeadDb.setToldToStop(coldRvmLead.isToldToStop());
        coldRvmLeadDb.setSold(coldRvmLead.isSold());
        coldRvmLeadDb.setWrongNumber(coldRvmLead.isWrongNumber());
        coldRvmLeadDb.setOfferMade(coldRvmLead.isOfferMade());
        coldRvmLeadDb.setLeadSentToAgent(coldRvmLead.isLeadSentToAgent());

        coldRvmLeadDb.setPerson(this.convertToPersonDb(coldRvmLead.getPerson()));
        coldRvmLeadDb.setProperty(this.convertToPropertyDb(coldRvmLead.getProperty()));

        return coldRvmLeadDb;
    }

}
