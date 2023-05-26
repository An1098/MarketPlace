package com.example.market.controllers;

import com.example.market.enumm.Status;
import com.example.market.models.Cart;
import com.example.market.models.Order;
import com.example.market.models.Person;
import com.example.market.models.Product;
import com.example.market.repositories.CartRepository;
import com.example.market.repositories.OrderRepository;
import com.example.market.repositories.ProductRepository;
import com.example.market.security.PersonDetails;
import com.example.market.services.PersonService;
import com.example.market.services.ProductService;
import com.example.market.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    private final PersonValidator personValidator;
    private final PersonService personService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private  final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public MainController(PersonValidator personValidator, PersonService personService, ProductService productService, ProductRepository productRepository, CartRepository cartRepository, OrderRepository orderRepository) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    //срабатывает при идентификации (при вводе логина и пароля)
    @GetMapping("/personAccount")
    public String index(Model model){
        //получим объект аутентифицированного пользователя с помощью SpringContextHolder из сессии, обращаемся к контексту и на нем вызываем метод аутентификации. Из сессии текущего пользователя получаем объект, который был положен в данную сессию после аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //преобразовываем объект аутентификации в PersonDetails, чтобы можно было с помощью этого класса работать с данными пользователя
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        //получим роль пользователя в переменную role
        //по personDetails мы извлекаем Person и из Person извлекаем Role
        String role = personDetails.getPerson().getRole();

        //если роль пользователя Админ, то он переходит на страницу администратора, если нет, то он вернется на страницу index
        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }


        model.addAttribute("products", productService.getAllProduct());
        return "user/index";
    }

    //функционал для регистрации

    @GetMapping("/registration")
    public String registration (@ModelAttribute("person") Person person){
        return "registration";
    }

    // метод позволит обработать данные с нашей формы registration
    // в параметрах принимаем объект с формы, указываем, что наша модель должна валидироваться и все ошибки валидации мы положим в BindingResult
    //обращаемся к personValidator вызываем метод validate, который позволит нам проверить данные, передаём в него объект person и объект ошибки
    // если ошибки есть, мы возвращаем форму регистрации на которой все ошибки отобразяться
    // если ошибки нет, то мы обращаемся к personService и вызываем метод регистрации в который мы передаём объект
    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/personAccount";
    }

    //Срабатывает при переходе на страницу аутентифицированного пользователя
    // Чтобы в аккаунте пользователя выводились все товары
    @GetMapping("/personAccount/product")
    public String getAllProduct(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "/user/index";
    }

    //При нажатии на ссылку продукта  в кабинете пользователя (для перехода в карточку продукта)
    //получаем id товара на который нажали, создаём модель
    //в модель положили аттрибут по ключу "product", обратидись к productService и его метод вернул нам продукт по конкретному id
    @GetMapping("/personAccount/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "/product/infoProduct";
    }

    //поиск товара
    @PostMapping("/personAccount/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "contract", required = false, defaultValue = "") String contract, Model model) {
        model.addAttribute("products", productService.getAllProduct());

        if(!ot.isEmpty() & !Do.isEmpty()){
            if(!price.isEmpty()){
                if(price.equals("sorted_by_ascending_price")) {
                    if (!contract.isEmpty()) {
                        if (contract.equals("furniture")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        } else if (contract.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        } else if (contract.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                } else if(price.equals("sorted_by_descending_price")){
                    if(!contract.isEmpty()){
                        if(contract.equals("furniture")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        }else if (contract.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        } else if (contract.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                    }  else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        return "/user/index";
    }

    //При нажатии на кнопку Добавление товара в корзину
    //принимаем id из ссылки
    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model){
        Product product = productService.getProductId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id_person = personDetails.getPerson().getId();


        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    //обработаем "redirect:/cart" (заход в корзину)
    //как только пользователь заходит на страницу корзины, мы должны взять объект аутентификации, преобразовать в personDetails и получить id того пользователя который заходит в корзину
    @GetMapping("/cart")
    public String cart(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id_person = personDetails.getPerson().getId();


        List<Cart> cartList = cartRepository.findByPersonId(id_person);

        List<Product> productList = new ArrayList<>();
        for (Cart cart : cartList
        ) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product : productList) {
            price+=product.getPrice();
        }
        model.addAttribute("price", price);


        model.addAttribute("cart_product", productList);

        return "/user/cart";
    }

    //Удаление товара из корзины
    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);

        List<Product> productList = new ArrayList<>();
        for (Cart cart : cartList
        ) {
            productList.add(productService.getProductId(cart.getProductId()));
        }
        cartRepository.deleteCartByProductId(id);

        return "redirect:/cart";
    }

    //работа с заказом
    @GetMapping("/order/create")
    public String order(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);

        List<Product> productList = new ArrayList<>();
        for (Cart cart : cartList
        ) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        float price = 0;
        for (Product product : productList) {
            price+=product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();

        for (Product product : productList){
            Order newOrder = new Order(uuid, product,personDetails.getPerson(), 1,product.getPrice(), Status.Оформлен);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    //При нажатии на кнопку Заказы
    @GetMapping("/orders")
    public String orderUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }
}
