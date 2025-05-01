package org.ecom.mypetstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения, с которого начинается запуск Spring Boot.
 * Аннотация @SpringBootApplication объединяет:
 * - @Configuration (описание бинов)
 * - @EnableAutoConfiguration (автоматическая настройка Spring Boot)
 * - @ComponentScan (поиск компонентов в пакете)
 */
@SpringBootApplication
public class MypetstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypetstoreApplication.class, args);
    }

}
