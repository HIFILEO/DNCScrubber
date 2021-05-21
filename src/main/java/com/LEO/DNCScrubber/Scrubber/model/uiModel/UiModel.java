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
    private final CommandType previousCommand;

    public UiModel(String screenMessage, boolean inFlight, boolean exit, CommandType previousCommand) {
        this.screenMessage = screenMessage;
        this.inFlight = inFlight;
        this.exit = exit;
        this.previousCommand = previousCommand;
    }

    public static UiModel init(String screenMessage) {
        return new UiModel(screenMessage, false, false, CommandType.NONE);
    }

    public static UiModel exit() {
        return new UiModel(null, false, true, CommandType.NONE);
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

    public CommandType getPreviousCommand() {
        return previousCommand;
    }

    /**
     * Too many state? Too many params in constructors? Call on the builder pattern to Save The Day!.
     */
    public static class UiModelBuilder {
        private final UiModel uiModel;

        private String screenMessage;
        private boolean inFlight;
        private boolean exit;
        private CommandType previousCommand;

        /**
         * Construct Builder using defaults from previous {@link UiModel}.
         * @param uiModel - model for builder to use.
         */
        public UiModelBuilder(@NonNull UiModel uiModel) {
            this.uiModel = uiModel;

            this.screenMessage = uiModel.screenMessage;
            this.inFlight = uiModel.inFlight;
            this.exit = uiModel.exit;
            this.previousCommand = uiModel.previousCommand;
        }

        /**
         * Create the {@link UiModel} using the types in {@link UiModelBuilder}.
         * @return new {@link UiModel}.
         */
        public UiModel createUiModel() {
            return new UiModel(screenMessage, inFlight, exit, previousCommand);
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

        public UiModelBuilder setPreviousCommand(CommandType previousCommand) {
            this.previousCommand = previousCommand;
            return this;
        }
    }
}
