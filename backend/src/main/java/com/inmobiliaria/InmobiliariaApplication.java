package com.inmobiliaria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(
    systemName = "Sistema Inmobiliario",
    sharedModules = "shared"
)
public class InmobiliariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(InmobiliariaApplication.class, args);
    }
}
