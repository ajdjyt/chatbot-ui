package ap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // steorotype annotation
@SpringBootApplication // meta annotation
public class ap {

    @RequestMapping("/") // router
    String home() {
        return "Hello!";
    }

    public static void main(String[] args) {
        SpringApplication.run(ap.class, args);
    }

}

