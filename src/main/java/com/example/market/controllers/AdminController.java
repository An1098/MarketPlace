package com.example.market.controllers;
import com.example.market.models.Category;
import com.example.market.models.Image;
import com.example.market.models.Person;
import com.example.market.models.Product;
import com.example.market.repositories.CategoryRepository;
import com.example.market.repositories.OrderRepository;
import com.example.market.repositories.PersonRepository;
import com.example.market.services.PersonService;
import com.example.market.services.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AdminController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;

    public AdminController(ProductService productService, CategoryRepository categoryRepository, OrderRepository orderRepository, PersonRepository personRepository, PersonService personService) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    //внедряем путь под хранение наших фото
    @Value("${upload.path}")
    private String uploadPath;


    //открытие шаблона админ
    //на странице будем выводить все существующие продукты, поэтому (Model model)
    @GetMapping("/admin")
    public String admin(Model model){
        //положим в модель лист всех продуктов
        model.addAttribute("products", productService.getAllProduct());

        return "admin";
    }
    //просмотр всех заказов
    @GetMapping("/ordersAdmin")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "/orderAdmin";
    }

    //просмотр всех пользователей
    @GetMapping("/persons")
    public String findPersons(Model model) {
        model.addAttribute("persons", personRepository.findAll());
        return "/persons";
    }

    //При нажатии на кнопку удаление товара
    @GetMapping("admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }


    //При нажатии на кнопку редактирование товара
    @GetMapping("admin/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id){
        //нужно положить в модель два аттрибута это редактируемый продукт и мы обращаемся к productService вызываем метод и передаём в него id (для получения товара по id)
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }
    //При нажатии на кнопку Редактировать(когда внесли изменения)
    //принимаем объект, его сразу валидируем, если есть ошибки, кладём эти ошибки в bindingResult и ещё принимем id и создаём модель
    @PostMapping("admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id, Model model){
        //есди в массиве bindingResult есть ошибки, тогда возвращаем обратно шаблон редактирования
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/editProduct";
        }
        //если ошибок нет, обращаемся к сервису вызываем метод updateProduct() передаём id редактируемого продукта и сам объект продукта
        productService.updateProduct(id, product);
        //и редиректимся на главную страницу администратора,
        return "redirect:/admin";
    }


    //При нажатии на кнопку Добавление товара
    //кладём в модель объект продукта (этот объект привяжем к форме) и объект категории (список всех категорий)
    @GetMapping("admin/product/add")
    public String addProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    //принимаем данные с формы добавления товара
    //принимаем объект модели "product", под хранение модели создаём экземпляр модели roduct product и указываем аннотацию @Valid которая позволит валидировать значения, создаём bindingResult, чтобы в него положить все ошибки в результате валидации
    //так как input type=file у нас не привязан к форме, здесь мы должны его отдельно получить @RequestParam("file_one") MultipartFile file_one и т.д.
    //принимаем категории
    //создаём экземпляр модели Model model
    @PostMapping("admin/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam("file_one")MultipartFile file_one, @RequestParam("file_two")MultipartFile file_two, @RequestParam("file_three")MultipartFile file_three, @RequestParam("file_four")MultipartFile file_four, @RequestParam("file_five")MultipartFile file_five, @RequestParam("category") int category, Model model)
            throws IOException {
        Category category_db = (Category) categoryRepository.findById(category).orElseThrow();
        System.out.println(category_db.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/addProduct";
        }
        if(file_one != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(file_two != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(file_three != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
            file_three.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(file_four != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
            file_four.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(file_five != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
            file_five.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        productService.saveProduct(product, category_db);

        return "redirect:/admin";
    }


    //поиск заказа
    @PostMapping("/adminAccount/order/search")
    public String orderSearch(@RequestParam("searchOrder") String search, Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("search_order", orderRepository.findByNumber(search));
        model.addAttribute("value_searchOrder", search);

        return "/orderAdmin";
    }



    //При нажатии на кнопку редактирование пользователя
    @GetMapping("admin/person/edit/{id}")
    public String editPerson(Model model, @PathVariable("id") int id){

        model.addAttribute("person", personService.getPersonId(id));

        return "editPerson";
    }

    //При нажатии на кнопку Редактировать(когда внесли изменения)
    //принимаем объект, его сразу валидируем, если есть ошибки, кладём эти ошибки в bindingResult и ещё принимем id и создаём модель
    @PostMapping("admin/person/edit/{id}")
    public String editPerson(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult, @PathVariable("id") int id){
        if(bindingResult.hasErrors()){

            return "editPerson";
        }
        personService.updatePerson(id, person);
        return "redirect:/persons";
    }

    //При нажатии на кнопку удаление пользователя
    @GetMapping("admin/person/delete/{id}")
    public String deletePerson(@PathVariable("id") int id){
        personService.deletePerson(id);
        return "redirect:/persons";
    }

}
