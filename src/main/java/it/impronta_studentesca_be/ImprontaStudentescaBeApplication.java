package it.impronta_studentesca_be;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ImprontaStudentescaBeApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ImprontaStudentescaBeApplication.class, args);
    }

}
