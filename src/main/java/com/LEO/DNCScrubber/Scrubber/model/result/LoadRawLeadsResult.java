package com.LEO.DNCScrubber.Scrubber.model.result;/*
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

import java.io.File;

public class LoadRawLeadsResult extends Result {
    private final @ResultType int resultType;
    private final String errorMessage;
    private final boolean userCanceled;
    private final boolean fileLoadError;
    private final int numberOfColdLeadDuplicates;
    private final int numberOfColdLeadsSaved;
    private final int numberOfColdLeadErrors;
    public LoadRawLeadsResult(int resultType, String errorMessage, boolean userCanceled,
                              boolean fileLoadError, int numberOfColdLeadDuplicates, int numberOfColdLeadsSaved,
                              int numberOfColdLeadErrors) {
        this.resultType = resultType;
        this.errorMessage = errorMessage;
        this.userCanceled = userCanceled;
        this.fileLoadError = fileLoadError;
        this.numberOfColdLeadsSaved = numberOfColdLeadsSaved;
        this.numberOfColdLeadErrors = numberOfColdLeadErrors;
        this.numberOfColdLeadDuplicates = numberOfColdLeadDuplicates;
    }

    public static LoadRawLeadsResult inFlight() {
        return new LoadRawLeadsResult(ResultType.IN_FLIGHT, "", false,
                false, 0 , 0, 0);
    }

    public static LoadRawLeadsResult success(int numberOfColdLeadDuplicates, int numberOfColdLeadsSaved,
                                             int numberOfColdLeadErrors) {
        return new LoadRawLeadsResult(Result.ResultType.SUCCESS,
                "",
                false,
                false,
                numberOfColdLeadDuplicates,
                numberOfColdLeadsSaved,
                numberOfColdLeadErrors);
    }

    public static LoadRawLeadsResult error(String errorMessage, boolean fileLoadError) {
        return new LoadRawLeadsResult(Result.ResultType.FAILURE,
                errorMessage,
                false,
                fileLoadError,
                0,
                0,
                0);
    }

    @Override
    public int getType() {
        return resultType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isUserCanceled() {
        return userCanceled;
    }

    public boolean isFileLoadError() {
        return fileLoadError;
    }

    public int getNumberOfColdLeadDuplicates() {
        return numberOfColdLeadDuplicates;
    }

    public int getNumberOfColdLeadsSaved() {
        return numberOfColdLeadsSaved;
    }

    public int getNumberOfColdLeadErrors() {
        return numberOfColdLeadErrors;
    }
}
