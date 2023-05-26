package com.example.market.services;
import com.example.market.models.Category;
import com.example.market.models.Product;
import com.example.market.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    //внедряем через конструктор ProductRepository
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //метод позволит получить список всех товаров
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //метод позволит получить товар по id (он потребуется  для редактирования товара, удаления,и при нажатии на карточку товара для получения информации о товаре)
    public Product getProductId(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //метод позволяет сохранить товар
    //в параметры получаем продукт и категорию, далее к продукту привязываем категорию, и объект продукта сохраняем в БД
    @Transactional //так как меняет БД (уже не только чтение, а изменение БД)
    public void saveProduct(Product product, Category category){
        product.setCategory(category);
        productRepository.save(product);
    }

    //метод позволит редактировать данные о товаре и сохранять измененный товар
    //в параметрах получаем (с формы Изменения товара) объект
    // в теле, мы устанавливаем id продукту, SpringDataJpa понимает, что мы хотим обновить информацию о продукте, и сохраняем
    @Transactional//так как меняет БД (уже не только чтение, а изменение БД)
    public void updateProduct(int id, Product product){
        product.setId(id);
        productRepository.save(product);
    }

    //метод позволит удалить товар по id
    @Transactional//так как меняет БД (уже не только чтение, а изменение БД)
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }
}
