package com.LEO.DNCScrubber.Scrubber.gateway;/*
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
import com.LEO.DNCScrubber.Scrubber.gateway.model.ColdRvmLeadDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDao;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PersonDao_;
import com.LEO.DNCScrubber.Scrubber.gateway.model.PropertyDao;
import com.LEO.DNCScrubber.Scrubber.model.data.*;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

/**
 * {@link DatabaseGateway} implementation using Hibernate. https://hibernate.org/
 */
public class DatabaseGatewayImpl implements DatabaseGateway{
    final static Logger logger = LoggerFactory.getLogger(DatabaseGatewayImpl.class);
    private final HibernateUtil hibernateUtil;
    private final DatabaseHelper databaseHelper;

    /**
     * Constructor
     * @param hibernateUtil - for DB interaction
     */
    public DatabaseGatewayImpl(HibernateUtil hibernateUtil, DatabaseHelper databaseHelper) {
        this.hibernateUtil = hibernateUtil;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public Observable<Boolean> writeColdRvmLead(ColdRvmLead coldRvmLead) {

        Transaction transaction = null;
        try (Session session = hibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();

            /*
            Note - primary key = auto generated ID. The CSV data has no primary ID and to use "String"
            as a key is bad practice since it's almost always non-unique.

            So we are using a 'naturalID' that is an association of unique fields i the data.

            Furthermore, hibernate does not do an "update" if exists, "insert" otherwise. It's not that smart.
            Instead it always 'inserts' if the primary key is null. It's up to you to first look for the id in
            the session. Even saveOrUpdate() does not work because the cascade inserts will throw duplicate
            entry exceptions.

            So the rule of thumb is this: save from bottom up while searching for naturalKey every time.
             */
            databaseHelper.saveColdRvmLead(session, coldRvmLead);
            transaction.commit();
            logger.debug("Raw Lead Written To DB Successfully");
        } catch (Exception exception) {
            logger.error("Raw Lead Failed To Write To DB. Error - []", exception);
            if (transaction != null && transaction.isActive()) {
                //Note - why double try - https://mkyong.com/hibernate/hibernate-transaction-handle-example/
                try{
                    transaction.rollback();
                }catch(RuntimeException runtimeException){
                    logger.error("Raw Lead couldn't roll back transaction", runtimeException);
                }
            }

            return Observable.error(exception);
        }

        return Observable.just(true);
    }

    @Override
    public Observable<ColdRvmLead> loadColdRvmLeadByNaturalId(String naturalId) {
        Transaction transaction = null;
        ColdRvmLeadDao coldRvmLeadDao = null;
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            coldRvmLeadDao = session.byNaturalId(ColdRvmLeadDao.class)
                    .using("naturalId", naturalId)
                    .load();

        } catch (Exception exception){
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Load ColdRvmLeadDao threw an exception. Ignoring - return empty. Error - []", exception);
        }

        if (coldRvmLeadDao == null) {
            return Observable.empty();
        } else {
            return Observable.just(databaseHelper.translateColdRvmLeadDbToColdRvmLead(coldRvmLeadDao));
        }
    }

    @Override
    public Observable<Person> loadPersonByNaturalId(String naturalId) {
        Transaction transaction = null;
        PersonDao personDao = null;
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            personDao = session.byNaturalId(PersonDao.class)
                    .using("naturalId", naturalId)
                    .load();
        } catch (Exception exception){
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Load PersonDao threw an exception. Ignoring - return empty. Error - []", exception);
        }

        if (personDao == null) {
            return Observable.empty();
        } else {
            return Observable.just(databaseHelper.translatePersonDbToPerson(personDao));
        }
    }

    @Override
    public Observable<Property> loadPropertyByNaturalId(String naturalId) {
        Transaction transaction = null;
        PropertyDao propertyDao = null;
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            propertyDao = session.byNaturalId(PropertyDao.class)
                    .using("naturalId", naturalId)
                    .load();
        } catch (Exception exception){
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Load PropertyDoa threw an exception. Ignoring - return empty. Error - []", exception);
        }

        if (propertyDao == null) {
            return Observable.empty();
        } else {
            return Observable.just(databaseHelper.translatePropertyDbToProperty(propertyDao));
        }
    }

    /**
     * Load all {@link Person} who have no phone numbers.
     * @return a stream of {@link Person}
     */
    public Observable<Person> loadPersonsWithNoPhoneNumber() {
        return Observable.create((ObservableOnSubscribe<PersonDao>) emitter -> {
            Transaction transaction = null;

            try (Session session = hibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                //Setup the builder to query for a specific DAO
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<PersonDao> criteriaQuery = criteriaBuilder.createQuery(PersonDao.class);
                Root<PersonDao> rootPersonDao = criteriaQuery.from(PersonDao.class);

                //MetaModel - the ability to access the String value of an attribute using generated code so you can
                //simplify your SQL queries. Awesome.
                //https://www.baeldung.com/hibernate-criteria-queries-metamodel
                //https://stackoverflow.com/questions/14422543/hibernate-criteria-filter-by-related-entity

                //Setup the WHERE clauses - where phone1 is empty AND phone 2 is empty AND phone 3 is empty
                Predicate[] predicates = new Predicate[3];

                predicates[0] = criteriaBuilder.or(
                        criteriaBuilder.isNull(rootPersonDao.get(PersonDao_.phone1)),
                        criteriaBuilder.like(rootPersonDao.get(PersonDao_.phone1), ""));

                predicates[1] = criteriaBuilder.or(
                        criteriaBuilder.isNull(rootPersonDao.get(PersonDao_.phone2)),
                        criteriaBuilder.like(rootPersonDao.get(PersonDao_.phone2), ""));

                predicates[2] = criteriaBuilder.or(
                        criteriaBuilder.isNull(rootPersonDao.get(PersonDao_.phone3)),
                        criteriaBuilder.like(rootPersonDao.get(PersonDao_.phone3), ""));

                criteriaQuery.select(rootPersonDao).where(criteriaBuilder.and(predicates));

                //Execute Query
                //Note - You want to batch your queries so you get all results when database is large
                // - You can do this by hand, or use Java Streams (Not RX)
                // - How hibernate pagination works - https://www.baeldung.com/hibernate-pagination
                // - How stream() works under the hood for ScrollableResult pagination -
                //      https://thorben-janssen.com/get-query-results-stream-hibernate-5/
                Query<PersonDao> query = session.createQuery(criteriaQuery);

                //Note - fetch size is a hint to the JDBC on how many rows to return every round trip.
                // - If not set, it's defaulted to the database itself.
                // - I decided to set it to 100
                //
                //Ex - Oracle 10, Db2 - 32
                //https://stackoverflow.com/questions/3355231/whats-the-default-size-of-hibernate-jdbc-fetch-size
                //Read - https://jeffreyjacobs.wordpress.com/2017/05/28/setting-jdbc-fetch-size-in-hibernate/
                query.setFetchSize(10);

                Stream<PersonDao> personDaoStream = query.stream();

                //switch from java streams to RX streams
                personDaoStream.forEach(emitter::onNext);
                personDaoStream.close();

            } catch (Exception exception){
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Load PropertyDoa threw an exception. Ignoring - return empty. Error - []", exception);
            }

            emitter.onComplete();
        }).flatMap(new Function<PersonDao, ObservableSource<Person>>() {
            @Override
            public ObservableSource<Person> apply(PersonDao personDao) throws Exception {
                return Observable.just(databaseHelper.translatePersonDbToPerson(personDao));
            }
        });
    }
}
