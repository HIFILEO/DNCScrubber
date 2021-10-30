# Hibernate

## Summary
The purpose of this read-me is to document decisions being made around 
hibernate and the database that it interacts with.

## Separation of Business Logic
Database objects in hibernate are tied to Sessions and their annotations 
make them difficult to mask with interfaces in the business logic. So the
decision was to have Business objects that are specific to our needs
regardless of the DAO. This wasn't the same decision like OpenCSV which
i would mask with interfaces. 

## Choice of Database
Hibernate is a great ORM and allows a multitude of supporting databases. 
Since this program is a simple filtering program there was no concern about 
scaling to production. Therefore, H2 was chosen. It allows for a embedded database 
without the need for spinning up something on the local host. That
simplicity and the h2 console ability to see the database lead me to making that
choice.

## Acid vs Base
Since CSV files are being used for the bulk of the data insertion, they have a
well structured schema. There is also no need for horizontal scaling since this is a 
standalone app. The data integrity was important. 

## Problems Dictate Architecture 
There are a number of problems with hibernate and h2 which lead to a good number 
of architectural work-arounds. I will document them here.

### *All Caps In Database*
When using hibernate to create tables in h2, I noticed all the table names and column
names were in caps. Even with the specification of *name* in the annotation the name was 
call caps. The fix to this was `DATABASE_TO_UPPER=false`

+ Although hibernate has [naming strategy](https://thorben-janssen.com/naming-strategies-in-hibernate-5/)
, `hibernate.implicit_naming_strategy` did nothing to change the cap problem. 
I wanted camel case in most cases and snake where required. 

+ [Another possible work](https://huongdanjava.com/ignore-case-sensitive-for-table-name-in-jpa-with-hibernate-implementation.html)
  around that require you to override code or
extend strategies (that would take too long)
  
+ Solution, although not well documented or supported was 
[HERE](https://stackoverflow.com/questions/33606800/how-to-disable-h2s-database-to-upper-in-spring-boot-without-explicit-connectio)


### *Problem With Auto Create*
The auto creation of the database via the ORM created a few problems.

+ Names were in alphabetical order and was a documented issue of hibernante. 
  I wanted the column names to be the same as the data model because the more importannt
  information of the table was on the left (like name and address, don't care about MLS #)
  - LInk?
+ `hibernate.hbm2ddl.import_files` only worked for when `hbm2ddl.auto`
was set to *create* or *create-drop*. This lead to a data wipe upon every launch.
  + [Documentation here](https://walkingtechie.blogspot.com/2018/12/execute-schema-and-data-sql-on-startup-spring-boot.html)
  + [Limitations about imported sql file setup](https://stackoverflow.com/questions/673802/how-to-import-initial-data-to-database-with-hibernate)
    , read comment by Jomar
    
#### Solution 
The solution came from running a script at startup that would create the tables for me.
I was also able to put the entries in a readable format unlike the hibernate import xml
scheme.

+ [Solution](https://stackoverflow.com/questions/4490138/problem-with-init-runscript-and-relative-paths) = `INIT=RUNSCRIPT FROM 'classpath:scripts/create.sql'`

#### Solution Drawback
The drawback came with constraints on the table. The SQL code was getting complicated
and it was easier to let hiberante do it. So we create the table names with the script
but we add contraints to the database usig hibernate `update`. This also checks validation.
    
