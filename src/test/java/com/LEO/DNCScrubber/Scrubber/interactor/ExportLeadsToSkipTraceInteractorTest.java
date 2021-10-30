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
import com.LEO.DNCScrubber.Scrubber.controller.model.WritePeopleToSkipTraceStatusImpl;
import com.LEO.DNCScrubber.Scrubber.gateway.DatabaseGateway;
import com.LEO.DNCScrubber.Scrubber.model.action.ExportLeadsToSkipTraceAction;
import com.LEO.DNCScrubber.Scrubber.model.result.ExportLeadsToSkipTraceResult;
import com.LEO.DNCScrubber.Scrubber.model.result.Result;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    public void processExportLeadsToSkipTraceAction_Success() {
        //
        //Arrange
        //
        TestObserver<ExportLeadsToSkipTraceResult> testObserver;

        final int TEN = 10;
        ExportLeadsToSkipTraceInteractor exportLeadsToSkipTraceInteractor =
                new ExportLeadsToSkipTraceInteractor(csvFileControllerMock, databaseGatewayMock);

        ExportLeadsToSkipTraceAction exportLeadsToSkipTraceAction =
                new ExportLeadsToSkipTraceAction(new File("Never Used"));

        when(databaseGatewayMock.loadPersonsWithNoPhoneNumber()).thenReturn(Single.just(new ArrayList<>()));
        when(csvFileControllerMock.writePeopleToSkipTrace(any(), any())).thenReturn(
                Single.just(new WritePeopleToSkipTraceStatusImpl(true, "", TEN))
        );

        //
        //Act
        //
        testObserver = exportLeadsToSkipTraceInteractor.processExportLeadsToSkipTraceAction(
                exportLeadsToSkipTraceAction).test();
        testScheduler.triggerActions();


        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);

        //in-flight
        ExportLeadsToSkipTraceResult exportLeadsToSkipTraceResult = (ExportLeadsToSkipTraceResult) testObserver
                .getEvents().get(0).get(0);

        assertThat(exportLeadsToSkipTraceResult.getType()).isEqualTo(Result.ResultType.IN_FLIGHT);

        //success
        exportLeadsToSkipTraceResult = (ExportLeadsToSkipTraceResult) testObserver
                .getEvents().get(0).get(1);
        assertThat(exportLeadsToSkipTraceResult.getType()).isEqualTo(Result.ResultType.SUCCESS);
        assertThat(exportLeadsToSkipTraceResult.getNumberOfLeadsExported()).isEqualTo(TEN);
        assertThat(exportLeadsToSkipTraceResult.getErrorMessage()).isEmpty();
        assertThat(exportLeadsToSkipTraceResult.isFileLoadError()).isFalse();
    }

    @Test
    public void processExportLeadsToSkipTraceAction_Failure() {
        //
        //Arrange
        //
        TestObserver<ExportLeadsToSkipTraceResult> testObserver;

        final int ZERO = 0;
        final String ERROR_MSG = "HAHA YOU FAIL";

        ExportLeadsToSkipTraceInteractor exportLeadsToSkipTraceInteractor =
                new ExportLeadsToSkipTraceInteractor(csvFileControllerMock, databaseGatewayMock);

        ExportLeadsToSkipTraceAction exportLeadsToSkipTraceAction =
                new ExportLeadsToSkipTraceAction(new File("Never Used"));

        when(databaseGatewayMock.loadPersonsWithNoPhoneNumber()).thenReturn(Single.just(new ArrayList<>()));
        when(csvFileControllerMock.writePeopleToSkipTrace(any(), any())).thenReturn(
                Single.just(new WritePeopleToSkipTraceStatusImpl(false, ERROR_MSG, ZERO))
        );

        //
        //Act
        //
        testObserver = exportLeadsToSkipTraceInteractor.processExportLeadsToSkipTraceAction(
                exportLeadsToSkipTraceAction).test();
        testScheduler.triggerActions();


        //
        //Assert
        //
        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);

        //in-flight
        ExportLeadsToSkipTraceResult exportLeadsToSkipTraceResult = (ExportLeadsToSkipTraceResult) testObserver
                .getEvents().get(0).get(0);

        assertThat(exportLeadsToSkipTraceResult.getType()).isEqualTo(Result.ResultType.IN_FLIGHT);

        //failure
        exportLeadsToSkipTraceResult = (ExportLeadsToSkipTraceResult) testObserver
                .getEvents().get(0).get(1);
        assertThat(exportLeadsToSkipTraceResult.getType()).isEqualTo(Result.ResultType.FAILURE);
        assertThat(exportLeadsToSkipTraceResult.getNumberOfLeadsExported()).isEqualTo(ZERO);
        assertThat(exportLeadsToSkipTraceResult.getErrorMessage()).isEqualToIgnoringCase(ERROR_MSG);
        assertThat(exportLeadsToSkipTraceResult.isFileLoadError()).isFalse();
    }
}