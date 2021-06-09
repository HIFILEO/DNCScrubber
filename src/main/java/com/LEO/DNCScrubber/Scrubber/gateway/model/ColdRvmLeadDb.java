package com.LEO.DNCScrubber.Scrubber.gateway.model;/*
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

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ColdRvmLead")
public class ColdRvmLeadDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId(mutable=true)
    @Column(nullable = false, unique = true)
    private String naturalId = "";

    //Note = https://www.baeldung.com/hibernate-date-time = only holds yyyy-MM-dd, no time units
    @Basic
    @Temporal(TemporalType.DATE)
    private Date dateWorkflowStarted = new Date();

    private boolean conversationStarted;
    private boolean toldToStop;
    private boolean sold;
    private boolean wrongNumber;
    private boolean offerMade;
    private boolean leadSentToAgent;

    /*
    Note - Although a bi-directional could have been used, the relationship does not warrant it. You have a person
    show up multiple times in a Cold RVM Lead table.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="Person_id")
    private PersonDb person;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="Property_id")
    private PropertyDb property;

    public Date getDateWorkflowStarted() {
        return dateWorkflowStarted;
    }

    public void setDateWorkflowStarted(Date dateWorkflowStarted) {
        this.dateWorkflowStarted = dateWorkflowStarted;
    }

    public boolean isConversationStarted() {
        return conversationStarted;
    }

    public void setConversationStarted(boolean conversationStarted) {
        this.conversationStarted = conversationStarted;
    }

    public boolean isToldToStop() {
        return toldToStop;
    }

    public void setToldToStop(boolean toldToStop) {
        this.toldToStop = toldToStop;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public boolean isWrongNumber() {
        return wrongNumber;
    }

    public void setWrongNumber(boolean wrongNumber) {
        this.wrongNumber = wrongNumber;
    }

    public boolean isOfferMade() {
        return offerMade;
    }

    public void setOfferMade(boolean offerMade) {
        this.offerMade = offerMade;
    }

    public boolean isLeadSentToAgent() {
        return leadSentToAgent;
    }

    public void setLeadSentToAgent(boolean leadSentToAgent) {
        this.leadSentToAgent = leadSentToAgent;
    }

    public PersonDb getPerson() {
        return person;
    }

    public void setPerson(PersonDb person) {
        this.person = person;
    }

    public PropertyDb getProperty() {
        return property;
    }

    public void setProperty(PropertyDb property) {
        this.property = property;
    }

    public String getNaturalId() {
        return naturalId;
    }

    public void setNaturalId(String naturalId) {
        this.naturalId = naturalId;
    }
}
