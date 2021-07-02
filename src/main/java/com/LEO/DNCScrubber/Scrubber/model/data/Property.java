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
 * Represents a business layer property class.
 */
public class Property {
    private Address address;
    private String aPN = "";
    private boolean ownerOccupied;
    private String companyName = "";
    private String companyAddress = "";
    private String propertyType = "";
    private String bedrooms = "";
    private String totalBathrooms = "";
    private int sqft;
    private int lotSizeSqft;
    private int yearBuilt;
    private int assessedValue;
    private Date lastSaleRecordingDate = new Date();
    private int lastSaleAmount;
    private int totalOpenLoans;
    private int estimatedRemainingBalance;
    private int estimatedValue;
    private int estimatedEquity;
    private String mLSStatus = "";
    private Date mlsDate = new Date();
    private String mLSAmount = "";
    private String lienAmount = "";
    private Date dateAddedToList = new Date();

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public int getLotSizeSqft() {
        return lotSizeSqft;
    }

    public void setLotSizeSqft(int lotSizeSqft) {
        this.lotSizeSqft = lotSizeSqft;
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

    public Date getMlsDate() {
        return mlsDate;
    }

    public void setMlsDate(Date mlsDate) {
        this.mlsDate = mlsDate;
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

    public String getNaturalId() {
        if (address == null || address.getMailingAddress().isEmpty()) {
            return "";
        }

        String noSpacesAddress = address.getMailingAddress().replaceAll("\\s+","");
        String noSpacesCity = address.getCity().replaceAll("\\s+","");
        return noSpacesAddress + "~" + noSpacesCity;
    }
}
