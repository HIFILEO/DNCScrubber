# DNCScrubber

For h2 console you'll need to install homebrew. Then h2.
http://macappstore.org/h2/

Type h2-console after installation to run the console in the browser.

Type in the path to the h2 database file and connect with username and password that is in hibernate configs.

You cannot connect to the DB from console and APP at the same time. 

# Requirements

### Load raw leads ###
* Load the CSV file and dump the into the person and property table
* Create a cold RVM entry that’s pretty much blank
* Use both owners
* Person points to property since that’s how REIBB does it. Two different people can own the same property but when inside REIBB you can’nt link one property to two people.
* Person to property is one to many
* Can be a new Cold RVM lead but the person is already in Database.
  * Raw lead files cannot overwrite property or person in Database.

### Export leads to skip trace 
* Export all the people that don’t have phoen numbers or emails
* Export a CSV file for person + property

### Import REI Skip leads
* Update persons table

###  Import DNC list
* Update / insert into DNC Table.
* Update the Person’s DNC field for that phone number

### Export Cold RVM Master List
* Export the cold RVM table

### Export Cold RVM For REIBB
* Export the cold RVM table but only those we did not contact yet

### Load Cold RVM master list
* Update the Cold RVM table only
* Version 2
  * Insert/Update the person and property for any changes we make.
  * Ie - we talked to karen in manchester but originally her nane and number were wrong.


## Person Table
* First Name
* Last Name
* Mailing Address
* Mailing City
* Mailing State
* Mailing Zip
* Mailing County
* Phone 1
* Phone 1 Type
* Phone 1 DNC
* Phone 1 Stop
* Phone 1 Litigation
* Phone 1 Telco
* Phone 2
* Phone 2 Type
* Phone 2 DNC
* Phone 2 Stop
* Phone 2 Litigation
* Phone 2 Telco
* Phone 3
* Phone 3 Type
* Phone 3 DNC
* Phone 3 Stop
* Phone 3 Litigation
* Phone 3 Telco
* Email 1
* Email 2
* Email 3
* Property Link Table

## Property
* Address
* Unit #
* City
* State
* Zip
* County
* APN
* Owner Occupied
* Company Name
* Company Address
* Property Type
* Bedrooms
* Total Bathrooms
* Building Sqft
* Lot Size Sqft
* Effective Year Built
* Total Assed Value
* Last Sale Recoring Date
* Last Sale Amount
* Total Open Loans
* Est. Remaining Balance
* Est. Value
* Est. Loan - To - Value
* Est. Equity
* MLS Status
* MLS Date
* MLS Amount
* Lien Amount
* Date Added To List


## Cold RVM Lead Table
* Date Workflow Started
* Conversation Started
* Told To Stop
* Sold
* Wrong Number
* Was An Offer Made
* Person Link
* Property Link