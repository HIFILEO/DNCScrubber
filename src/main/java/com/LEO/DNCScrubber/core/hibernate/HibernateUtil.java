package com.LEO.DNCScrubber.core.hibernate;/*
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

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hibernate utility class. Should only be one per application instance.
 *
 * https://dzone.com/articles/hibernate-5-xml-configuration-example
 */
public class HibernateUtil  {
    final static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    private StandardServiceRegistry standardServiceRegistry;
    private SessionFactory sessionFactory;

    /**
     * Constructor. Create hibernate factories required to hook into database.
     */
    public HibernateUtil() {
        //createDB();

        try {
            // Create registry
            standardServiceRegistry = new StandardServiceRegistryBuilder().configure().build();

            // Create MetadataSources
            MetadataSources sources = new MetadataSources(standardServiceRegistry);

            // Create Metadata
            Metadata metadata = sources.getMetadataBuilder().build();

            // Create SessionFactory
            sessionFactory = metadata.getSessionFactoryBuilder().build();

        } catch (Exception e) {
            logger.error("Hibernate failed to initialize []", e);

            if (standardServiceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
            }
        }
    }

    /**
     * Get {@link SessionFactory}
     * @return
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Shutdown hibernate session.
     */
    public void shutdown() {
        if (standardServiceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
        }
    }

//    private void createDB() {
//        // Defines the JDBC URL. As you can see, we are not specifying
//        // the database name in the URL.
//        String url = "jdbc:mysql://127.0.0.1";
//
//        // Defines username and password to connect to database server.
//        String username = "root";
//        String password = "1982";
//
//        // SQL command to create a database in MySQL.
//        String sql = "CREATE DATABASE IF NOT EXISTS dncScrubberDB";
//
//        try (Connection conn = DriverManager.getConnection(url, username, password);
//             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
//            preparedStatement.execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
