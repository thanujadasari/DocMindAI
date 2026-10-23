package com.docmind;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.docmind.service.ChromaService;




@SpringBootApplication
public class DocMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocMindApplication.class, args);
    }
@Bean
CommandLineRunner testChromaConnection(ChromaService chromaService) {
    return args -> {
        System.out.println("ChromaDB Connection Test:");
        System.out.println(chromaService.checkConnection());

        System.out.println("Creating ChromaDB collection...");
        System.out.println(chromaService.createCollection());
    };
}
}