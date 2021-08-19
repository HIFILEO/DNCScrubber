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
import com.LEO.DNCScrubber.rx.RxJavaTest;
import com.LEO.DNCScrubber.util.Visitors;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl();
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
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl();
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
        CsvFileControllerImpl csvFileControllerImpl = new CsvFileControllerImpl();
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
}