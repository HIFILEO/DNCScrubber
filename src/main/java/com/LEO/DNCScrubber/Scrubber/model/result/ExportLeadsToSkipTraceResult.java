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

public class ExportLeadsToSkipTraceResult extends Result {
    private final @ResultType int resultType;
    private final String errorMessage;
    private final boolean fileLoadError;
    private final int numberOfLeadsExported;

    public ExportLeadsToSkipTraceResult(int resultType, String errorMessage, boolean fileLoadError,
                                        int numberOfLeadsExported) {
        this.resultType = resultType;
        this.errorMessage = errorMessage;

        //TODO - remove this no?
        this.fileLoadError = fileLoadError;
        this.numberOfLeadsExported = numberOfLeadsExported;
    }

    public static ExportLeadsToSkipTraceResult inFlight() {
        return new ExportLeadsToSkipTraceResult(ResultType.IN_FLIGHT, "", false,
                0);
    }

    public static ExportLeadsToSkipTraceResult success(int numberOfLeadsExported, String errorMessage) {
        return new ExportLeadsToSkipTraceResult(ResultType.SUCCESS,
                errorMessage,
                false,
                numberOfLeadsExported
                );
    }

    public static ExportLeadsToSkipTraceResult error(String errorMessage, boolean fileLoadError) {
        return new ExportLeadsToSkipTraceResult(ResultType.FAILURE,
                errorMessage,
                fileLoadError,
                0);
    }

    @Override
    public int getType() {
        return resultType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isFileLoadError() {
        return fileLoadError;
    }

    public int getNumberOfLeadsExported() {
        return numberOfLeadsExported;
    }
}
