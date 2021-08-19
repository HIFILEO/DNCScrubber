package com.LEO.DNCScrubber.Scrubber.model.action;/*
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

import com.LEO.DNCScrubber.Scrubber.model.CommandType;

public class LoadFileDialogAction extends Action {
    private final CommandType commandType;
    private final boolean userCanceled;
    private final boolean fileLoadError;
    private final String errorMessage;

    public LoadFileDialogAction(CommandType commandType, boolean userCanceled, boolean fileLoadError,
                                String errorMessage) {
        this.commandType = commandType;
        this.userCanceled = userCanceled;
        this.fileLoadError = fileLoadError;
        this.errorMessage = errorMessage;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public boolean isUserCanceled() {
        return userCanceled;
    }

    public boolean isFileLoadError() {
        return fileLoadError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
