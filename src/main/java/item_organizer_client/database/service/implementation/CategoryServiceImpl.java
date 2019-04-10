package item_organizer_client.database.service.implementation;

import item_organizer_client.database.Repository;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private Repository<Category> categoryRepository;

    @Override
    public Category add(Category category) {
        return categoryRepository.add(category);
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findById(Category.class, id);
    }

    @Override
    public Category findOrAdd(Category category) {
        List<Category> foundedCategoryList = findByName(category.getName());
        if (foundedCategoryList.size() > 0) {
            return foundedCategoryList.get(0);
        }
        return add(category);
    }

    @Override
    public List<Category> findByName(String name) {
        return categoryRepository.findBy(Category.class, "name", name);
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.getAll(Category.class);
    }
}
