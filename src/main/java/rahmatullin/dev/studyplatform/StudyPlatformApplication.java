package rahmatullin.dev.studyplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class StudyPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPlatformApplication.class, args);
    }

    @RestController
    static class HelloController {
        @GetMapping("/")
        public String home() {
            return "<h1>Привет с Spring Boot в Docker! 🚀</h1>" +
                    "<p>Ты успешно подключился через сеть.</p>";
        }

        @GetMapping("/ping")
        public String ping() {
            return "pong";
        }
    }
}
