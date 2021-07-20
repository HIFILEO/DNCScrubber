package com.LEO.DNCScrubber.Scrubber.interactor;/*
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

import com.LEO.DNCScrubber.Scrubber.model.data.*;

/**
 * Converts {@link com.LEO.DNCScrubber.Scrubber.model.data.RawLead} to
 * {@link com.LEO.DNCScrubber.Scrubber.model.data.ColdRvmLead}
 */
public class RawLeadConvertor {

    public ColdRvmLead convertRawLeadToColdRvmLead(RawLead rawLead) {
        //
        // Create Property
        //
        Address address = new Address(
                rawLead.getMailingAddress(),
                rawLead.getUnitNumber(),
                rawLead.getCity(),
                rawLead.getState(),
                rawLead.getZip(),
                rawLead.getCounty(),
                "US"
        );

        Property property = new Property(
                rawLead.getAPN(),
                address);
        property.setOwnerOccupied(rawLead.isOwnerOccupied());
        property.setCompanyName(rawLead.getCompanyName());
        property.setCompanyAddress(rawLead.getCompanyAddress());
        property.setPropertyType(rawLead.getPropertyType());
        property.setBedrooms(Float.toString(rawLead.getBedrooms()));
        property.setTotalBathrooms(Float.toString(rawLead.getTotalBathrooms()));
        property.setSqft(rawLead.getBuildingSqft());
        property.setLotSizeSqft(rawLead.getLotSizeSqft());
        property.setYearBuilt(rawLead.getEffectiveYearBuilt());
        property.setAssessedValue(rawLead.getTotalAssessedValue());
        property.setLastSaleRecordingDate(rawLead.getLastSaleRecordingDate());
        property.setLastSaleAmount(rawLead.getLastSaleAmount());
        property.setTotalOpenLoans(rawLead.getTotalOpenLoans());
        property.setEstimatedRemainingBalance(rawLead.getEstRemainingBalanceOfOpenLoans());
        property.setEstimatedValue(rawLead.getEstValue());
        property.setEstimatedEquity(rawLead.getEstEquity());
        property.setmLSStatus(rawLead.getMLSStatus());
        property.setMlsDate(rawLead.getMLSDate());
        property.setmLSAmount(Integer.toString(rawLead.getMLSAmount()));
        property.setLienAmount(Integer.toString(rawLead.getLienAmount()));
        property.setDateAddedToList(rawLead.getDateAddedToList());

        //
        // Create Person
        //

        //Note - reminder no phone number or email here
        Person person = new Person(
                rawLead.getOwnerOneFirstName(),
                rawLead.getOwnerOneLastName(), address
        );
        person.addProperty(property);

        //
        // Create ColdRvmLead
        //
        ColdRvmLead coldRvmLead = new ColdRvmLead();
        coldRvmLead.setDateWorkflowStarted(rawLead.getDateAddedToList());
        coldRvmLead.setPerson(person);
        coldRvmLead.setProperty(property);

        return coldRvmLead;
    }
}
