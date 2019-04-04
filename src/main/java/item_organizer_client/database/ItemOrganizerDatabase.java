package item_organizer_client.database;

import item_organizer_client.model.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ItemOrganizerDatabase {
    private static SessionFactory sessionFactory = null;

    public static void configureSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
