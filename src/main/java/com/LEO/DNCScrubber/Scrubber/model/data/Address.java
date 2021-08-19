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
/**
 * Represents an Address
 */
public class Address {
    private final String mailingAddress;
    private final String unitNumber;
    private final String city;
    private final String state;
    private final String zip;
    private final String county;
    private final String country;

    public Address(String mailingAddress, String unitNumber, String city, String state, String zip, String county,
                   String country) {
        this.mailingAddress = mailingAddress;
        this.unitNumber = unitNumber;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.county = county;
        this.country = country;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getCounty() {
        return county;
    }

    public String getCountry() {
        return country;
    }
}
