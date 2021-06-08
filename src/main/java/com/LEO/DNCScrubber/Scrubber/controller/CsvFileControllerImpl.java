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

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.reactivex.Observable;

import java.io.File;
import java.io.FileReader;

/**
 * {@link CsvFileController} implementation using Open CSV - http://opencsv.sourceforge.net/
 */
public class CsvFileControllerImpl implements CsvFileController {

    @Override
    public Observable<RawLeadCsv> readRawLeads(File file) {
       return Observable.create(emitter -> {
           CsvToBeanBuilder cvsToBeanBuilder = new CsvToBeanBuilder(new FileReader(file));
           CsvToBean csvToBean = cvsToBeanBuilder.withType(RawLeadCsvCVSImp.class).build();

           for (RawLeadCsvCVSImp rawLeadData : (Iterable<RawLeadCsvCVSImp>) csvToBean) {
               emitter.onNext(rawLeadData);
           }

           emitter.onComplete();
       });
    }
}
