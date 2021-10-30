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

import com.LEO.DNCScrubber.Scrubber.controller.model.RawLeadCsvImpl;
import com.LEO.DNCScrubber.Scrubber.controller.model.RawLeadErrorImpl;
import com.LEO.DNCScrubber.Scrubber.model.WritePeopleToSkipTraceStatus;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import com.LEO.DNCScrubber.util.Visitors;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class CsvFileControllerImplTest extends RxJavaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void readRawLeads_success() throws Exception {
        //
        //Arrange
        //
        CsvHelper csvHelperMock = Mockito.mock(CsvHelper.class);
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelperMock);
        TestObserver<RawLead> testObserver;

        File file = new File("build/resources/test/test-data - Properties.csv");

        //
        //Act
        //
        testObserver = csvFileControllerImpl.readRawLeads(file).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }

//    Got rid of the bean verifier so no test. We want all them to have errors and report them up to screen.
//    @Test
//    public void readRawLeads_error_failBeanVerifier() throws Exception {
//        //
//        //Arrange
//        //
//        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl();
//        TestObserver<RawLead> testObserver;
//
//        File file = new File("build/resources/test/unittest-data-failBeanVerify.csv");
//
//        //
//        //Act
//        //
//        testObserver = csvFileControllerImpl.readRawLeads(file).test();
//        testScheduler.triggerActions();
//
//        //
//        //Assert
//        //
//        List<Throwable> errors = testObserver.errors();
//        assertThat(errors).isNotEmpty();
//        assertThat(errors.get(0).getMessage()).isEqualToIgnoringCase(
//                "com.opencsv.exceptions.CsvConstraintViolationException: " + RawLedVerifier.ERROR_MSG);
//    }

    @Test
    public void testOpenCsv_success() throws Exception {
        //
        //Arrange
        //
        File file = new File("build/resources/test/test-openCSV.csv");
        System.out.println("File Path = " + file.getAbsolutePath());

        //
        //Act
        //
        //

        //For a small file size - this makes sense. Since we are loading files that have 2 million entries, i'm
        //going to shoot for load one at a time.

        //Load everything (or use stream() - java 8)
        //Leave as example
//        List<Visitors> beans = new CsvToBeanBuilder(new FileReader(file))
//                .withType(Visitors.class).build().parse();

        //Iterate and load one by one
        Iterator<Visitors> iterator =  new CsvToBeanBuilder(new FileReader(file))
                .withType(Visitors.class).build().iterator();

        List<Visitors> beans = new ArrayList<>();
        while(iterator.hasNext()) {
            beans.add(iterator.next());
        }

        //
        //Assert
        //
        assertThat(beans).isNotEmpty();
        assertThat(beans).hasSize(2);

        Visitors visitor = beans.get(0);
        assertThat(visitor.getFirstName()).isEqualToIgnoringCase("John");
        assertThat(visitor.getLastName()).isEqualToIgnoringCase("Doe");
        assertThat(visitor.getVisitsToWebsite()).isEqualTo(12);

    }

    @Test
    public void readRawLeads_fileNotFound() {
        //
        //Arrange
        //
        CsvHelper csvHelperMock = Mockito.mock(CsvHelper.class);
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelperMock);
        TestObserver<RawLead> testObserver;

        File file = new File("no-file.csv");

        //
        //Act
        //
        testObserver = csvFileControllerImpl.readRawLeads(file).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        assertThat(testObserver.errorCount()).isEqualTo(1);
        testObserver.assertErrorMessage("no-file.csv (No such file or directory)");
    }

    @Test
    public void readRawLeads_firstGood_restFail() throws Exception {
        //
        //Arrange
        //
        CsvHelper csvHelperMock = Mockito.mock(CsvHelper.class);
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelperMock);
        TestObserver<RawLead> testObserver;

        File file = new File("build/resources/test/test-data - Bad Data.csv");

        //
        //Act
        //
        testObserver = csvFileControllerImpl.readRawLeads(file).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(10);//9 fields - 10 count because duplicate errors on one screen

        //5 good 5 bad
        int numRawLeadCsvImpl = 0;
        int numRawLeadErrorImpl = 0;

        for (Iterator<Object> it = testObserver.getEvents().get(0).iterator(); it.hasNext(); ) {
            RawLead rawLead = (RawLead) it.next();
            if (rawLead instanceof RawLeadErrorImpl) {
                numRawLeadErrorImpl++;
            } else if (rawLead instanceof RawLeadCsvImpl) {
                numRawLeadCsvImpl++;
            }
        }

        assertThat(numRawLeadCsvImpl).isEqualTo(5);
        assertThat(numRawLeadErrorImpl).isEqualTo(5);
    }

    @Test
    public void readPeopleFromSkipTrace_useReiSkipTraceData() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<PersonFromSkipTrace> testObserver;

        File file = new File("build/resources/test/test-data - ReiSkipTraceData.csv");

        //
        //Act
        //
        testObserver = csvFileControllerImpl.readPeopleFromSkipTrace(file).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();

        //First Test - Name
        PersonFromSkipTrace personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(0);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Bonnie");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Last");

        //First Test - Address
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getMailingAddress()).isEqualToIgnoringCase("456 S End Rd");
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getCity()).isEqualToIgnoringCase("Plantsville");
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getState()).isEqualToIgnoringCase("CT");
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getCounty()).isEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getZip()).isEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getAddress().getCountry()).isEqualToIgnoringCase("US");

        //First Test - Phone 1
        assertThat(personFromSkipTrace.getPerson().get().getPhone1().getPhoneNumber()).isEqualToIgnoringCase("8609196070");
        assertThat(personFromSkipTrace.getPerson().get().getPhone1().getPhoneType()).isEqualToIgnoringCase("Wireless");
        assertThat(personFromSkipTrace.getPerson().get().getPhone1().isPhoneDNC()).isTrue();
        assertThat(personFromSkipTrace.getPerson().get().getPhone1().isPhoneLitigation()).isTrue();
        assertThat(personFromSkipTrace.getPerson().get().getPhone1().getPhoneTelco()).isEqualToIgnoringCase("CELLCO PARTNERSHIP DBA VERIZON");

        //First Test - Phone 2
        assertThat(personFromSkipTrace.getPerson().get().getPhone2().getPhoneNumber()).isEqualToIgnoringCase("8602614781");
        assertThat(personFromSkipTrace.getPerson().get().getPhone2().getPhoneType()).isEqualToIgnoringCase("Land Line");
        assertThat(personFromSkipTrace.getPerson().get().getPhone2().isPhoneDNC()).isFalse();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2().isPhoneLitigation()).isFalse();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2().getPhoneTelco()).isEqualToIgnoringCase("COMCAST PHONE OF CONNECTICUT, ");

        //First Test - Phone 3
        assertThat(personFromSkipTrace.getPerson().get().getPhone3().getPhoneNumber()).isEqualToIgnoringCase("8605842657");
        assertThat(personFromSkipTrace.getPerson().get().getPhone3().getPhoneType()).isEqualToIgnoringCase("Land Line");
        assertThat(personFromSkipTrace.getPerson().get().getPhone3().isPhoneDNC()).isFalse();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3().isPhoneLitigation()).isFalse();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3().getPhoneTelco()).isEqualToIgnoringCase("SOUTHERN NEW ENGLAND TELCO DBA");

        //First Test - Emails
        assertThat(personFromSkipTrace.getPerson().get().getEmail1()).isEqualToIgnoringCase("paghense@comcast.net");
        assertThat(personFromSkipTrace.getPerson().get().getEmail2()).isEqualToIgnoringCase("bpaghense@yahoo.com");
        assertThat(personFromSkipTrace.getPerson().get().getEmail3()).isEqualToIgnoringCase("bonniepaghense@aol.com");

        /* Note - now we test permutations in the file only, not to make sure everything comes across */

        //Second Test - Name
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(1);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Nick");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test MckNill");

        //Third Test - Name (only 1 phone)
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(2);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Ham");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Last K");

        assertThat(personFromSkipTrace.getPerson().get().getPhone1()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2()).isNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3()).isNull();

        //Forth Test - Name (only 1 email)
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(3);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Adam");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Last Maj");

        assertThat(personFromSkipTrace.getPerson().get().getEmail1()).isEqualToIgnoringCase("emclellan@hotmail.com");
        assertThat(personFromSkipTrace.getPerson().get().getEmail2()).isNullOrEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getEmail3()).isNullOrEmpty();

        //Fifth Test - Name (no emails - but phone numbers)
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(4);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test First Name Sam");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Last Z");

        assertThat(personFromSkipTrace.getPerson().get().getPhone1()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3()).isNotNull();

        assertThat(personFromSkipTrace.getPerson().get().getEmail1()).isNullOrEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getEmail2()).isNullOrEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getEmail3()).isNullOrEmpty();

        //Sixth Test - Name (one phone + one email)
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(5);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Dan First Name");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Last Macaroni");

        assertThat(personFromSkipTrace.getPerson().get().getPhone1()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2()).isNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3()).isNull();

        assertThat(personFromSkipTrace.getPerson().get().getEmail1()).isEqualToIgnoringCase("emilymacione@comcast.net");
        assertThat(personFromSkipTrace.getPerson().get().getEmail2()).isNullOrEmpty();
        assertThat(personFromSkipTrace.getPerson().get().getEmail3()).isNullOrEmpty();

        //Seventh Test - Name (has extra data on CSV that we don't use)
        personFromSkipTrace = (PersonFromSkipTrace) testObserver.getEvents().get(0).get(6);
        assertThat(personFromSkipTrace.getPerson().get().getFirstName()).isEqualToIgnoringCase("Test Bret");
        assertThat(personFromSkipTrace.getPerson().get().getLastName()).isEqualToIgnoringCase("Test Oakes Last");

        assertThat(personFromSkipTrace.getPerson().get().getPhone1()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone2()).isNotNull();
        assertThat(personFromSkipTrace.getPerson().get().getPhone3()).isNotNull();

        assertThat(personFromSkipTrace.getPerson().get().getEmail1()).isEqualToIgnoringCase("danio1972@aol.com");
        assertThat(personFromSkipTrace.getPerson().get().getEmail2()).isEqualToIgnoringCase("danielleoakes713@yahoo.com");
        assertThat(personFromSkipTrace.getPerson().get().getEmail3()).isNullOrEmpty();

    }

    @Test
    public void writePeopleToSkipTrace_badInputs_nullFile() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<WritePeopleToSkipTraceStatus> testObserver;

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        List<Person> listToWrite = new ArrayList<>();
        listToWrite.add(person);

        //
        //Act
        //
        testObserver = csvFileControllerImpl.writePeopleToSkipTrace(null, listToWrite).test();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();

        WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus = (WritePeopleToSkipTraceStatus) testObserver.getEvents().get(0).get(0);
        assertThat(writePeopleToSkipTraceStatus.success()).isFalse();
        assertThat(writePeopleToSkipTraceStatus.linesWritten()).isEqualTo(0);
        assertThat(writePeopleToSkipTraceStatus.errorMessage()).isEqualToIgnoringCase(CsvFileControllerImpl.FILE_OR_DATA_MISSING);
    }

    @Test
    public void writePeopleToSkipTrace_badInputs_nullList() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<WritePeopleToSkipTraceStatus> testObserver;

        final String fileName = "build/tmp/ReiSkipTraceTest.csv";
        File fileToWrite = new File(fileName);

        //
        //Act
        //
        testObserver = csvFileControllerImpl.writePeopleToSkipTrace(fileToWrite, null).test();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();

        WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus = (WritePeopleToSkipTraceStatus) testObserver.getEvents().get(0).get(0);
        assertThat(writePeopleToSkipTraceStatus.success()).isFalse();
        assertThat(writePeopleToSkipTraceStatus.linesWritten()).isEqualTo(0);
        assertThat(writePeopleToSkipTraceStatus.errorMessage()).isEqualToIgnoringCase(CsvFileControllerImpl.FILE_OR_DATA_MISSING);
    }

    @Test
    public void writePeopleToSkipTrace_badInputs_emptyList() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<WritePeopleToSkipTraceStatus> testObserver;

        final String fileName = "build/tmp/ReiSkipTraceTest.csv";
        File fileToWrite = new File(fileName);

        List<Person> listToWrite = new ArrayList<>();

        //
        //Act
        //
        testObserver = csvFileControllerImpl.writePeopleToSkipTrace(fileToWrite, listToWrite).test();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();

        WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus = (WritePeopleToSkipTraceStatus) testObserver.getEvents().get(0).get(0);
        assertThat(writePeopleToSkipTraceStatus.success()).isFalse();
        assertThat(writePeopleToSkipTraceStatus.linesWritten()).isEqualTo(0);
        assertThat(writePeopleToSkipTraceStatus.errorMessage()).isEqualToIgnoringCase(CsvFileControllerImpl.FILE_OR_DATA_MISSING);
    }

    @Test
    public void writePeopleToSkipTrace_oneOutput() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<WritePeopleToSkipTraceStatus> testObserver;

        final String fileName = "build/tmp/ReiSkipTraceTest.csv";
        File fileToWrite = new File(fileName);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        List<Person> listToWrite = new ArrayList<>();
        listToWrite.add(person);

        //
        //Act
        //
        testObserver = csvFileControllerImpl.writePeopleToSkipTrace(fileToWrite, listToWrite).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus = (WritePeopleToSkipTraceStatus) testObserver.getEvents().get(0).get(0);
        assertThat(writePeopleToSkipTraceStatus.success()).isTrue();
        assertThat(writePeopleToSkipTraceStatus.linesWritten()).isEqualTo(1);
        assertThat(writePeopleToSkipTraceStatus.errorMessage()).isEmpty();

        //Load file and do a comparison
        TestObserver<PersonFromSkipTrace> testObserver2;
        testObserver2 = csvFileControllerImpl.readPeopleFromSkipTrace(fileToWrite).test();

        testObserver2.assertComplete();
        testObserver2.assertNoErrors();
        testObserver2.assertValueCount(1);

        //remember - no phone numbers so won't read file...so ok. Just check file is there i guess...
        PersonFromSkipTrace personFromSkipTrace = (PersonFromSkipTrace) testObserver2.getEvents().get(0).get(0);
        assertThat(personFromSkipTrace.hasError()).isTrue();

        //
        //Clean Up - delete file
        //
        fileToWrite.delete();
    }

    @Test
    public void writePeopleToSkipTrace_oneOutput_withPhoneNumber_thatWontWrite() {
        //
        //Arrange
        //
        CsvHelper csvHelper = new CsvHelper();
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl(csvHelper);
        TestObserver<WritePeopleToSkipTraceStatus> testObserver;

        final String fileName = "build/tmp/ReiSkipTraceTest.csv";
        File fileToWrite = new File(fileName);

        Property property = createProperty();
        Person person = createPerson();
        person.addProperty(property);

        Phone phone = new Phone();
        phone.setPhoneNumber("123-456-7899");
        phone.setPhoneTelco("Verizon");
        phone.setPhoneLitigation(true);
        phone.setPhoneDNC(true);
        phone.setPhoneStop(true);
        phone.setPhoneType("LandLine");

        person.setPhone1(phone);
        person.setEmail1("test@gmail.com");

        List<Person> listToWrite = new ArrayList<>();
        listToWrite.add(person);

        //
        //Act
        //
        testObserver = csvFileControllerImpl.writePeopleToSkipTrace(fileToWrite, listToWrite).test();
        testScheduler.triggerActions();

        //
        //Assert
        //
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus = (WritePeopleToSkipTraceStatus) testObserver.getEvents().get(0).get(0);
        assertThat(writePeopleToSkipTraceStatus.success()).isTrue();
        assertThat(writePeopleToSkipTraceStatus.linesWritten()).isEqualTo(1);
        assertThat(writePeopleToSkipTraceStatus.errorMessage()).isEmpty();

        //Load file and do a comparison - remember, you NEVER write a phone number so loading will fail because
        // you can't load a REISkip that's missing a phone number:-)
        TestObserver<PersonFromSkipTrace> testObserver2;
        testObserver2 = csvFileControllerImpl.readPeopleFromSkipTrace(fileToWrite).test();

        testObserver2.assertComplete();
        testObserver2.assertNoErrors();
        testObserver2.assertValueCount(1);

        //remember
        PersonFromSkipTrace personFromSkipTrace = (PersonFromSkipTrace) testObserver2.getEvents().get(0).get(0);
        assertThat(personFromSkipTrace.hasError()).isTrue();

        //
        //Clean Up - delete file
        //
        fileToWrite.delete();
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