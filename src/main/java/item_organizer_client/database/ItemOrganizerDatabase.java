package item_organizer_client.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
