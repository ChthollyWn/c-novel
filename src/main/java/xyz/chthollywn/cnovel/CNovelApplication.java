package xyz.chthollywn.cnovel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CNovelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CNovelApplication.class, args);
    }
}
