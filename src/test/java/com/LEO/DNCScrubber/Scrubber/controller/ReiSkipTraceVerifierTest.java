package com.LEO.DNCScrubber.Scrubber.controller;

import com.LEO.DNCScrubber.Scrubber.controller.model.ReiSkipTraceCsv;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import com.opencsv.exceptions.CsvConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
class ReiSkipTraceVerifierTest extends RxJavaTest {

    @Test
    public void reiSkipTraceVerifier_pass() {
        //
        //Arrange
        //
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
                true,
                "test phone 1 telcoName",
                "test phone 2 number",
                "test phone 2 type",
                true,
                true,
                "test phone 2 telcoName",
                "test phone 3 number",
                "test phone 3 type",
                true,
                true,
                "test phone 3 telcoName",
                "test email 1",
                "test email 2",
                "test email 3"
        );

        ReiSkipTraceVerifier reiSkipTraceVerifier = new ReiSkipTraceVerifier();

        //
        //Act
        //
        boolean result = false;
        try {
            result = reiSkipTraceVerifier.verifyBean(reiSkipTraceCsv);
        } catch (CsvConstraintViolationException e) {
            e.printStackTrace();
        }

        //
        //Assert
        //
        assertThat(result).isTrue();
    }

    @Test
    public void reiSkipTraceVerifier_failPhone() {
        ReiSkipTraceCsv reiSkipTraceCsv = new ReiSkipTraceCsv(
                "Test First Name",
                "Test Last Name",
                "test property address",
                "test property city",
                "test property state",
                "test property zip",
                "test mailing address",
                "test mailing city",
                "test mailing state",
                "test mailing zip",
                "",
                "test phone 1 type",
                true,
                true,
                "test phone 1 telcoName",
                "",
                "test phone 2 type",
                true,
                true,
                "test phone 2 telcoName",
                "",
                "test phone 3 type",
                true,
                true,
                "test phone 3 telcoName",
                "test email 1",
                "test email 2",
                "test email 3"
        );

        ReiSkipTraceVerifier reiSkipTraceVerifier = new ReiSkipTraceVerifier();

        //
        //Act
        //
        boolean result = false;
        Exception exception = null;
        try {
            result = reiSkipTraceVerifier.verifyBean(reiSkipTraceCsv);
        } catch (CsvConstraintViolationException e) {
            exception = e;
        }

        //
        //Assert
        //
        assertThat(result).isFalse();
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualToIgnoringCase(ReiSkipTraceVerifier.NO_PHONE_NUMBERS_ERROR_MSG);
    }

    @Test
    public void reiSkipTraceVerifier_nullPhone() {
        ReiSkipTraceCsv reiSkipTraceCsv = new ReiSkipTraceCsv(
                null,
                null,
                "test property address",
                "test property city",
                "test property state",
                "test property zip",
                "test mailing address",
                "test mailing city",
                "test mailing state",
                "test mailing zip",
                null,
                "test phone 1 type",
                true,
                true,
                "test phone 1 telcoName",
                null,
                "test phone 2 type",
                true,
                true,
                "test phone 2 telcoName",
                null,
                "test phone 3 type",
                true,
                true,
                "test phone 3 telcoName",
                "test email 1",
                "test email 2",
                "test email 3"
        );

        ReiSkipTraceVerifier reiSkipTraceVerifier = new ReiSkipTraceVerifier();

        //
        //Act
        //
        boolean result = false;
        Exception exception = null;
        try {
            result = reiSkipTraceVerifier.verifyBean(reiSkipTraceCsv);
        } catch (CsvConstraintViolationException e) {
            exception = e;
        }

        //
        //Assert
        //
        assertThat(result).isFalse();
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualToIgnoringCase(ReiSkipTraceVerifier.NO_PHONE_NUMBERS_ERROR_MSG);
    }
}