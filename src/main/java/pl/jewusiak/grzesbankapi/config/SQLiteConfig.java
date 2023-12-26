package pl.jewusiak.grzesbankapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class SQLiteConfig {

    @Value("${pl.jewusiak.grzesbankapi.db.url}")
    private String dbUrl;
    
    @Value("${pl.jewusiak.grzesbankapi.db.username}")
    private String dbUser;
    
    @Value("${pl.jewusiak.grzesbankapi.db.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
