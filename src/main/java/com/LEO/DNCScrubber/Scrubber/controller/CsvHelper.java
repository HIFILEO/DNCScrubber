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

import com.LEO.DNCScrubber.Scrubber.controller.model.PersonFromSkipTraceImpl;
import com.LEO.DNCScrubber.Scrubber.controller.model.ReiSkipTraceCsv;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import jakarta.validation.constraints.NotNull;

/**
 * The purpose of this class is to help translate business objects to CSV objects and vice versa.
 */
public class CsvHelper {

    public PersonFromSkipTrace translateReiSkipTraceCsvToPersonFromSkipTrace(ReiSkipTraceCsv reiSkipTraceCsv) {
        Address address = new Address(
                reiSkipTraceCsv.getMailingAddress(),
                "0",
                reiSkipTraceCsv.getMailingCity(),
                reiSkipTraceCsv.getMailingState(),
                reiSkipTraceCsv.getMailingZip(),
                "",
                "US"
        );

        Person person = new Person(
                reiSkipTraceCsv.getFirstName(),
                reiSkipTraceCsv.getLastName(),
                address
        );

        //only create phone object if you have data
        if (reiSkipTraceCsv.getPhone1() != null && !reiSkipTraceCsv.getPhone1().isEmpty()) {
            Phone phone1 = new Phone();
            phone1.setPhoneNumber(reiSkipTraceCsv.getPhone1());
            phone1.setPhoneType(reiSkipTraceCsv.getPhone1Type());
            phone1.setPhoneDNC(reiSkipTraceCsv.isPhone1Dnc());
            phone1.setPhoneStop(false);
            phone1.setPhoneLitigation(reiSkipTraceCsv.isPhone1Litigator());
            phone1.setPhoneTelco(reiSkipTraceCsv.getPhone1TelcoName());

            person.setPhone1(phone1);
        }

        if (reiSkipTraceCsv.getPhone2() != null && !reiSkipTraceCsv.getPhone2().isEmpty()) {
            Phone phone2 = new Phone();
            phone2.setPhoneNumber(reiSkipTraceCsv.getPhone2());
            phone2.setPhoneType(reiSkipTraceCsv.getPhone2Type());
            phone2.setPhoneDNC(reiSkipTraceCsv.isPhone2Dnc());
            phone2.setPhoneStop(false);
            phone2.setPhoneLitigation(reiSkipTraceCsv.isPhone2Litigator());
            phone2.setPhoneTelco(reiSkipTraceCsv.getPhone2TelcoName());

            person.setPhone2(phone2);
        }

        if (reiSkipTraceCsv.getPhone2() != null && !reiSkipTraceCsv.getPhone2().isEmpty()) {
            Phone phone3 = new Phone();
            phone3.setPhoneNumber(reiSkipTraceCsv.getPhone3());
            phone3.setPhoneType(reiSkipTraceCsv.getPhone3Type());
            phone3.setPhoneDNC(reiSkipTraceCsv.isPhone3Dnc());
            phone3.setPhoneStop(false);
            phone3.setPhoneLitigation(reiSkipTraceCsv.isPhone3Litigator());
            phone3.setPhoneTelco(reiSkipTraceCsv.getPhone3TelcoName());

            person.setPhone3(phone3);
        }

        person.setEmail1(reiSkipTraceCsv.getEmail1());
        person.setEmail2(reiSkipTraceCsv.getEmail2());
        person.setEmail3(reiSkipTraceCsv.getEmail3());

        /*
        Note - not going to carry over property information, that should already be in Database before we skip
        trace.
        */
        return new PersonFromSkipTraceImpl(person,"");
    }

    /**
     * Converts Business Logic {@link Person} to {@link ReiSkipTraceCsv} for a specific {@link Property}. This is
     * how REISkip trace works - you need a person's name and mailing address and property of interest, you'll then
     * get back their phone, email, & property information that they have.
     *
     *
     * Won't do any checks. Only property is
     *
     * @param person - to process, can't be null
     * @return - ReiSkipTraceCsv
     */
    public ReiSkipTraceCsv translatePersonToReiSkipTraceCsv(@NotNull Person person, Property property) {
        ReiSkipTraceCsv reiSkipTraceCsv = new ReiSkipTraceCsv(
                person.getFirstName(),
                person.getLastName(),
                property.getAddress().getMailingAddress(),
                property.getAddress().getCity(),
                property.getAddress().getState(),
                property.getAddress().getZip(),
                person.getAddress().getMailingAddress(),
                person.getAddress().getCity(),
                person.getAddress().getState(),
                person.getAddress().getZip(),
                //phone 1
                "",
                "",
                false,
                false,
                "",
                //phone 2
                "",
                "",
                false,
                false,
                "",
                //phone 3
                "",
                "",
                false,
                false,
                "",
                //emails
                "",
                "",
                ""
        );

        return reiSkipTraceCsv;
    }
}
