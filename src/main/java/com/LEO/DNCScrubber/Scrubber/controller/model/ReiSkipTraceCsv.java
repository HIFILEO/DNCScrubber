package com.LEO.DNCScrubber.Scrubber.controller.model;/*
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

import com.LEO.DNCScrubber.Scrubber.controller.ConvertReiSkipToBoolean;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

/**
 * Implementation for ReiSkipTrace that is created using CSV data ingestion from openCSV.
 */
public class ReiSkipTraceCsv {
    public static final String FIRST_NAME = "First Name";
    public static final String LAST_NAME = "Last Name";
    public static final String STREET_ADDRESS = "Street Address";
    public static final String CITY = "City";
    public static final String STATE = "State";
    public static final String ZIP = "Zip";
    public static final String MAILING_STREET_ADDRESS = "Mailing Street Address";
    public static final String MAILING_CITY = "Mailing City";
    public static final String MAILING_STATE = "Mailing State";
    public static final String MAILING_ZIP_CODE = "Mailing Zip Code";
    public static final String PHONE_1 = "Phone 1";
    public static final String PHONE_1_TYPE = "Phone 1 Type";
    public static final String PHONE_1_DNC = "Phone 1 DNC";
    public static final String PHONE_1_LITIGATOR = "Phone 1 Litigator";
    public static final String PHONE_1_TELCO_NAME = "Phone 1 Telco Name";
    public static final String PHONE_2 = "Phone 2";
    public static final String PHONE_2_TYPE = "Phone 2 Type";
    public static final String PHONE_2_DNC = "Phone 2 DNC";
    public static final String PHONE_2_LITIGATOR = "Phone 2 Litigator";
    public static final String PHONE_2_TELCO_NAME = "Phone 2 Telco Name";
    public static final String PHONE_3 = "Phone 3";
    public static final String PHONE_3_TYPE = "Phone 3 Type";
    public static final String PHONE_3_DNC = "Phone 3 DNC";
    public static final String PHONE_3_LITIGATOR = "Phone 3 Litigator";
    public static final String PHONE_3_TELCO_NAME = "Phone 3 Telco Name";
    public static final String EMAIL_1 = "Email 1";
    public static final String EMAIL_2 = "Email 2";
    public static final String EMAIL_3 = "Email 3";

    //this is used for CSV columns ordering on exporting LocalBusinessTrainingPairs
    public static final String[] FIELDS_ORDER = {FIRST_NAME, LAST_NAME, STREET_ADDRESS, CITY, STATE, ZIP,
            MAILING_STREET_ADDRESS, MAILING_CITY, MAILING_STATE, MAILING_ZIP_CODE,
            PHONE_1, PHONE_1_TYPE, PHONE_1_DNC, PHONE_1_LITIGATOR, PHONE_1_TELCO_NAME,
            PHONE_2, PHONE_2_TYPE, PHONE_2_DNC, PHONE_2_LITIGATOR, PHONE_2_TELCO_NAME,
            PHONE_3, PHONE_3_TYPE, PHONE_3_DNC, PHONE_3_LITIGATOR, PHONE_3_TELCO_NAME,
            EMAIL_1, EMAIL_2, EMAIL_3
    };

    @CsvBindByName(column = FIRST_NAME, required = true)
    String firstName;

    @CsvBindByName(column = LAST_NAME, required = true)
    String lastName;

    /*
    Note - the property we want to buy
     */
    @CsvBindByName(column = STREET_ADDRESS, required = true)
    String propertyAddress;

    @CsvBindByName(column = CITY, required = true)
    String propertyCity;

    @CsvBindByName(column = STATE, required = true)
    String propertyState;

    @CsvBindByName(column =  ZIP)
    String propertyZip;

    /*
    Note - Where the person lives
     */
    @CsvBindByName(column = MAILING_STREET_ADDRESS, required = true)
    String mailingAddress;

    @CsvBindByName(column = MAILING_CITY, required = true)
    String mailingCity;

    @CsvBindByName(column = MAILING_STATE, required = true)
    String mailingState;

    @CsvBindByName(column = MAILING_ZIP_CODE)
    String mailingZip;

    @CsvBindByName(column = PHONE_1)
    String phone1;

    @CsvBindByName(column = PHONE_1_TYPE)
    String phone1Type;

    @CsvCustomBindByName(column = PHONE_1_DNC, converter = ConvertReiSkipToBoolean.class)
    boolean phone1Dnc;

    @CsvCustomBindByName(column = PHONE_1_LITIGATOR, converter = ConvertReiSkipToBoolean.class)
    boolean phone1Litigator;

    @CsvBindByName(column = PHONE_1_TELCO_NAME)
    String phone1TelcoName;

    @CsvBindByName(column = PHONE_2)
    String phone2;

    @CsvBindByName(column = PHONE_2_TYPE)
    String phone2Type;

    @CsvCustomBindByName(column = PHONE_2_DNC, converter = ConvertReiSkipToBoolean.class)
    boolean phone2Dnc;

    @CsvCustomBindByName(column = PHONE_2_LITIGATOR, converter = ConvertReiSkipToBoolean.class)
    boolean phone2Litigator;

    @CsvBindByName(column = PHONE_2_TELCO_NAME)
    String phone2TelcoName;

    @CsvBindByName(column = PHONE_3)
    String phone3;

    @CsvBindByName(column = PHONE_3_TYPE)
    String phone3Type;

    @CsvCustomBindByName(column = PHONE_3_DNC, converter = ConvertReiSkipToBoolean.class)
    boolean phone3Dnc;

    @CsvCustomBindByName(column = PHONE_3_LITIGATOR, converter = ConvertReiSkipToBoolean.class)
    boolean phone3Litigator;

    @CsvBindByName(column = PHONE_3_TELCO_NAME)
    String phone3TelcoName;

    @CsvBindByName(column = EMAIL_1)
    String email1;

    @CsvBindByName(column = EMAIL_2)
    String email2;

    @CsvBindByName(column = EMAIL_3)
    String email3;

    /*
    Let Here for future enhancements - like APNs might differ.
     */
//    int fipsCode;
//    boolean vacant;
//    boolean absentee;
//    String occupancy;
//    String ownershipType;
//    String FormattedApn;
//    int censusTract;
//    int township;
//    int section;
//    int tractNumber;
//    String primaryOwnerFirst;
//    String primaryOwnerMiddle;
//    String primaryOwnerLast;
//    String secondaryOwnerFirst;
//    String secondaryOwnerMiddle;
//    String secondaryOwnerLast;
//    int mortgageAmount;
//    Date mortgageDate;
//    String mortgageLoanTypeCode;
//    String mortgageTermCode;
//    String lenderName;
//    int yearAssessed;
//    int assessedValueTotal;
//    int assessedValueImprovements;
//    int assessedValueLand;
//    int marketValueTotal;
//    int marketValueImprovements;
//    int marketValueLand;
//    int taxFiscalYear;
//    int taxBilledAmount;
//    int yearBuilt;
//    String lastSaleType;
//    String prevSaleType;
//    String sellerName;
//    Date assessorLastSaleDate;
//    String assessorLastSaleAmount;
//    Date assessorPriorSaleDate;
//    int assessorPriorSaleAmount;
//    int livingSqFt;
//    int areaLotAcres;
//    int areaLotSf;
//    int basementArea;
//    String parkingGarage;
//    int bathCount;
//    int bathPartialCount;
//    int bedroomsCount;
//    int roomsCount;
//    int storiesCount;
//    String sewer;
//    int estimatedValue;
//    int estimatedMinValue;
//    int estimatedMaxValue;

    /**
     * Needed by OpenCSV
     */
    public ReiSkipTraceCsv() {

    }

    /**
     * Create the object - used for saving to CSV file
     * @param firstName -
     * @param lastName        -
     * @param propertyAddress -
     * @param propertyCity    -
     * @param propertyState   -
     * @param propertyZip     -
     * @param mailingAddress  -
     * @param mailingCity     -
     * @param mailingState    -
     * @param mailingZip      -
     * @param phone1          -
     * @param phone1Type      -
     * @param phone1Dnc       -
     * @param phone1Litigator -
     * @param phone1TelcoName -
     * @param phone2          -
     * @param phone2Type      -
     * @param phone2Dnc       -
     * @param phone2Litigator -
     * @param phone2TelcoName -
     * @param phone3          -
     * @param phone3Type      -
     * @param phone3Dnc       -
     * @param phone3Litigator -
     * @param phone3TelcoName -
     * @param email1          -
     * @param email2          -
     * @param email3          -
     */
    public ReiSkipTraceCsv(String firstName, String lastName, String propertyAddress, String propertyCity,
                           String propertyState, String propertyZip, String mailingAddress, String mailingCity,
                           String mailingState, String mailingZip, String phone1, String phone1Type, boolean phone1Dnc,
                           boolean phone1Litigator, String phone1TelcoName, String phone2, String phone2Type,
                           boolean phone2Dnc, boolean phone2Litigator, String phone2TelcoName, String phone3,
                           String phone3Type, boolean phone3Dnc, boolean phone3Litigator, String phone3TelcoName,
                           String email1, String email2, String email3) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.propertyAddress = propertyAddress;
        this.propertyCity = propertyCity;
        this.propertyState = propertyState;
        this.propertyZip = propertyZip;
        this.mailingAddress = mailingAddress;
        this.mailingCity = mailingCity;
        this.mailingState = mailingState;
        this.mailingZip = mailingZip;
        this.phone1 = phone1;
        this.phone1Type = phone1Type;
        this.phone1Dnc = phone1Dnc;
        this.phone1Litigator = phone1Litigator;
        this.phone1TelcoName = phone1TelcoName;
        this.phone2 = phone2;
        this.phone2Type = phone2Type;
        this.phone2Dnc = phone2Dnc;
        this.phone2Litigator = phone2Litigator;
        this.phone2TelcoName = phone2TelcoName;
        this.phone3 = phone3;
        this.phone3Type = phone3Type;
        this.phone3Dnc = phone3Dnc;
        this.phone3Litigator = phone3Litigator;
        this.phone3TelcoName = phone3TelcoName;
        this.email1 = email1;
        this.email2 = email2;
        this.email3 = email3;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public String getPropertyCity() {
        return propertyCity;
    }

    public String getPropertyState() {
        return propertyState;
    }

    public String getPropertyZip() {
        return propertyZip;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public String getMailingState() {
        return mailingState;
    }

    public String getMailingZip() {
        return mailingZip;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone1Type() {
        return phone1Type;
    }

    public boolean isPhone1Dnc() {
        return phone1Dnc;
    }

    public boolean isPhone1Litigator() {
        return phone1Litigator;
    }

    public String getPhone1TelcoName() {
        return phone1TelcoName;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone2Type() {
        return phone2Type;
    }

    public boolean isPhone2Dnc() {
        return phone2Dnc;
    }

    public boolean isPhone2Litigator() {
        return phone2Litigator;
    }

    public String getPhone2TelcoName() {
        return phone2TelcoName;
    }

    public String getPhone3() {
        return phone3;
    }

    public String getPhone3Type() {
        return phone3Type;
    }

    public boolean isPhone3Dnc() {
        return phone3Dnc;
    }

    public boolean isPhone3Litigator() {
        return phone3Litigator;
    }

    public String getPhone3TelcoName() {
        return phone3TelcoName;
    }

    public String getEmail1() {
        return email1;
    }

    public String getEmail2() {
        return email2;
    }

    public String getEmail3() {
        return email3;
    }
}
