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
import com.LEO.DNCScrubber.Scrubber.model.data.RawLead;
import com.LEO.DNCScrubber.core.hibernate.HibernateUtil;
import io.reactivex.Observable;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DatabaseGateway} implementation using Hibernate. https://hibernate.org/
 */
public class DatabaseGatewayImpl implements DatabaseGateway{
    final static Logger logger = LoggerFactory.getLogger(DatabaseGatewayImpl.class);
    private final HibernateUtil hibernateUtil;

    /**
     * Constructor
     * @param hibernateUtil - for DB interaction
     */
    public DatabaseGatewayImpl(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public Observable<Boolean> writeRawLead(RawLead rawLead) {
        return Observable.create(emitter -> {
            Transaction transaction = null;
            try (Session session = hibernateUtil.getSessionFactory().openSession()) {
                // start a transaction
                transaction = session.beginTransaction();
                // save the student objects
                session.save(rawLead);
                // commit transaction
                transaction.commit();
                emitter.onNext(true);
                logger.debug("Raw Lead Written To DB Successfully");
            } catch (Exception exception) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Raw Lead Failed To Write To DB. Error - []", exception);
                emitter.onError(exception);
            }
        });
    }
}
