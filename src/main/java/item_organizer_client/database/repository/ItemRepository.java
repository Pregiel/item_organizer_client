package item_organizer_client.database.repository;

import item_organizer_client.database.Repository;
import item_organizer_client.model.Item;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import static item_organizer_client.database.ItemOrganizerDatabase.configureSessionFactory;
import static item_organizer_client.database.ItemOrganizerDatabase.getSessionFactory;

@org.springframework.stereotype.Repository
public class ItemRepository extends Repository<Item> {

    public int updateId(Item item, Integer newId) {
        int result = 0;
        configureSessionFactory();

        Transaction transaction = null;
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("UPDATE Item SET id = :newId WHERE id = :oldId");
            query.setParameter("oldId", item.getNumber());
            query.setParameter("newId", newId);

            result = query.executeUpdate();

            if (result < 1) {
                throw new Exception();
            }

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
        return result;
    }
}
