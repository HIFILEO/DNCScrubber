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

import com.LEO.DNCScrubber.Scrubber.model.WritePeopleToSkipTraceStatus;
import com.LEO.DNCScrubber.Scrubber.model.data.Person;
import com.LEO.DNCScrubber.Scrubber.model.data.PersonFromSkipTrace;
import com.LEO.DNCScrubber.Scrubber.model.data.RawLead;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.io.File;
import java.util.List;

/**
 * Controller for interacting with CSV File types.
 */
public interface CsvFileController {

    /**
     * Reads the given CSV file and returns each line as it's own {@link RawLead} object.
     *
     * @param file - a CSV file that conforms to the {@link RawLead}
     * @return {@link RawLead} (including ones with errors) until EOF is reached, then On Complete is called.
     * Will throw OnError where appropriate.
     */
    Observable<RawLead> readRawLeads(File file);

    /**
     * Save the given list to the file. Note - if list is empty the file will still be created.
     *
     * Note - Phone numbers & emails will automatically be omitted in the write to file.
     *
     * @param file - a file that we will write all the {@link Person} To.
     * @param peopleList - {@link List} of {@link Person} to save to CSV File
     * @return - {@link WritePeopleToSkipTraceStatus}
     */
    Single<WritePeopleToSkipTraceStatus> writePeopleToSkipTrace(File file, List<Person> peopleList);

    /**
     * Reads the given CSV file and returns each line as it's own {@link PersonFromSkipTrace} object.
     *
     * @param file - a CSV file that conforms to the {@link PersonFromSkipTrace}
     * @return {@link PersonFromSkipTrace} (including ones with errors) until EOF is reached, then On Complete is called.
     * Will throw OnError where appropriate.
     */
    Observable<PersonFromSkipTrace> readPeopleFromSkipTrace(File file);
}
