package example.ru.freeslotbottg.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
public class BotConfig {
    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
}
