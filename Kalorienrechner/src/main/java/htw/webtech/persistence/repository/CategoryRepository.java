package htw.webtech.persistence.repository;

import htw.webtech.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {}
