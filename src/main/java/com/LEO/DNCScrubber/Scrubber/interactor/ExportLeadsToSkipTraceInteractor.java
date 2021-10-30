package com.LEO.DNCScrubber.Scrubber.interactor;/*
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

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileController;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.WritePeopleToSkipTraceStatus;
import com.LEO.DNCScrubber.Scrubber.model.action.ExportLeadsToSkipTraceAction;
import com.LEO.DNCScrubber.Scrubber.model.data.Person;
import com.LEO.DNCScrubber.Scrubber.model.result.ExportLeadsToSkipTraceResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

import java.util.List;

public class ExportLeadsToSkipTraceInteractor {
    private final CsvFileController csvFileController;
    private final DatabaseGateway databaseGateway;

    /**
     * Constructor
     * @param csvFileController - CSV File Reader
     * @param databaseGateway - Database Accessor
     */
    public ExportLeadsToSkipTraceInteractor(CsvFileController csvFileController, DatabaseGateway databaseGateway) {
        this.csvFileController = csvFileController;
        this.databaseGateway = databaseGateway;
    }

    /**
     * Process {@link ExportLeadsToSkipTraceAction}
     * @param exportLeadsToSkipTraceAction - action to process
     * @return - result status - in progress, failure, completion
     */
    public Observable<ExportLeadsToSkipTraceResult> processExportLeadsToSkipTraceAction(
            ExportLeadsToSkipTraceAction exportLeadsToSkipTraceAction) {
        // 1- load all the People that don't have a phone number
        //      Note - there can be more than one owner on a property - we will skip trace them both
        // 2 - export the leads to a CSV
        //      Note - specific to REISkipTrace and their requirements
        // 3 - return status
        return databaseGateway.loadPersonsWithNoPhoneNumber()
            .flatMap(new Function<List<Person>, SingleSource<WritePeopleToSkipTraceStatus>>() {
                @Override
                public SingleSource<WritePeopleToSkipTraceStatus> apply(List<Person> people) throws Exception {
                    return csvFileController.writePeopleToSkipTrace(
                            exportLeadsToSkipTraceAction.getCsvFile(),
                            people
                    );
                }
            })
            .flatMap(new Function<WritePeopleToSkipTraceStatus, SingleSource<ExportLeadsToSkipTraceResult>>() {
                @Override
                public SingleSource<ExportLeadsToSkipTraceResult> apply(WritePeopleToSkipTraceStatus writePeopleToSkipTraceStatus) throws Exception {
                    if (writePeopleToSkipTraceStatus.success()) {
                        return Single.just(new ExportLeadsToSkipTraceResult(Result.ResultType.SUCCESS,
                                "",
                                false,
                                writePeopleToSkipTraceStatus.linesWritten()));
                    } else {
                        return Single.just(new ExportLeadsToSkipTraceResult(Result.ResultType.FAILURE,
                                writePeopleToSkipTraceStatus.errorMessage(),
                                false,
                                writePeopleToSkipTraceStatus.linesWritten()));
                    }
                }
            })
            .toObservable()
            .startWith(new ExportLeadsToSkipTraceResult(Result.ResultType.IN_FLIGHT, "", false, 0));
    }
}
