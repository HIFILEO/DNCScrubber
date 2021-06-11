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

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Person")
public class PersonDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //uniqueConstraints= combine {"firstName", "lastName", "address"}
    @NaturalId(mutable=true)
    @Column(nullable = false, unique = true)
    private String naturalId = "";

    @NotNull
    @Column(nullable = false)
    private String firstName = "";

    @NotNull
    @Column(nullable = false)
    private String lastName = "";

    @NotNull
    @Column(nullable = false)
    private String address = "";

    private String unitNumber = "";
    private String city = "";
    private String state = "";
    private String zip = "";
    private String county = "";
    private String country = "";

    private String phone1 = "";
    private String phone1Type = "";
    private boolean phone1DNC;
    private boolean phone1Stop;
    private boolean phone1Litigation;
    private String phone1Telco = "";

    private String phone2 = "";
    private String phone2Type = "";
    private boolean phone2DNC;
    private boolean phone2Stop;
    private boolean phone2Litigation;
    private String phone2Telco = "";

    private String phone3 = "";
    private String phone3Type = "";
    private boolean phone3DNC;
    private boolean phone3Stop;
    private boolean phone3Litigation;
    private String phone3Telco = "";

    private String email1 = "";
    private String email2 = "";
    private String email3 = "";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "Property_id")
    private List<PropertyDao> properties = new ArrayList<PropertyDao>();

    /*
        The parent entity, Person, features two utility methods which are used to synchronize both sides of the
        bidirectional association. You should always provide these methods whenever you are working with a
         bidirectional association as, otherwise, you risk very subtle state propagation issues.
         https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
     */
    /**
     * Add {@link PropertyDao} to Person
     * @param propertyDao - Property owned
     */
    public void addProperty(PropertyDao propertyDao) {
        properties.add(propertyDao);
        propertyDao.setPerson(this);
    }

    public void removeProperty(PropertyDao propertyDao) {
        properties.remove(propertyDao);
        propertyDao.setPerson(null);
    }

    public List<PropertyDao> getProperties() {
        return properties;
    }

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

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1Type() {
        return phone1Type;
    }

    public void setPhone1Type(String phone1Type) {
        this.phone1Type = phone1Type;
    }

    public boolean isPhone1DNC() {
        return phone1DNC;
    }

    public void setPhone1DNC(boolean phone1DNC) {
        this.phone1DNC = phone1DNC;
    }

    public boolean isPhone1Stop() {
        return phone1Stop;
    }

    public void setPhone1Stop(boolean phone1Stop) {
        this.phone1Stop = phone1Stop;
    }

    public boolean isPhone1Litigation() {
        return phone1Litigation;
    }

    public void setPhone1Litigation(boolean phone1Litigation) {
        this.phone1Litigation = phone1Litigation;
    }

    public String getPhone1Telco() {
        return phone1Telco;
    }

    public void setPhone1Telco(String phone1Telco) {
        this.phone1Telco = phone1Telco;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2Type() {
        return phone2Type;
    }

    public void setPhone2Type(String phone2Type) {
        this.phone2Type = phone2Type;
    }

    public boolean isPhone2DNC() {
        return phone2DNC;
    }

    public void setPhone2DNC(boolean phone2DNC) {
        this.phone2DNC = phone2DNC;
    }

    public boolean isPhone2Stop() {
        return phone2Stop;
    }

    public void setPhone2Stop(boolean phone2Stop) {
        this.phone2Stop = phone2Stop;
    }

    public boolean isPhone2Litigation() {
        return phone2Litigation;
    }

    public void setPhone2Litigation(boolean phone2Litigation) {
        this.phone2Litigation = phone2Litigation;
    }

    public String getPhone2Telco() {
        return phone2Telco;
    }

    public void setPhone2Telco(String phone2Telco) {
        this.phone2Telco = phone2Telco;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getPhone3Type() {
        return phone3Type;
    }

    public void setPhone3Type(String phone3Type) {
        this.phone3Type = phone3Type;
    }

    public boolean isPhone3DNC() {
        return phone3DNC;
    }

    public void setPhone3DNC(boolean phone3DNC) {
        this.phone3DNC = phone3DNC;
    }

    public boolean isPhone3Stop() {
        return phone3Stop;
    }

    public void setPhone3Stop(boolean phone3Stop) {
        this.phone3Stop = phone3Stop;
    }

    public boolean isPhone3Litigation() {
        return phone3Litigation;
    }

    public void setPhone3Litigation(boolean phone3Litigation) {
        this.phone3Litigation = phone3Litigation;
    }

    public String getPhone3Telco() {
        return phone3Telco;
    }

    public void setPhone3Telco(String phone3Telco) {
        this.phone3Telco = phone3Telco;
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

    public String getNaturalId() {
        return naturalId;
    }

    public void setNaturalId(String naturalId) {
        this.naturalId = naturalId;
    }
}
