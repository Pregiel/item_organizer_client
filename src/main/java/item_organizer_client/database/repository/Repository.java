package item_organizer_client.database.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import static item_organizer_client.database.ItemOrganizerDatabase.configureSessionFactory;
import static item_organizer_client.database.ItemOrganizerDatabase.getSessionFactory;

abstract class Repository {
    static <T> T add(T object) {
        configureSessionFactory();

        Transaction transaction = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.save(object);
            session.flush();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return object;
    }

    static <T> T findById(Class<T> tClass, int id) {
        configureSessionFactory();

        T object = null;
        Session session = null;

        try {
            session = getSessionFactory().openSession();

            object = session.get(tClass, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return object;
    }

    static <T> List<T> findBy(Class<T> tClass, String fieldName, String fieldValue) {
        configureSessionFactory();

        List<T> list = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            Query query = session.createQuery("FROM " + tClass.getName() + " i WHERE i." + fieldName + " = :value");
            query.setParameter("value", fieldValue);

            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return list;
    }

    static <T> List<T> getAll(Class<T> tClass) {
        configureSessionFactory();

        List<T> list = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();

            list = session.createQuery("FROM " + tClass.getName()).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return list;
    }


}
