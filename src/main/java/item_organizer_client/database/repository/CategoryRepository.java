package item_organizer_client.database.repository;

import item_organizer_client.model.Category;

import java.util.List;


public class CategoryRepository extends Repository {
    public static Category add(Category category) {
        return Repository.add(category);
    }

    public static Category findById(int id) {
        return findById(Category.class, id);
    }

    public static Category findOrAdd(Category category) {
        List<Category> foundedCategoryList = findByName(category.getName());
        if (foundedCategoryList.size() > 0) {
            return foundedCategoryList.get(0);
        }
        return add(category);
    }

    public static List<Category> findByName(String name) {
        return findBy(Category.class, "name", name);
    }

    public static List<Category> getAll() {
        return getAll(Category.class);
    }
}
