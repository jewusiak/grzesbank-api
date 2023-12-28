package pl.jewusiak.grzesbankapi.config;

import com.mysql.cj.log.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(value = "pl.jewusiak.grzesbankapi.db.sqliteenabled", havingValue = "true")
@Slf4j
public class SQLiteConfig {

    @Value("${pl.jewusiak.grzesbankapi.db.url}")
    private String dbUrl;
    
    @Value("${pl.jewusiak.grzesbankapi.db.username}")
    private String dbUser;
    
    @Value("${pl.jewusiak.grzesbankapi.db.password}")
    private String dbPassword;

    @Bean
    @ConditionalOnProperty(value = "pl.jewusiak.grzesbankapi.db.sqliteenabled", havingValue = "true")
    public DataSource dataSource() {
        log.info("Sqlite is being enabled...");  
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
