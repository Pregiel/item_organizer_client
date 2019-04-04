package item_organizer_client.database.repository;

import item_organizer_client.model.Item;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import static item_organizer_client.database.ItemOrganizerDatabase.*;

public class ItemRepository {

    public static void add(Item item) {
        configureSessionFactory();

        Transaction transaction = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.save(item);
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
    }

    public static Item findById(int id) {
        configureSessionFactory();

        Item item = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();

            item = session.get(Item.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return item;
    }

    public static List<Item> findByName(String name) {
        configureSessionFactory();

        List<Item> itemList = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            Query query = session.createQuery("FROM Item i WHERE i.name = :item_name");
            query.setParameter("item_name", name);

            itemList = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return itemList;
    }

    public static List<Item> getAll() {
        configureSessionFactory();

        List<Item> itemList = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();

            itemList = session.createQuery("FROM Item").list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return itemList;
    }
}
