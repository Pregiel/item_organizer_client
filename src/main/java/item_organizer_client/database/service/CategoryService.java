package item_organizer_client.database.service;

import item_organizer_client.model.Category;

import java.util.List;

public interface CategoryService {
    Category add(Category category);

    Category findById(int id);

    Category findOrAdd(Category category);

    List<Category> findByName(String name);

    List<Category> getAll();
}
