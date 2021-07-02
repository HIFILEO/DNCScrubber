package com.LEO.DNCScrubber.Scrubber.model.data;/*
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
import java.util.Date;

/**
 * Business layer representation of a raw lead.
 */
public interface RawLead {
    String getAddress();
    String getUnitNumber();
    String getCity();
    String getState();
    String getZip();
    String getCounty();
    String getAPN();
    boolean isOwnerOccupied();
    String getOwnerOneFirstName();
    String getOwnerOneLastName();
    String getCompanyName();
    String getCompanyAddress();
    String getOwnerTwoFirstName();
    String getOwnerTwoLastName();
    String getMailingCareOfName();
    String getMailingAddress();
    String getMailingUnitNumber();
    String getMailingCity();
    String getMailingState();
    String getMailingZip();
    String getMailingCounty();
    String getDoNotMail();
    String getPropertyType();
    float getBedrooms();
    float getTotalBathrooms();
    int getBuildingSqft();
    int getLotSizeSqft();
    int getEffectiveYearBuilt();
    int getTotalAssessedValue();
    Date getLastSaleRecordingDate();
    int getLastSaleAmount();
    int getTotalOpenLoans();
    int getEstRemainingBalanceOfOpenLoans();
    int getEstValue();
    float getEstLoanToValue();
    int getEstEquity();
    String getMLSStatus();
    Date getMLSDate();
    int getMLSAmount();
    int getLienAmount();
    int getMarketingLists();
    Date getDateAddedToList();
}
