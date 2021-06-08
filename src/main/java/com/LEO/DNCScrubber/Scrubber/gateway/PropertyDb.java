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

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Property")
public class PropertyDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String unitNumber;
    private String city;
    private String state;
    private String zip;
    private String county;
    private String country;

    @Column(name = "APN")
    private String aPN;

    private boolean ownerOccupied;
    private String companyName;
    private String companyAddress;

    private String propertyType;
    private String bedrooms;
    private String totalBathrooms;
    private int sqft;
    private int loftSizeSqft;
    private int yearBuilt;
    private int assessedValue;

    //Note = https://www.baeldung.com/hibernate-date-time = only holds yyyy-MM-dd, no time units
    @Basic
    @Temporal(TemporalType.DATE)
    private Date lastSaleRecordingDate;

    private int lastSaleAmount;
    private int totalOpenLoans;
    private int estimatedRemainingBalance;
    private int estimatedValue;
    private int estimatedEquity;

    @Column(name = "MLS_Status")
    private String mLSStatus;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "MLS_Date")
    private Date mLSDate;

    @Column(name = "MLS_Amount")
    private String mLSAmount;

    private String lienAmount;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date dateAddedToList;

    /*
    Note - The @ManyToOne association uses FetchType.LAZY because, otherwise, we’d fall back to EAGER
    fetching which is bad for performance.
    https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private PersonDb person;

    /*
    Note - The child entity, PropertyDb, implement the equals and hashCode methods. Since we cannot rely on a natural
    identifier for equality checks, we need to use the entity identifier instead for the equals method.
    However, you need to do it properly so that equality is consistent across all entity state transitions, which is
    also the reason why the hashCode has to be a constant value. Because we rely on equality for the removeComment,
    it’s good practice to override equals and hashCode for the child entity in a bidirectional association.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyDb )) return false;
        return id != null && id.equals(((PropertyDb) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setPerson(PersonDb personDb) {
        person = personDb;
    }

    public PersonDb getPerson() {
        return person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getaPN() {
        return aPN;
    }

    public void setaPN(String aPN) {
        this.aPN = aPN;
    }

    public boolean isOwnerOccupied() {
        return ownerOccupied;
    }

    public void setOwnerOccupied(boolean ownerOccupied) {
        this.ownerOccupied = ownerOccupied;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getTotalBathrooms() {
        return totalBathrooms;
    }

    public void setTotalBathrooms(String totalBathrooms) {
        this.totalBathrooms = totalBathrooms;
    }

    public int getSqft() {
        return sqft;
    }

    public void setSqft(int sqft) {
        this.sqft = sqft;
    }

    public int getLoftSizeSqft() {
        return loftSizeSqft;
    }

    public void setLoftSizeSqft(int loftSizeSqft) {
        this.loftSizeSqft = loftSizeSqft;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public int getAssessedValue() {
        return assessedValue;
    }

    public void setAssessedValue(int assessedValue) {
        this.assessedValue = assessedValue;
    }

    public Date getLastSaleRecordingDate() {
        return lastSaleRecordingDate;
    }

    public void setLastSaleRecordingDate(Date lastSaleRecordingDate) {
        this.lastSaleRecordingDate = lastSaleRecordingDate;
    }

    public int getLastSaleAmount() {
        return lastSaleAmount;
    }

    public void setLastSaleAmount(int lastSaleAmount) {
        this.lastSaleAmount = lastSaleAmount;
    }

    public int getTotalOpenLoans() {
        return totalOpenLoans;
    }

    public void setTotalOpenLoans(int totalOpenLoans) {
        this.totalOpenLoans = totalOpenLoans;
    }

    public int getEstimatedRemainingBalance() {
        return estimatedRemainingBalance;
    }

    public void setEstimatedRemainingBalance(int estimatedRemainingBalance) {
        this.estimatedRemainingBalance = estimatedRemainingBalance;
    }

    public int getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(int estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public int getEstimatedEquity() {
        return estimatedEquity;
    }

    public void setEstimatedEquity(int estimatedEquity) {
        this.estimatedEquity = estimatedEquity;
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

    public String getmLSAmount() {
        return mLSAmount;
    }

    public void setmLSAmount(String mLSAmount) {
        this.mLSAmount = mLSAmount;
    }

    public String getLienAmount() {
        return lienAmount;
    }

    public void setLienAmount(String lienAmount) {
        this.lienAmount = lienAmount;
    }

    public Date getDateAddedToList() {
        return dateAddedToList;
    }

    public void setDateAddedToList(Date dateAddedToList) {
        this.dateAddedToList = dateAddedToList;
    }
}
