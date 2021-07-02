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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a business layer home owner. Named Person accordingly since a home owner could be a company and we deal w/ people.
 *
 * Note - A person might own multiple properties but depending where we load the data from the list might oly contain 1
 * ie - CSV typically 1 to 1
 *      DB 1 to many
 */
public class Person {
    private String firstName = "";
    private String lastName = "";
    private Address address;
    private Phone phone1;
    private Phone phone2;
    private Phone phone3;
    private String email1 = "";
    private String email2 = "";
    private String email3 = "";
    private List<Property> propertyList = new ArrayList<Property>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone1() {
        return phone1;
    }

    public void setPhone1(Phone phone1) {
        this.phone1 = phone1;
    }

    public Phone getPhone2() {
        return phone2;
    }

    public void setPhone2(Phone phone2) {
        this.phone2 = phone2;
    }

    public Phone getPhone3() {
        return phone3;
    }

    public void setPhone3(Phone phone3) {
        this.phone3 = phone3;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getEmail3() {
        return email3;
    }

    public void setEmail3(String email3) {
        this.email3 = email3;
    }

    public void addProperty(Property property) {
        propertyList.add(property);
    }

    public boolean removeProperty(Property property) {
        return propertyList.remove(property);
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public String getNaturalId() {
        if (firstName.isEmpty() && lastName.isEmpty() && (address == null || address.getMailingAddress().isEmpty())) {
            return "";
        }

        String noSpacesFirstName = firstName.replaceAll("\\s+","");
        String noSpacesLastName = lastName.replaceAll("\\s+","");
        String noSpacesAddress = address.getMailingAddress().replaceAll("\\s+","");
        return noSpacesFirstName + "~" + noSpacesLastName + "~" + noSpacesAddress;
    }
}
