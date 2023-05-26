package com.example.market.config;
import com.example.market.services.PersonDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests() //указываем что все страницы должны быть защищены аутентификацией
                .requestMatchers("/admin").hasRole("ADMIN")//указываем, что страница /admin доступна только пользователю с ролью ADMIN
                .requestMatchers("/authentication", "/registration", "/error","/resources/**", "/static/**","/css/**", "/js/**","/img/**", "/product","/image/**", "/product/info/{id}", "/product/search").permitAll() //перечисленные страницы в .requestMatchers доступны всем пользователям
                .anyRequest().hasAnyRole("USER","ADMIN")// указываем, что все остальные страницы доступны пользователям с ролями USER, ADMIN
                .and() // указываем, что дальше настраивается аутентификация и соединяем ее с настройкой доступа
                .formLogin().loginPage("/authentication")// указываем какой url запрос будет отправлятся при заходе на защищенные страницы
                .loginProcessingUrl("/process_login") // указываем на какой адрес будут отправляться данные с формы. Нам уже не нужно будет создавать метод в контроллере и обрабатывать данные с формы. Мы задали url, который используется по умолчанию для обработки форьы аутентификации по средствам SpringSecurity. SpringSecurity будет ждать объект с формы аутентификации и затем сверять логин и пароль с данными в БД
                .defaultSuccessUrl("/personAccount", true) //указываем на какой url необходимо направить пользователя после успешной аутентификации. Вторым аргументом указывается true, чтобы перенаправление шло в любом случае после успешной аутентификации
                .failureUrl("/authentication?error") // указываем куда необходимо перенаправить пользователя при проваленной аутентификации. В запрос будет передан объект error(в качестве get параметра), который будет проверяться на форме и при наличии данного объекта в запросе выводиться сообщение "Неправильный логин или пароль"
                .and()//указываем, что дальше будет еще какой-то блок настройки
                .logout()//указываем, что у нас идет настройка выхода из аккаунта
                .logoutUrl("/logout")//указываем, что при выходе из аккаунта это всё будет обрабатываться определенным url
                .logoutSuccessUrl("/authentication");// указываем, что после выхода из аккаунта необходимо перенаправлять пользователя по этому url

        return http.build();
    }


    protected  void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder());


    }
}
