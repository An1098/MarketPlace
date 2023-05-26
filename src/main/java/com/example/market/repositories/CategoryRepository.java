package com.example.market.repositories;

import com.example.market.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    //метод возвращает категорию по её наименованию
    com.example.market.models.Category findByName(String name);
}
