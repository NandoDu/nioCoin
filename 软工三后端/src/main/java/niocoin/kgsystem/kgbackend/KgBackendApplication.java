package niocoin.kgsystem.kgbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@SpringBootApplication
public class KgBackendApplication {
    public static void main(String[] args) throws IOException {
        ApplicationContext applicationContext = SpringApplication.run(KgBackendApplication.class, args);
        Object bean = applicationContext.getBean("driver");
    }
}
