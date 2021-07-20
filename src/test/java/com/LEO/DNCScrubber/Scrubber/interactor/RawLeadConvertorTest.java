package com.LEO.DNCScrubber.Scrubber.interactor;

import com.LEO.DNCScrubber.Scrubber.controller.RawLeadCsvImpl;
import com.LEO.DNCScrubber.Scrubber.controller.RawLedVerifier;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
class RawLeadConvertorTest extends RxJavaTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        initMocks(this);
    }

    public String convertDateToString(Date date, String datePattern) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        return dateFormat.format(date);
    }

    @Test
    public void convertRawLeadToColdRvmLead_success() throws Exception {
        //
        //Arrange
        //
        RawLeadConvertor rawLeadConvertor = new RawLeadConvertor();

        //Load the raw leads - just because i'm lazy
        File file = new File("build/resources/test/test-data - Properties.csv");
        CsvToBeanBuilder cvsToBeanBuilder = new CsvToBeanBuilder(new FileReader(file));
        cvsToBeanBuilder.withVerifier(new RawLedVerifier());
        CsvToBean csvToBean = cvsToBeanBuilder.withType(RawLeadCsvImpl.class).build();
        List<RawLeadCsvImpl> rawLeadCsvList = csvToBean.parse();

        //
        //Act
        //
        ColdRvmLead coldRvmLead = rawLeadConvertor.convertRawLeadToColdRvmLead(rawLeadCsvList.get(0));

        //
        //Assert
        //
        assertThat(rawLeadCsvList.size()).isEqualTo(9);

        //ColdRvmLead
        assertThat(coldRvmLead.getDateWorkflowStarted()).isNotNull();
        assertThat(this.convertDateToString(coldRvmLead.getDateWorkflowStarted(), "MM/dd/yyyy"))
                .isEqualToIgnoringCase("04/23/2021");
        assertThat(coldRvmLead.isConversationStarted()).isFalse();
        assertThat(coldRvmLead.isToldToStop()).isFalse();
        assertThat(coldRvmLead.isSold()).isFalse();
        assertThat(coldRvmLead.isWrongNumber()).isFalse();
        assertThat(coldRvmLead.isOfferMade()).isFalse();
        assertThat(coldRvmLead.isLeadSentToAgent()).isFalse();

        //Person
        assertThat(coldRvmLead.getPerson()).isNotNull();
        assertThat(coldRvmLead.getPerson().getFirstName()).isEqualToIgnoringCase("BRAD N. ");
        assertThat(coldRvmLead.getPerson().getLastName()).isEqualToIgnoringCase("MALICKI, ESQ");

        //Person Address -
        assertThat(coldRvmLead.getPerson().getAddress().getMailingAddress()).isEqualToIgnoringCase("107 Hamilton St");
        assertThat(coldRvmLead.getPerson().getAddress().getUnitNumber()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getPerson().getAddress().getCity()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getPerson().getAddress().getState()).isEqualToIgnoringCase("CT");
        assertThat(coldRvmLead.getPerson().getAddress().getZip()).isEqualToIgnoringCase("06106");
        assertThat(coldRvmLead.getPerson().getAddress().getCounty()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getPerson().getAddress().getCountry()).isEqualToIgnoringCase("US");

        //Person Phone
        assertThat(coldRvmLead.getPerson().getPhone1()).isNull();
        assertThat(coldRvmLead.getPerson().getPhone2()).isNull();
        assertThat(coldRvmLead.getPerson().getPhone3()).isNull();

//        assertThat(coldRvmLead.getPerson().getPhone1().getPhoneNumber()).isEqualToIgnoringCase("");
//        assertThat(coldRvmLead.getPerson().getPhone1().getPhoneType()).isEqualToIgnoringCase("");
//        assertThat(coldRvmLead.getPerson().getPhone1().isPhoneDNC()).isFalse();
//        assertThat(coldRvmLead.getPerson().getPhone1().isPhoneStop()).isFalse();
//        assertThat(coldRvmLead.getPerson().getPhone1().isPhoneLitigation()).isFalse();
//        assertThat(coldRvmLead.getPerson().getPhone1().getPhoneTelco()).isEqualToIgnoringCase("");

        //Person Email
        assertThat(coldRvmLead.getPerson().getEmail1()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getPerson().getEmail2()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getPerson().getEmail3()).isEqualToIgnoringCase("");

        //Person Property
        assertThat(coldRvmLead.getPerson().getPropertyList().size()).isEqualTo(1);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getMailingAddress()).isEqualToIgnoringCase("107 Hamilton St");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getUnitNumber()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getCity()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getState()).isEqualToIgnoringCase("CT");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getZip()).isEqualToIgnoringCase("06106");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getCounty()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getAddress()
                .getCountry()).isEqualToIgnoringCase("US");

        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getaPN()).isEqualToIgnoringCase("HTFD M:183 B:501 L:038");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .isOwnerOccupied()).isFalse();
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getCompanyName()).isEqualToIgnoringCase("107 Hamilton Llc");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getCompanyAddress()).isEqualToIgnoringCase("07 HAMILTON STREET, HARTFORD, CT, 06106");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getPropertyType()).isEqualToIgnoringCase("Triplex (3 units, any combination)");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getBedrooms()).isEqualToIgnoringCase("6.0");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getTotalBathrooms()).isEqualToIgnoringCase("3.0");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getSqft()).isEqualTo(3258);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getLotSizeSqft()).isEqualTo(8999);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getYearBuilt()).isEqualTo(1900);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getAssessedValue()).isEqualTo(62720);
        assertThat(this.convertDateToString(coldRvmLead.getPerson().getPropertyList().get(0)
                .getLastSaleRecordingDate(), "yyyy-MM-dd")).isEqualToIgnoringCase("2003-02-13");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getLastSaleAmount()).isEqualTo(37000);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getTotalOpenLoans()).isEqualTo(0);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getEstimatedRemainingBalance()).isEqualTo(0);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getEstimatedValue()).isEqualTo(151069);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getEstimatedEquity()).isEqualTo(151069);
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getmLSStatus()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0).getMlsDate()).isNull();
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getmLSAmount()).isEqualToIgnoringCase("0");
        assertThat(coldRvmLead.getPerson().getPropertyList().get(0)
                .getLienAmount()).isEqualToIgnoringCase("0");
        assertThat(this.convertDateToString(coldRvmLead.getPerson().getPropertyList().get(0)
                .getDateAddedToList(), "MM/dd/yyyy")).isEqualToIgnoringCase("04/23/2021");

        //Property
        assertThat(coldRvmLead.getProperty()).isNotNull();
        assertThat(coldRvmLead.getProperty().getAddress()
                .getMailingAddress()).isEqualToIgnoringCase("107 Hamilton St");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getUnitNumber()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getCity()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getState()).isEqualToIgnoringCase("CT");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getZip()).isEqualToIgnoringCase("06106");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getCounty()).isEqualToIgnoringCase("Hartford");
        assertThat(coldRvmLead.getProperty().getAddress()
                .getCountry()).isEqualToIgnoringCase("US");

        assertThat(coldRvmLead.getProperty()
                .getaPN()).isEqualToIgnoringCase("HTFD M:183 B:501 L:038");
        assertThat(coldRvmLead.getProperty()
                .isOwnerOccupied()).isFalse();
        assertThat(coldRvmLead.getProperty()
                .getCompanyName()).isEqualToIgnoringCase("107 Hamilton Llc");
        assertThat(coldRvmLead.getProperty()
                .getCompanyAddress()).isEqualToIgnoringCase("07 HAMILTON STREET, HARTFORD, CT, 06106");
        assertThat(coldRvmLead.getProperty()
                .getPropertyType()).isEqualToIgnoringCase("Triplex (3 units, any combination)");
        assertThat(coldRvmLead.getProperty()
                .getBedrooms()).isEqualToIgnoringCase("6.0");
        assertThat(coldRvmLead.getProperty()
                .getTotalBathrooms()).isEqualToIgnoringCase("3.0");
        assertThat(coldRvmLead.getProperty()
                .getSqft()).isEqualTo(3258);
        assertThat(coldRvmLead.getProperty()
                .getLotSizeSqft()).isEqualTo(8999);
        assertThat(coldRvmLead.getProperty()
                .getYearBuilt()).isEqualTo(1900);
        assertThat(coldRvmLead.getProperty()
                .getAssessedValue()).isEqualTo(62720);
        assertThat(this.convertDateToString(coldRvmLead.getProperty()
                .getLastSaleRecordingDate(), "yyyy-MM-dd")).isEqualToIgnoringCase("2003-02-13");
        assertThat(coldRvmLead.getProperty()
                .getLastSaleAmount()).isEqualTo(37000);
        assertThat(coldRvmLead.getProperty()
                .getTotalOpenLoans()).isEqualTo(0);
        assertThat(coldRvmLead.getProperty()
                .getEstimatedRemainingBalance()).isEqualTo(0);
        assertThat(coldRvmLead.getProperty()
                .getEstimatedValue()).isEqualTo(151069);
        assertThat(coldRvmLead.getProperty()
                .getEstimatedEquity()).isEqualTo(151069);
        assertThat(coldRvmLead.getProperty()
                .getmLSStatus()).isEqualToIgnoringCase("");
        assertThat(coldRvmLead.getProperty().getMlsDate()).isNull();
        assertThat(coldRvmLead.getProperty()
                .getmLSAmount()).isEqualToIgnoringCase("0");
        assertThat(coldRvmLead.getProperty()
                .getLienAmount()).isEqualToIgnoringCase("0");
        assertThat(this.convertDateToString(coldRvmLead.getProperty()
                .getDateAddedToList(), "MM/dd/yyyy")).isEqualToIgnoringCase("04/23/2021");

        //Test Natural Ids
        final String aPN = "HTFDM:183B:501L:038";
        final String county = "Hartford";
        assertThat(coldRvmLead.getProperty().getNaturalId()).isEqualToIgnoringCase(county + "~" + aPN);

        final String firstName = "BRADN.";
        final String lastName = "MALICKI,ESQ";
        final String address = "107HamiltonSt";
        assertThat(coldRvmLead.getPerson().getNaturalId()).isEqualToIgnoringCase(firstName +
                "~" +
                lastName +
                "~" +
                address);
    }


}