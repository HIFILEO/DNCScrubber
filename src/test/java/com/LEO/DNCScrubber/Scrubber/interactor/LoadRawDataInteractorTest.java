package com.LEO.DNCScrubber.Scrubber.interactor;
/*
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

import com.LEO.DNCScrubber.rx.RxJavaTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
public class LoadRawDataInteractorTest extends RxJavaTest {

    @Before
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void DatabaseStatusCounterScanner_addDuplicate() throws Exception {
        //
        //Arrange
        //
        LoadRawDataInteractor.DatabaseStatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.DatabaseStatusCounterScanner();

        LoadRawDataInteractor.DatabaseStatusCounter databaseStatusCounter =
                new LoadRawDataInteractor.DatabaseStatusCounter();
        databaseStatusCounter.numberOfColdLeadDuplicates = 10;
        databaseStatusCounter.numberOfColdLeadsAdded = 9;

        LoadRawDataInteractor.DatabaseStatus databaseStatus = new LoadRawDataInteractor.DatabaseStatus();
        databaseStatus.duplicateColdRvmEntry = true;

        //
        //Act
        //
        LoadRawDataInteractor.DatabaseStatusCounter counterToTest =
                databaseStatusCounterScanner.apply(databaseStatusCounter, databaseStatus);

        //
        //Assert
        //
        assertThat(counterToTest.numberOfColdLeadDuplicates).isEqualTo(11);
        assertThat(counterToTest.numberOfColdLeadsAdded).isEqualTo(9);
    }

    @Test
    public void DatabaseStatusCounterScanner_addNew() {
        //
        //Arrange
        //
        LoadRawDataInteractor.DatabaseStatusCounterScanner databaseStatusCounterScanner =
                new LoadRawDataInteractor.DatabaseStatusCounterScanner();

        //
        //Act
        //

        //
        //Assert
        //
    }

}