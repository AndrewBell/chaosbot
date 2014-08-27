package com.recursivechaos.chaosbot.hibernate.dao;

/**
 * DAO
 * Handles most of the generic hibernate calls
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class DAO {
	private static final Logger log = Logger.getAnonymousLogger();
	private static final ThreadLocal<Session> session = new ThreadLocal<Session>();
	
	private static SessionFactory sessionFactory;

	/**
	 * Fetches a session factory, creating one if needed
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // loads configuration and mappings
            Configuration configuration = new Configuration().configure();
            ServiceRegistry serviceRegistry
                = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
             
            // builds a session factory from the service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);           
        }
         
        return sessionFactory;
    }
	

	/**
	 * closes current hibernate session
	 */
	protected static void close() {
		getSession().close();
		DAO.session.set(null);
	}

	/**
	 * Returns the active session, or creates one.
	 * 
	 * @return session from sessionFactory
	 */
	protected static Session getSession() {
		Session session = (Session) DAO.session.get();
		if (session == null) {
			if (sessionFactory == null) {
				sessionFactory = getSessionFactory();
			}
			session = sessionFactory.openSession();
			DAO.session.set(session);
		}
		return session;
	}

	protected DAO() {
	}

	/**
	 * Begins hibernate transaction
	 */
	protected void begin() {
		getSession().beginTransaction();
	}

	/**
	 * Commits hibernate transaction
	 */
	protected void commit() {
		getSession().getTransaction().commit();
	}

	/**
	 * commits, and closes transaction, canceling if caught error
	 */
	protected void rollback() {
		try {
			getSession().getTransaction().commit();
		} catch (HibernateException e) {
			log.log(Level.WARNING, "Cannot rollback", e);
		}

		try {
			getSession().close();
		} catch (HibernateException e) {
			log.log(Level.WARNING, "Cannot close", e);
		}
		DAO.session.set(null);
	}
}
