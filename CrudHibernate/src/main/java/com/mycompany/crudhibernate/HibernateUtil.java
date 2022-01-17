package com.mycompany.crudhibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author chris
 */
public class HibernateUtil {
    
    private static final SessionFactory sf;

    static {
        try {
            sf = new Configuration().configure().buildSessionFactory();
        } catch(Exception ex) {
            System.out.println("Error al crear SessionFactory");
            System.out.println(ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sf;
    }
    
    
}

