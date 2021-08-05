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
import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import io.reactivex.Observable;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

/**
 * {@link CsvFileController} implementation using Open CSV - http://opencsv.sourceforge.net/
 */
public class CsvFileControllerImpl implements CsvFileController {

    @Override
    public Observable<RawLead> readRawLeads(File file) {
        return Observable.create(emitter -> {
            CsvToBeanBuilder cvsToBeanBuilder = new CsvToBeanBuilder(new FileReader(file));

            // Verify the data, throw exception if data isn't ready to be processed.
            //cvsToBeanBuilder.withVerifier(new RawLedVerifier());

            //suppress errors on iterator so you can log them
            //https://stackoverflow.com/questions/58807420/how-to-create-a-list-of-valid-csv-records-by-logging-the-error-of-invalid-record
            cvsToBeanBuilder.withThrowExceptions(false);

            CsvToBean csvToBean = cvsToBeanBuilder.withType(RawLeadCsvImpl.class).build();
            for (RawLeadCsvImpl rawLeadCsv : (Iterable<RawLeadCsvImpl>) csvToBean) emitter.onNext(rawLeadCsv);

            //Report Malformed Data errors as RawLeads w/ errors
            //Note - well architected streams don't call onError for valid response. Send specific types & "break the stream".
            //Note - you can get multiple exceptions on one line. Send them down, it's the message that clarifies problems.
            for (CsvException csvException : (Iterable<CsvException>) csvToBean.getCapturedExceptions()) {

                StringBuilder errorStringBuilder = new StringBuilder();
                errorStringBuilder.append(csvException.getLineNumber());
                errorStringBuilder.append(" - ");
                errorStringBuilder.append(csvException.getMessage());

                RawLeadErrorImpl rawLeadError = new RawLeadErrorImpl(errorStringBuilder.toString());
                emitter.onNext(rawLeadError);
            }

            emitter.onComplete();
        });
    }
}
