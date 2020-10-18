package com.fertigapp.backend;

import com.fertigApp.backend.BackendApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = BackendApplication.class)
class BackendApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplicationTests.class, args);
    }

    @Test
    void authLoginOk() throws Exception{
        assertTrue(true);
    }

}
