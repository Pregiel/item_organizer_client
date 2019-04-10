package item_organizer_client.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Arrays;
import java.util.List;

import static item_organizer_client.database.ItemOrganizerDatabase.configureSessionFactory;
import static item_organizer_client.database.ItemOrganizerDatabase.getSessionFactory;

@org.springframework.stereotype.Repository
public class Repository<T> {
    public T add(T object) {
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

    public List<T> addAll(T... objects) {
        for (T object : objects) {
            add(object);
        }
        return Arrays.asList(objects);
    }

    public T findById(Class<T> tClass, int id) {
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

    public List<T> findBy(Class<T> tClass, String fieldName, String fieldValue) {
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

    public List<T> findByQuery(String query, QueryParameter... parameters) {
        configureSessionFactory();

        List<T> list = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            Query result = session.createQuery(query);
            for (QueryParameter parameter : parameters) {
                result.setParameter(parameter.getKey(), parameter.getValue());
            }

            list = result.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return list;
    }

    public List<T> getAll(Class<T> tClass) {
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

    public <U> List<U> getAllColumn(Class<T> tClass, String column) {
        configureSessionFactory();

        List<U> list = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();

            list = session.createQuery("SELECT " + column + " FROM " + tClass.getName()).list();
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
