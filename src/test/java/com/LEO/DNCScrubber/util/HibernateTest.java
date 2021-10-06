package com.LEO.DNCScrubber.util;/*
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

import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import com.LEO.DNCScrubber.rx.RxJavaTest;
import org.assertj.core.util.VisibleForTesting;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Common Hibernate testing params in this class. Extend when testing hibernate work.
 */
public abstract class HibernateTest extends RxJavaTest {
    @VisibleForTesting
    protected static HibernateUtil hibernateUtil;

    @VisibleForTesting
    protected Session session;

    @BeforeAll
    public static void setup() {
        hibernateUtil = new HibernateUtil("hibernate-test.cfg.xml");
    }

    @AfterAll
    public static void tearDown() {
        hibernateUtil.shutdown();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();

        //Truncate All Tables For Each Test
        Session localSession = hibernateUtil.getSessionFactory().openSession();

        try {
            //Required - https://stackoverflow.com/questions/25821579/transactionrequiredexception-executing-an-update-delete-query
            Transaction transaction = localSession.beginTransaction();

            // Find all involved tables (ClassMetadata & CollectionMetadata)
            // - https://www.codejava.net/testing/junit-5-tutorial-for-beginner-test-crud-for-hibernateZ
            // Note - 5+ requirements
            //  - https://stackoverflow.com/questions/2007073/getting-all-mapped-entities-from-entitymanager
            //  - https://stackoverflow.com/questions/2007073/getting-all-mapped-entities-from-entitymanager
            MetamodelImplementor metamodelImplementor = (MetamodelImplementor) localSession.getMetamodel();
            Map<String, EntityPersister> entityPersisterMap = metamodelImplementor.entityPersisters();
            Collection<EntityPersister> entityPersisterCollection = entityPersisterMap.values();
            for (final EntityPersister entityPersister : entityPersisterCollection) {
                final String tableName = ((AbstractEntityPersister) entityPersister).getTableName();
                if (tableName != null) {

                    //Note - need to disable the key constraings
                    //  - https://www.codegrepper.com/code-examples/sql/mysql+truncate+table+with+foreign+keys
                    //  -https://stackoverflow.com/questions/253849/cannot-truncate-table-because-it-is-being-referenced-by-a-foreign-key-constraint
                    localSession.createSQLQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
                    localSession.createSQLQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                    localSession.createSQLQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                }
            }

            Map<String, CollectionPersister> collectionPersisterMap = metamodelImplementor.collectionPersisters();
            Collection<CollectionPersister> collectionPersistersCollection = collectionPersisterMap.values();
            for (final CollectionPersister collectionPersister : collectionPersistersCollection) {
                final String tableName = ((AbstractCollectionPersister) collectionPersister).getTableName();
                if (tableName != null) {
                    localSession.createSQLQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
                    localSession.createSQLQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                    localSession.createSQLQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
                }
            }

            transaction.commit();
        } finally {
            localSession.close();
        }

        //Get session for all the tests
        this.session = hibernateUtil.getSessionFactory().openSession();
    }

    @AfterEach
    public void tearDownEach() {
        if (session != null) session.close();
    }
}
