package pl.mrasoft.springboottest.jbmp;

import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.factorybeans.RuntimeEnvironmentFactoryBean;
import org.kie.spring.factorybeans.RuntimeManagerFactoryBean;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import pl.mrasoft.springboottest.jbmp.internal.SpringExecutorServiceFactory;
import pl.mrasoft.springboottest.jbmp.internal.SpringQuartzSchedulerService;
import pl.mrasoft.springboottest.jbmp.internal.SpringRegisterableItemsFactory;
import pl.mrasoft.springboottest.jbmp.spring.ApplicationContextProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class JbpmConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "datasource.jbpm")
    public DataSource jbpmDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.quartz")
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    public PlatformTransactionManager jbpmTransactionManager(@Qualifier("jbpmEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean jbpmEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("jbpmDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(dataSource)
                .persistenceUnit("org.jbpm.persistence.spring.local")
                .build();
        emf.setPersistenceXmlLocation("classpath:config/persistence-local.xml");
        return emf;
    }

    @Bean
    public SharedEntityManagerBean jbpmEntityManager(
            @Qualifier("jbpmEntityManagerFactory") EntityManagerFactory emf) {
        SharedEntityManagerBean entityManager = new SharedEntityManagerBean();
        entityManager.setEntityManagerFactory(emf);
        return entityManager;
    }

    @Bean
    public Resource processClassPathResource() {
        return ResourceFactory.newClassPathResource("sample.bpmn2");
    }

    @Bean
    public RuntimeEnvironment runtimeEnvironment(
            @Qualifier("jbpmEntityManagerFactory") EntityManagerFactory emf,
            @Qualifier("jbpmEntityManager") EntityManager em,
            @Qualifier("jbpmTransactionManager") PlatformTransactionManager transactionManager,
            @Qualifier("processClassPathResource") Resource resource,
            SpringRegisterableItemsFactory registerableItemsFactory,
            GlobalSchedulerService quartzSchedulerService) throws Exception {

        RuntimeEnvironmentFactoryBean factory = new RuntimeEnvironmentFactoryBean();
        factory.setSchedulerService(quartzSchedulerService);
        factory.setType(RuntimeEnvironmentFactoryBean.TYPE_DEFAULT);
        factory.setEntityManagerFactory(emf);
        factory.setEntityManager(em);
        factory.setTransactionManager(transactionManager);
        factory.setRegisterableItemsFactory(registerableItemsFactory);
        Map<Resource, ResourceType> assets = new HashMap<>();
        assets.put(resource, ResourceType.BPMN2);
        factory.setAssets(assets);

        return (RuntimeEnvironment) factory.getObject();
    }

    @Bean(destroyMethod = "close")
    public RuntimeManager runtimeManager(
            RuntimeEnvironment runtimeEnvironment) throws Exception {
        RuntimeManagerFactoryBean factory = new RuntimeManagerFactoryBean();
        factory.setIdentifier("spring-rm");
        factory.setRuntimeEnvironment(runtimeEnvironment);
        factory.setType("PER_REQUEST");
        return (RuntimeManager) factory.getObject();
    }

    @Bean(destroyMethod = "shutdown")
    public GlobalSchedulerService quartzSchedulerService(Scheduler scheduler) throws Exception {
        SpringQuartzSchedulerService quartzSchedulerService = new SpringQuartzSchedulerService();
        quartzSchedulerService.setQuartzScheduler(scheduler);

        return quartzSchedulerService;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            @Qualifier("quartzDataSource") DataSource dataSource) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file:
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);

        factory.setQuartzProperties(quartzProperties());

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


    @Bean(initMethod = "init")
    @ConfigurationProperties(prefix = "jbpm.executor")
    @DependsOn("applicationContextProvider") //maybe should use ContextRefreshedEvent
    public ExecutorService jbpmExecutorService(
            @Qualifier("jbpmEntityManager") EntityManager em,
            @Qualifier("jbpmTransactionManager") AbstractPlatformTransactionManager transactionManager) throws Exception {

        SpringExecutorServiceFactory factory = new SpringExecutorServiceFactory();
        ExecutorService service = factory.newExecutorService(em, transactionManager);

        return service;
    }

}
