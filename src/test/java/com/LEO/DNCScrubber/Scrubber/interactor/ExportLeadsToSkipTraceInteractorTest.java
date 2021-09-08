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

import com.LEO.DNCScrubber.Scrubber.controller.CsvFileController;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.action.ExportLeadsToSkipTraceAction;
import com.LEO.DNCScrubber.Scrubber.model.result.ExportLeadsToSkipTraceResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;


class ExportLeadsToSkipTraceInteractorTest extends RxJavaTest {

    @Mock
    CsvFileController csvFileControllerMock;

    @Mock
    DatabaseGateway databaseGatewayMock;

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    @Test
    public void processExportLeadsToSkipTraceAction_InFlight() {
        //
        //Arrange
        //
        TestObserver<ExportLeadsToSkipTraceResult> testObserver;


        ExportLeadsToSkipTraceInteractor exportLeadsToSkipTraceInteractor =
                new ExportLeadsToSkipTraceInteractor(csvFileControllerMock, databaseGatewayMock);

        ExportLeadsToSkipTraceAction exportLeadsToSkipTraceAction =
                new ExportLeadsToSkipTraceAction(new File("Never Used"));

        //
        //Act
        //
        testObserver = exportLeadsToSkipTraceInteractor.processExportLeadsToSkipTraceAction(
                exportLeadsToSkipTraceAction).test();
        testScheduler.triggerActions();
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);

        ExportLeadsToSkipTraceResult exportLeadsToSkipTraceResult = (ExportLeadsToSkipTraceResult) testObserver
                .getEvents().get(0).get(0);

        assertThat(exportLeadsToSkipTraceResult.getType()).isEqualTo(Result.ResultType.IN_FLIGHT);
    }
}