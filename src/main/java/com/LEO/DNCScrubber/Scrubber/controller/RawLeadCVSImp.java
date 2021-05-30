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
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class RawLeadCVSImp implements RawLead {

    @CsvBindByName(column = "Address", required = true)
    private String address;

    @CsvBindByName(column = "Unit #", required = true)
    private String unitNumber;

    @CsvBindByName(column = "City", required = true)
    private String city;

    @CsvBindByName(column = "State", required = true)
    private String state;

    @CsvBindByName(column = "Zip", required = true)
    private String zip;

    @CsvBindByName(column = "County", required = true)
    private String county;

    @CsvBindByName(column = "APN", required = true)
    private String aBN;

    @CsvBindByName(column = "Owner Occupied", required = true)
    private boolean ownerOccupied;

    @CsvBindByName(column = "Owner 1 First Name", required = true)
    private String ownerOneFirstName;

    @CsvBindByName(column = "Owner 1 Last Name", required = true)
    private String ownerOneLastName;

    @CsvBindByName(column = "Company Name", required = true)
    private String companyName;

    @CsvBindByName(column = "Owner 2 First Name", required = true)
    private String ownerTwoFirstName;

    @CsvBindByName(column = "Owner 2 Last Name", required = true)
    private String ownerTwoLastName;

    @CsvBindByName(column = "Mailing Care of Name", required = true)
    private String mailingCareOfName;

    @CsvBindByName(column = "Mailing Address", required = true)
    private String mailingAddress;

    @CsvBindByName(column = "Mailing Unit #", required = true)
    private String mailingUnitNumber;

    @CsvBindByName(column = "Mailing City", required = true)
    private String mailingCity;

    @CsvBindByName(column = "Mailing State", required = true)
    private String mailingState;

    @CsvBindByName(column = "Mailing Zip", required = true)
    private String mailingZip;

    @CsvBindByName(column = "Mailing County", required = true)
    private String mailingCounty;

    @CsvBindByName(column = "Do Not Mail", required = true)
    private String doNotMail;

    @CsvBindByName(column = "Property Type", required = true)
    private String propertyType;

    @CsvBindByName(column = "Bedrooms", required = true)
    private float bedrooms;

    @CsvBindByName(column = "Total Bathrooms", required = true)
    private float totalBathrooms;

    @CsvBindByName(column = "Building Sqft", required = true)
    private int buildingSqft;

    @CsvBindByName(column = "Lot Size Sqft", required = true)
    private int lotSizeSqft;

    @CsvBindByName(column = "Effective Year Built", required = true)
    private int effectiveYearBuilt;

    @CsvBindByName(column = "Total Assessed Value", required = true)
    private int totalAssessedValue;

    @CsvBindByName(column = "Last Sale Recording Date", required = true)
    @CsvDate("yyyy-MM-dd")
    private Date lastSaleRecordingDate;

    @CsvBindByName(column = "Last Sale Amount", required = true)
    private int lastSaleAmount;

    @CsvBindByName(column = "Total Open Loans", required = true)
    private int totalOpenLoans;

    @CsvBindByName(column = "Est. Remaining balance of Open Loans", required = true)
    private int estRemainingBalanceOfOpenLoans;

    @CsvBindByName(column = "Est. Value", required = true)
    private int estValue;

    @CsvBindByName(column = "Est. Loan-to-Value", required = true)
    private float estLoanToValue;

    @CsvBindByName(column = "Est. Equity", required = true)
    private int estEquity;

    @CsvBindByName(column = "MLS Status", required = true)
    private String mLSStatus;

    @CsvBindByName(column = "MLS Date", required = true)
    @CsvDate("yyyy-MM-dd")
    private Date mLSDate;

    @CsvBindByName(column = "MLS Amount", required = true)
    private int mLSAmount;

    @CsvBindByName(column = "Lien Amount", required = true)
    private int lienAmount;

    @CsvBindByName(column = "Marketing Lists", required = true)
    private int marketingLists;

    @CsvBindByName(column = "Date Added to List", required = true)
    @CsvDate("-MM/dd/yyyy")
    private Date dateAddedToList;

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public String getUnitNumber() {
        return this.unitNumber;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public String getZip() {
        return this.zip;
    }

    @Override
    public String getCounty() {
        return this.county;
    }

    @Override
    public String getABN() {
        return this.aBN;
    }

    @Override
    public boolean isOwnerOccupied() {
        return this.ownerOccupied;
    }

    @Override
    public String getOwnerOneFirstName() {
        return this.ownerOneFirstName;
    }

    @Override
    public String getOwnerOneLastName() {
        return this.ownerOneLastName;
    }

    @Override
    public String getCompanyName() {
        return this.companyName;
    }

    @Override
    public String getOwnerTwoFirstName() {
        return this.ownerTwoFirstName;
    }

    @Override
    public String getOwnerTwoLastName() {
        return this.ownerTwoLastName;
    }

    @Override
    public String getMailingCareOfName() {
        return this.mailingCareOfName;
    }

    @Override
    public String getMailingAddress() {
        return this.mailingAddress;
    }

    @Override
    public String getMailingUnitNumber() {
        return this.mailingUnitNumber;
    }

    @Override
    public String getMailingCity() {
        return this.mailingCity;
    }

    @Override
    public String getMailingState() {
        return this.mailingState;
    }

    @Override
    public String getMailingZip() {
        return this.mailingZip;
    }

    @Override
    public String getMailingCounty() {
        return this.mailingCounty;
    }

    @Override
    public String getDoNotMail() {
        return this.doNotMail;
    }

    @Override
    public String getPropertyType() {
        return this.propertyType;
    }

    @Override
    public float getBedrooms() {
        return this.bedrooms;
    }

    @Override
    public float getTotalBathrooms() {
        return this.totalBathrooms;
    }

    @Override
    public int getBuildingSqft() {
        return this.buildingSqft;
    }

    @Override
    public int getLotSizeSqft() {
        return this.lotSizeSqft;
    }

    @Override
    public int getEffectiveYearBuilt() {
        return this.effectiveYearBuilt;
    }

    @Override
    public int getTotalAssessedValue() {
        return this.totalAssessedValue;
    }

    @Override
    public Date getLastSaleRecordingDate() {
        return this.lastSaleRecordingDate;
    }

    @Override
    public int getLastSaleAmount() {
        return this.lastSaleAmount;
    }

    @Override
    public int getTotalOpenLoans() {
        return this.totalOpenLoans;
    }

    @Override
    public int getEstRemainingBalanceOfOpenLoans() {
        return this.estRemainingBalanceOfOpenLoans;
    }

    @Override
    public int getEstValue() {
        return this.estValue;
    }

    @Override
    public float getEstLoanToValue() {
        return this.estLoanToValue;
    }

    @Override
    public int getEstEquity() {
        return this.estEquity;
    }

    @Override
    public String getMLSStatus() {
        return this.mLSStatus;
    }

    @Override
    public Date getMLSDate() {
        return this.mLSDate;
    }

    @Override
    public int getMLSAmount() {
        return this.mLSAmount;
    }

    @Override
    public int getLienAmount() {
        return this.lienAmount;
    }

    @Override
    public int getMarketingLists() {
        return this.marketingLists;
    }

    @Override
    public Date getDateAddedToList() {
        return this.dateAddedToList;
    }
}
