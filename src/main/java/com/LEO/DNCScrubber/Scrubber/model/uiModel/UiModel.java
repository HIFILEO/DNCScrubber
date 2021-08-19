package com.LEO.DNCScrubber.Scrubber.model.uiModel;/*
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
import io.reactivex.annotations.NonNull;

public class UiModel {
    private final String screenMessage;
    private final boolean inFlight;
    private final boolean exit;
    private final boolean showLoadFileDialog;
    private final CommandType command;

    public UiModel(String screenMessage, boolean inFlight, boolean exit, boolean showLoadFileDialog,
                   CommandType command) {
        this.screenMessage = screenMessage;
        this.inFlight = inFlight;
        this.exit = exit;
        this.command = command;
        this.showLoadFileDialog = showLoadFileDialog;
    }

    public static UiModel init(String screenMessage) {
        return new UiModel(screenMessage, false, false, false, CommandType.NONE);
    }

    public static UiModel exit() {
        return new UiModel(null, false, true, false,
                CommandType.NONE);
    }

    public String getScreenMessage() {
        return screenMessage;
    }

    public boolean isInFlight() {
        return inFlight;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isShowLoadFileDialog() {
        return showLoadFileDialog;
    }

    public CommandType getCommand() {
        return command;
    }

    /**
     * Too many state? Too many params in constructors? Call on the builder pattern to Save The Day!.
     */
    public static class UiModelBuilder {
        private final UiModel uiModel;
        private String screenMessage;
        private boolean inFlight;
        private boolean exit;
        private boolean showLoadFileDialog;
        private CommandType command;

        /**
         * Construct Builder using defaults from previous {@link UiModel}.
         * @param uiModel - model for builder to use.
         */
        public UiModelBuilder(@NonNull UiModel uiModel) {
            this.uiModel = uiModel;

            this.screenMessage = uiModel.screenMessage;
            this.inFlight = uiModel.inFlight;
            this.exit = uiModel.exit;
            this.command = uiModel.command;
        }

        /**
         * Create the {@link UiModel} using the types in {@link UiModelBuilder}.
         * @return new {@link UiModel}.
         */
        public UiModel createUiModel() {
            return new UiModel(screenMessage, inFlight, exit, showLoadFileDialog, command);
        }

        public UiModelBuilder setScreenMessage(String screenMessage) {
            this.screenMessage = screenMessage;
            return this;
        }

        public UiModelBuilder setInFlight(boolean inFlight) {
            this.inFlight = inFlight;
            return this;
        }

        public UiModelBuilder setExit(boolean exit) {
            this.exit = exit;
            return this;
        }

        public UiModelBuilder setShowLoadFileDialog(boolean showLoadFileDialog) {
            this.showLoadFileDialog = showLoadFileDialog;
            return this;
        }

        public UiModelBuilder setCommand(CommandType command) {
            this.command = command;
            return this;
        }
    }
}
