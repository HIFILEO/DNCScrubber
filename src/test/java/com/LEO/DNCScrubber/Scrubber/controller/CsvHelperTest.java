package com.LEO.DNCScrubber.Scrubber.controller;

import com.LEO.DNCScrubber.Scrubber.controller.model.ReiSkipTraceCsv;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/*
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
class CsvHelperTest extends RxJavaTest {
    private static final boolean PHONE_LITIGATOR = false;

    @Test
    public void translateReiSkipTraceCsvToPersonFromSkipTrace() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();

        ReiSkipTraceCsv reiSkipTraceCsv = new ReiSkipTraceCsv(
                "test first name",
                "test last name",
                "test property address",
                "test property city",
                "test property state",
                "test property zip",
                "test mailing address",
                "test mailing city",
                "test mailing state",
                "test mailing zip",
                "test phone 1 number",
                "test phone 1 type",
                true,
                PHONE_LITIGATOR,
                "test phone 1 telcoName",
                "test phone 2 number",
                "test phone 2 type",
                false,
                PHONE_LITIGATOR,
                "test phone 2 telcoName",
                "test phone 3 number",
                "test phone 3 type",
                true,
                PHONE_LITIGATOR,
                "test phone 3 telcoName",
                "test email 1",
                "test email 2",
                "test email 3"
        );

        //
        //Act
        //
        PersonFromSkipTrace personFromSkipTrace = csvHelper
                .translateReiSkipTraceCsvToPersonFromSkipTrace(reiSkipTraceCsv);

        //
        //Assert
        //
        assertThat(personFromSkipTrace.hasError()).isFalse();
        assertThat(personFromSkipTrace.getErrorMessage().isPresent()).isTrue();
        assertThat(personFromSkipTrace.getErrorMessage().get()).isEmpty();

        assertThat(personFromSkipTrace.getPerson().isPresent()).isTrue();

        Person personToTest = personFromSkipTrace.getPerson().get();
        assertThat(personToTest.getFirstName()).isEqualToIgnoringCase(reiSkipTraceCsv.getFirstName());
        assertThat(personToTest.getLastName()).isEqualToIgnoringCase(reiSkipTraceCsv.getLastName());
        assertThat(personToTest.getAddress().getMailingAddress()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingAddress());
        assertThat(personToTest.getAddress().getUnitNumber()).isEqualToIgnoringCase("0");
        assertThat(personToTest.getAddress().getCity()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingCity());
        assertThat(personToTest.getAddress().getState()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingState());
        assertThat(personToTest.getAddress().getZip()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingZip());
        assertThat(personToTest.getAddress().getCounty()).isEqualToIgnoringCase("");
        assertThat(personToTest.getAddress().getCountry()).isEqualToIgnoringCase("US");

        //phone 1
        assertThat(personToTest.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1());
        assertThat(personToTest.getPhone1().getPhoneType()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1Type());
        assertThat(personToTest.getPhone1().isPhoneDNC()).isEqualTo(reiSkipTraceCsv.isPhone1Dnc());
        assertThat(personToTest.getPhone1().isPhoneStop()).isEqualTo(false);
        assertThat(personToTest.getPhone1().isPhoneLitigation()).isEqualTo(PHONE_LITIGATOR);
        assertThat(personToTest.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1TelcoName());

        //phone 2
        assertThat(personToTest.getPhone2().getPhoneNumber()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone2());
        assertThat(personToTest.getPhone2().getPhoneType()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone2Type());
        assertThat(personToTest.getPhone2().isPhoneDNC()).isEqualTo(reiSkipTraceCsv.isPhone2Dnc());
        assertThat(personToTest.getPhone2().isPhoneStop()).isEqualTo(false);
        assertThat(personToTest.getPhone2().isPhoneLitigation()).isEqualTo(PHONE_LITIGATOR);
        assertThat(personToTest.getPhone2().getPhoneTelco()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone2TelcoName());

        //phone 3
        assertThat(personToTest.getPhone3().getPhoneNumber()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone3());
        assertThat(personToTest.getPhone3().getPhoneType()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone3Type());
        assertThat(personToTest.getPhone3().isPhoneDNC()).isEqualTo(reiSkipTraceCsv.isPhone3Dnc());
        assertThat(personToTest.getPhone3().isPhoneStop()).isEqualTo(false);
        assertThat(personToTest.getPhone3().isPhoneLitigation()).isEqualTo(PHONE_LITIGATOR);
        assertThat(personToTest.getPhone3().getPhoneTelco()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone3TelcoName());

        assertThat(personToTest.getEmail1()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail1());
        assertThat(personToTest.getEmail2()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail2());
        assertThat(personToTest.getEmail3()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail3());
    }

    @Test
    public void translateReiSkipTraceCsvToPersonFromSkipTrace_noPhone2OrPhone3() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();

        ReiSkipTraceCsv reiSkipTraceCsv = new ReiSkipTraceCsv(
                "test first name",
                "test last name",
                "test property address",
                "test property city",
                "test property state",
                "test property zip",
                "test mailing address",
                "test mailing city",
                "test mailing state",
                "test mailing zip",
                "test phone 1 number",
                "test phone 1 type",
                true,
                PHONE_LITIGATOR,
                "test phone 1 telcoName",
                "",
                "",
                false,
                PHONE_LITIGATOR,
                "",
                "",
                "",
                true,
                PHONE_LITIGATOR,
                "",
                "",
                "",
                ""
        );

        //
        //Act
        //
        PersonFromSkipTrace personFromSkipTrace = csvHelper
                .translateReiSkipTraceCsvToPersonFromSkipTrace(reiSkipTraceCsv);

        //
        //Assert
        //
        assertThat(personFromSkipTrace.hasError()).isFalse();
        assertThat(personFromSkipTrace.getErrorMessage().isPresent()).isTrue();
        assertThat(personFromSkipTrace.getErrorMessage().get()).isEmpty();

        assertThat(personFromSkipTrace.getPerson().isPresent()).isTrue();

        Person personToTest = personFromSkipTrace.getPerson().get();
        assertThat(personToTest.getFirstName()).isEqualToIgnoringCase(reiSkipTraceCsv.getFirstName());
        assertThat(personToTest.getLastName()).isEqualToIgnoringCase(reiSkipTraceCsv.getLastName());
        assertThat(personToTest.getAddress().getMailingAddress()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingAddress());
        assertThat(personToTest.getAddress().getUnitNumber()).isEqualToIgnoringCase("0");
        assertThat(personToTest.getAddress().getCity()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingCity());
        assertThat(personToTest.getAddress().getState()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingState());
        assertThat(personToTest.getAddress().getZip()).isEqualToIgnoringCase(reiSkipTraceCsv.getMailingZip());
        assertThat(personToTest.getAddress().getCounty()).isEqualToIgnoringCase("");
        assertThat(personToTest.getAddress().getCountry()).isEqualToIgnoringCase("US");

        //phone 1
        assertThat(personToTest.getPhone1().getPhoneNumber()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1());
        assertThat(personToTest.getPhone1().getPhoneType()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1Type());
        assertThat(personToTest.getPhone1().isPhoneDNC()).isEqualTo(reiSkipTraceCsv.isPhone1Dnc());
        assertThat(personToTest.getPhone1().isPhoneStop()).isEqualTo(false);
        assertThat(personToTest.getPhone1().isPhoneLitigation()).isEqualTo(PHONE_LITIGATOR);
        assertThat(personToTest.getPhone1().getPhoneTelco()).isEqualToIgnoringCase(reiSkipTraceCsv.getPhone1TelcoName());

        //phone 2
        assertThat(personToTest.getPhone2()).isNull();;

        //phone 3
        assertThat(personToTest.getPhone3()).isNull();

        assertThat(personToTest.getEmail1()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail1());
        assertThat(personToTest.getEmail2()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail2());
        assertThat(personToTest.getEmail3()).isEqualToIgnoringCase(reiSkipTraceCsv.getEmail3());
    }

    @Test
    public void translatePersonToReiSkipTraceCsv() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();

        Person person = createPerson();
        Property property = createProperty();
        person.addProperty(property);

        //
        //Act
        //
        ReiSkipTraceCsv reiSkipTraceCsv = csvHelper.translatePersonToReiSkipTraceCsv(person, property);

        //
        //Assert
        //
        assertThat(reiSkipTraceCsv).isNotNull();
        assertThat(reiSkipTraceCsv.getFirstName()).isEqualToIgnoringCase(person.getFirstName());
        assertThat(reiSkipTraceCsv.getLastName()).isEqualToIgnoringCase(person.getLastName());

        assertThat(reiSkipTraceCsv.getPropertyAddress()).isEqualToIgnoringCase(property.getAddress().getMailingAddress());
        assertThat(reiSkipTraceCsv.getPropertyCity()).isEqualToIgnoringCase(property.getAddress().getCity());
        assertThat(reiSkipTraceCsv.getPropertyState()).isEqualToIgnoringCase(property.getAddress().getState());
        assertThat(reiSkipTraceCsv.getPropertyZip()).isEqualToIgnoringCase(property.getAddress().getZip());

        assertThat(reiSkipTraceCsv.getMailingAddress()).isEqualToIgnoringCase(person.getAddress().getMailingAddress());
        assertThat(reiSkipTraceCsv.getMailingCity()).isEqualToIgnoringCase(person.getAddress().getCity());
        assertThat(reiSkipTraceCsv.getMailingState()).isEqualToIgnoringCase(person.getAddress().getState());
        assertThat(reiSkipTraceCsv.getMailingZip()).isEqualToIgnoringCase(person.getAddress().getZip());

        assertThat(reiSkipTraceCsv.getPhone1()).isEmpty();
        assertThat(reiSkipTraceCsv.getPhone1Type()).isEmpty();
        assertThat(reiSkipTraceCsv.isPhone1Dnc()).isFalse();
        assertThat(reiSkipTraceCsv.isPhone1Litigator()).isFalse();
        assertThat(reiSkipTraceCsv.getPhone1TelcoName()).isEmpty();

        assertThat(reiSkipTraceCsv.getPhone2()).isEmpty();
        assertThat(reiSkipTraceCsv.getPhone2Type()).isEmpty();
        assertThat(reiSkipTraceCsv.isPhone2Dnc()).isFalse();
        assertThat(reiSkipTraceCsv.isPhone2Litigator()).isFalse();
        assertThat(reiSkipTraceCsv.getPhone2TelcoName()).isEmpty();

        assertThat(reiSkipTraceCsv.getPhone3()).isEmpty();
        assertThat(reiSkipTraceCsv.getPhone3Type()).isEmpty();
        assertThat(reiSkipTraceCsv.isPhone3Dnc()).isFalse();
        assertThat(reiSkipTraceCsv.isPhone3Litigator()).isFalse();
        assertThat(reiSkipTraceCsv.getPhone3TelcoName()).isEmpty();

        assertThat(reiSkipTraceCsv.getEmail1()).isEmpty();
        assertThat(reiSkipTraceCsv.getEmail2()).isEmpty();
        assertThat(reiSkipTraceCsv.getEmail3()).isEmpty();

    }

    private Person createPerson() {
        Address personAddress = new Address(
                "123 Ave person",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Person person = new Person(
                "Dan",
                "Leo",
                personAddress
        );

        return person;
    }

    private Property createProperty() {
        Address propertyAddress = new Address(
                "123 Ave property",
                "",
                "Boston",
                "MA",
                "00215",
                "Boston",
                "US");

        Property property = new Property(
                "APN#12345",
                propertyAddress
        );

        property.setOwnerOccupied(true);
        property.setCompanyName("The bacon company, LLC");
        property.setCompanyAddress("1234 bacon way");
        property.setPropertyType("Multi");
        property.setBedrooms("5");
        property.setTotalBathrooms("1");
        property.setSqft(2000);
        property.setLotSizeSqft(50000);
        property.setYearBuilt(1910);
        property.setAssessedValue(1000000);
        property.setLastSaleRecordingDate(new Date());
        property.setLastSaleAmount(500000);
        property.setTotalOpenLoans(1);
        property.setEstimatedRemainingBalance(50000);
        property.setEstimatedValue(900000);
        property.setEstimatedEquity(100000);
        property.setmLSStatus("Listed");
        property.setMlsDate(new Date());
        property.setmLSAmount("1000000");
        property.setLienAmount("12345");
        property.setDateAddedToList(new Date());

        return property;
    }
}