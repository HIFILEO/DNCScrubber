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

import com.LEO.DNCScrubber.Scrubber.model.data.RawLead;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RawLead")
public class RawLeadDBImpl implements RawLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String address;
    private String unitNumber;
    private String city;
    private String state;
    private String zip;
    private String county;

    @Column(name = "ABN")
    private String aBN;

    private boolean ownerOccupied;

    @Column(name = "Owner_1_FirstName")
    private String ownerOneFirstName;

    @Column(name = "Owner_1_LastName")
    private String ownerOneLastName;

    private String companyName;

    @Column(name = "Owner_2_FirstName")
    private String ownerTwoFirstName;

    @Column(name = "Owner_2_LastName")
    private String ownerTwoLastName;

    private String mailingCareOfName;
    private String mailingAddress;
    private String mailingUnitNumber;
    private String mailingCity;
    private String mailingState;
    private String mailingZip;
    private String mailingCounty;
    private String doNotMail;
    private String propertyType;
    private float bedrooms;
    private float totalBathrooms;
    private int buildingSqft;
    private int lotSizeSqft;
    private int effectiveYearBuilt;
    private int totalAssessedValue;

    //Note = https://www.baeldung.com/hibernate-date-time = only holds yyyy-MM-dd, no time units
    @Basic
    @Temporal(TemporalType.DATE)
    private Date lastSaleRecordingDate;

    private int lastSaleAmount;
    private int totalOpenLoans;
    private int estRemainingBalanceOfOpenLoans;
    private int estValue;
    private float estLoanToValue;
    private int estEquity;
    private String mLSStatus;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "MLS_Date")
    private Date mLSDate;

    @Column(name = "MLS_Amount")
    private int mLSAmount;

    private int lienAmount;
    private int marketingLists;

    @Basic
    @Temporal(TemporalType.DATE)
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getaBN() {
        return aBN;
    }

    public void setaBN(String aBN) {
        this.aBN = aBN;
    }

    public void setOwnerOccupied(boolean ownerOccupied) {
        this.ownerOccupied = ownerOccupied;
    }

    public void setOwnerOneFirstName(String ownerOneFirstName) {
        this.ownerOneFirstName = ownerOneFirstName;
    }

    public void setOwnerOneLastName(String ownerOneLastName) {
        this.ownerOneLastName = ownerOneLastName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setOwnerTwoFirstName(String ownerTwoFirstName) {
        this.ownerTwoFirstName = ownerTwoFirstName;
    }

    public void setOwnerTwoLastName(String ownerTwoLastName) {
        this.ownerTwoLastName = ownerTwoLastName;
    }

    public void setMailingCareOfName(String mailingCareOfName) {
        this.mailingCareOfName = mailingCareOfName;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public void setMailingUnitNumber(String mailingUnitNumber) {
        this.mailingUnitNumber = mailingUnitNumber;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public void setMailingState(String mailingState) {
        this.mailingState = mailingState;
    }

    public void setMailingZip(String mailingZip) {
        this.mailingZip = mailingZip;
    }

    public void setMailingCounty(String mailingCounty) {
        this.mailingCounty = mailingCounty;
    }

    public void setDoNotMail(String doNotMail) {
        this.doNotMail = doNotMail;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setBedrooms(float bedrooms) {
        this.bedrooms = bedrooms;
    }

    public void setTotalBathrooms(float totalBathrooms) {
        this.totalBathrooms = totalBathrooms;
    }

    public void setBuildingSqft(int buildingSqft) {
        this.buildingSqft = buildingSqft;
    }

    public void setLotSizeSqft(int lotSizeSqft) {
        this.lotSizeSqft = lotSizeSqft;
    }

    public void setEffectiveYearBuilt(int effectiveYearBuilt) {
        this.effectiveYearBuilt = effectiveYearBuilt;
    }

    public void setTotalAssessedValue(int totalAssessedValue) {
        this.totalAssessedValue = totalAssessedValue;
    }

    public void setLastSaleRecordingDate(Date lastSaleRecordingDate) {
        this.lastSaleRecordingDate = lastSaleRecordingDate;
    }

    public void setLastSaleAmount(int lastSaleAmount) {
        this.lastSaleAmount = lastSaleAmount;
    }

    public void setTotalOpenLoans(int totalOpenLoans) {
        this.totalOpenLoans = totalOpenLoans;
    }

    public void setEstRemainingBalanceOfOpenLoans(int estRemainingBalanceOfOpenLoans) {
        this.estRemainingBalanceOfOpenLoans = estRemainingBalanceOfOpenLoans;
    }

    public void setEstValue(int estValue) {
        this.estValue = estValue;
    }

    public void setEstLoanToValue(float estLoanToValue) {
        this.estLoanToValue = estLoanToValue;
    }

    public void setEstEquity(int estEquity) {
        this.estEquity = estEquity;
    }

    public String getmLSStatus() {
        return mLSStatus;
    }

    public void setmLSStatus(String mLSStatus) {
        this.mLSStatus = mLSStatus;
    }

    public Date getmLSDate() {
        return mLSDate;
    }

    public void setmLSDate(Date mLSDate) {
        this.mLSDate = mLSDate;
    }

    public int getmLSAmount() {
        return mLSAmount;
    }

    public void setmLSAmount(int mLSAmount) {
        this.mLSAmount = mLSAmount;
    }

    public void setLienAmount(int lienAmount) {
        this.lienAmount = lienAmount;
    }

    public void setMarketingLists(int marketingLists) {
        this.marketingLists = marketingLists;
    }

    public void setDateAddedToList(Date dateAddedToList) {
        this.dateAddedToList = dateAddedToList;
    }
}
