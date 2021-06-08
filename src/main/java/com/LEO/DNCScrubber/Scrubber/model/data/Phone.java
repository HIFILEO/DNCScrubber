package com.LEO.DNCScrubber.Scrubber.model.data;/*
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

/**
 * Represents a Phone number.
 */
public class Phone {
    private String phoneNumber = "";
    private String phoneType = "";
    private boolean phoneDNC;
    private boolean phoneStop;
    private boolean phoneLitigation;
    private String phoneTelco = "";

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public boolean isPhoneDNC() {
        return phoneDNC;
    }

    public void setPhoneDNC(boolean phoneDNC) {
        this.phoneDNC = phoneDNC;
    }

    public boolean isPhoneStop() {
        return phoneStop;
    }

    public void setPhoneStop(boolean phoneStop) {
        this.phoneStop = phoneStop;
    }

    public boolean isPhoneLitigation() {
        return phoneLitigation;
    }

    public void setPhoneLitigation(boolean phoneLitigation) {
        this.phoneLitigation = phoneLitigation;
    }

    public String getPhoneTelco() {
        return phoneTelco;
    }

    public void setPhoneTelco(String phoneTelco) {
        this.phoneTelco = phoneTelco;
    }
}
