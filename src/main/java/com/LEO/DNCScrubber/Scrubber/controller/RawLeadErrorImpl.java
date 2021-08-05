package com.LEO.DNCScrubber.Scrubber.controller;/*
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

import com.LEO.DNCScrubber.Scrubber.model.data.RawLead;

import java.util.Date;

/**
 * Represents an expected error when processing a {@link RawLead}.
 *
 */
public class RawLeadErrorImpl implements RawLead {
    private String errorMsg;

    /**
     * Constructor - create the class with specific error message.
     * @param errorMsg - error message
     */
    public RawLeadErrorImpl(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMsg;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public String getUnitNumber() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getZip() {
        return null;
    }

    @Override
    public String getCounty() {
        return null;
    }

    @Override
    public String getAPN() {
        return null;
    }

    @Override
    public boolean isOwnerOccupied() {
        return false;
    }

    @Override
    public String getOwnerOneFirstName() {
        return null;
    }

    @Override
    public String getOwnerOneLastName() {
        return null;
    }

    @Override
    public String getCompanyName() {
        return null;
    }

    @Override
    public String getCompanyAddress() {
        return null;
    }

    @Override
    public String getOwnerTwoFirstName() {
        return null;
    }

    @Override
    public String getOwnerTwoLastName() {
        return null;
    }

    @Override
    public String getMailingCareOfName() {
        return null;
    }

    @Override
    public String getMailingAddress() {
        return null;
    }

    @Override
    public String getMailingUnitNumber() {
        return null;
    }

    @Override
    public String getMailingCity() {
        return null;
    }

    @Override
    public String getMailingState() {
        return null;
    }

    @Override
    public String getMailingZip() {
        return null;
    }

    @Override
    public String getMailingCounty() {
        return null;
    }

    @Override
    public String getDoNotMail() {
        return null;
    }

    @Override
    public String getPropertyType() {
        return null;
    }

    @Override
    public float getBedrooms() {
        return 0;
    }

    @Override
    public float getTotalBathrooms() {
        return 0;
    }

    @Override
    public int getBuildingSqft() {
        return 0;
    }

    @Override
    public int getLotSizeSqft() {
        return 0;
    }

    @Override
    public int getEffectiveYearBuilt() {
        return 0;
    }

    @Override
    public int getTotalAssessedValue() {
        return 0;
    }

    @Override
    public Date getLastSaleRecordingDate() {
        return null;
    }

    @Override
    public int getLastSaleAmount() {
        return 0;
    }

    @Override
    public int getTotalOpenLoans() {
        return 0;
    }

    @Override
    public int getEstRemainingBalanceOfOpenLoans() {
        return 0;
    }

    @Override
    public int getEstValue() {
        return 0;
    }

    @Override
    public float getEstLoanToValue() {
        return 0;
    }

    @Override
    public int getEstEquity() {
        return 0;
    }

    @Override
    public String getMLSStatus() {
        return null;
    }

    @Override
    public Date getMLSDate() {
        return null;
    }

    @Override
    public int getMLSAmount() {
        return 0;
    }

    @Override
    public int getLienAmount() {
        return 0;
    }

    @Override
    public int getMarketingLists() {
        return 0;
    }

    @Override
    public Date getDateAddedToList() {
        return null;
    }
}
