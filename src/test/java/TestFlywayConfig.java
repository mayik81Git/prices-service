import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class TestFlywayConfig {

    // Forzamos que el bean de Hibernate dependa del inicializador de Flyway
    @Bean
    @DependsOn("flywayInitializer")
    public String flywayDependency() {
        return "flywayReady";
    }
}