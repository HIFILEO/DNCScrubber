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

import com.LEO.DNCScrubber.Scrubber.controller.model.*;
import com.LEO.DNCScrubber.Scrubber.model.WritePeopleToSkipTraceStatus;
import com.LEO.DNCScrubber.Scrubber.model.data.Person;
import com.LEO.DNCScrubber.Scrubber.model.data.PersonFromSkipTrace;
import com.LEO.DNCScrubber.Scrubber.model.data.Property;
import com.LEO.DNCScrubber.Scrubber.model.data.RawLead;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvException;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link CsvFileController} implementation using Open CSV - http://opencsv.sourceforge.net/
 */
public class CsvFileControllerImpl implements CsvFileController {
    public static final String FILE_OR_DATA_MISSING = "Invalid data or file used.";
    private final CsvHelper csvHelper;

    public CsvFileControllerImpl(CsvHelper csvHelper) {
        this.csvHelper = csvHelper;
    }

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
            //Note - well architected streams don't call onError for valid response. Send specific types & don't "break the stream".
            //Note - you can get multiple exceptions on one line. Send them down, it's the message that clarifies problems.
            //Note - calling onError - kills the stream and you can't call multiple errors which this can throw.
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

    @Override
    public Single<WritePeopleToSkipTraceStatus> writePeopleToSkipTrace(File file, List<Person> peopleList) {
        if (file == null || peopleList == null || peopleList.isEmpty()) {
            return Single.just(new WritePeopleToSkipTraceStatusImpl(
                    false,
                    FILE_OR_DATA_MISSING,
                    0
                    ));
        } else
            return Single.create(new SingleOnSubscribe<WritePeopleToSkipTraceStatus>() {
                @Override
                public void subscribe(SingleEmitter<WritePeopleToSkipTraceStatus> emitter) throws Exception {
                    //A person can have multiple properties, each will be their own line in the output.
                    List<ReiSkipTraceCsv> listToWrite = new ArrayList<>();
                    for (Person person : peopleList) {
                        for (Property property : person.getPropertyList()) {
                            listToWrite.add(csvHelper.translatePersonToReiSkipTraceCsv(person, property));
                        }
                    }

                    //Write the beans - in ORDER
                    //https://stackoverflow.com/questions/45203867/opencsv-how-to-create-csv-file-from-pojo-with-custom-column-headers-and-custom
                    Writer writer = new FileWriter(file);
                    HeaderColumnNameMappingStrategy<ReiSkipTraceCsv> strategy = new HeaderColumnNameMappingStrategyBuilder<ReiSkipTraceCsv>().build();
                    strategy.setType(ReiSkipTraceCsv.class);
                    strategy.setColumnOrderOnWrite(new OrderedComparatorIgnoringCase(ReiSkipTraceCsv.FIELDS_ORDER));
                    StatefulBeanToCsv beanToCsv;
                    beanToCsv = new StatefulBeanToCsvBuilder(writer)
                            .withMappingStrategy(strategy)
                            .build();
                    beanToCsv.write(listToWrite);
                    writer.close();

                    //report status
                    emitter.onSuccess(new WritePeopleToSkipTraceStatusImpl(
                            true,
                            "",
                            listToWrite.size()
                    ));
                }
            });
    }

    @Override
    public Observable<PersonFromSkipTrace> readPeopleFromSkipTrace(File file) {
        return Observable.create(emitter -> {
            CsvToBeanBuilder cvsToBeanBuilder = new CsvToBeanBuilder(new FileReader(file));

            //verify data
            cvsToBeanBuilder.withVerifier(new ReiSkipTraceVerifier());

            //suppress errors on iterator so you can log them
            //https://stackoverflow.com/questions/58807420/how-to-create-a-list-of-valid-csv-records-by-logging-the-error-of-invalid-record
            cvsToBeanBuilder.withThrowExceptions(false);

            CsvToBean csvToBean = cvsToBeanBuilder.withType(ReiSkipTraceCsv.class).build();
            for (ReiSkipTraceCsv reiSkipTraceCsv : (Iterable<ReiSkipTraceCsv>) csvToBean) {
                emitter.onNext(csvHelper.translateReiSkipTraceCsvToPersonFromSkipTrace(reiSkipTraceCsv));
            }

            //Report Malformed Data errors as RawLeads w/ errors
            //Note - well architected streams don't call onError for valid response. Send specific types & don't "break the stream".
            //Note - you can get multiple exceptions on one line. Send them down, it's the message that clarifies problems.
            //Note - calling onError - kills the stream and you can't call multiple errors which this can throw.
            for (CsvException csvException : (Iterable<CsvException>) csvToBean.getCapturedExceptions()) {

                StringBuilder errorStringBuilder = new StringBuilder();
                errorStringBuilder.append(csvException.getLineNumber());
                errorStringBuilder.append(" - ");
                errorStringBuilder.append(csvException.getMessage());

                emitter.onNext(new PersonFromSkipTraceImpl(null, errorStringBuilder.toString()));
            }

            emitter.onComplete();
        });
    }
}
