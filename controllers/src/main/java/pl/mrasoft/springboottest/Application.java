package pl.mrasoft.springboottest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import pl.mrasoft.springboottest.model.Wasteland2NPC;
import pl.mrasoft.springboottest.model.Wasteland2Weapon;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.npc")
    public DataSource npcDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.weapon")
    public DataSource weaponDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.npcro")
    public DataSource npcDataSourceRO() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.weaponro")
    public DataSource weaponDataSourceRO() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean npcEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("npcDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Wasteland2NPC.class)
                .persistenceUnit("npc")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean weaponEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("weaponDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Wasteland2Weapon.class)
                .persistenceUnit("weapon")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean npcEntityManagerFactoryRO(
            EntityManagerFactoryBuilder builder,
            @Qualifier("npcDataSourceRO") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Wasteland2NPC.class)
                .persistenceUnit("npcro")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean weaponEntityManagerFactoryRO(
            EntityManagerFactoryBuilder builder,
            @Qualifier("weaponDataSourceRO") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(Wasteland2Weapon.class)
                .persistenceUnit("weaponro")
                .build();
    }

    @Bean
    public PlatformTransactionManager npcTransactionManager(@Qualifier("npcEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    public PlatformTransactionManager weaponTransactionManager(@Qualifier("weaponEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    public PlatformTransactionManager npcTransactionManagerRO(@Qualifier("npcEntityManagerFactoryRO") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    public PlatformTransactionManager weaponTransactionManagerRO(@Qualifier("weaponEntityManagerFactoryRO") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

}
