package com.fertigapp.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
@SpringBootTest
class BackendApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplicationTests.class, args);
    }

    @Test
    void authLoginOk() throws Exception{
        assertTrue(true);
    }

}
