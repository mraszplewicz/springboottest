package liquibase.ext.hibernate.database;

import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.ext.hibernate.database.connection.HibernateConnection;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.service.ServiceRegistry;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;

public class ExtendedHibernateSpringDatabase extends HibernateSpringDatabase {

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return conn.getURL().startsWith("hibernate:extspring:");
    }

    @Override
    public Configuration buildConfigurationFromScanning(HibernateConnection connection) {
        String[] packagesToScan = connection.getPath().split(",");

        for (String packageName : packagesToScan) {
            LOG.info("Found package "+packageName);
        }

        DefaultPersistenceUnitManager internalPersistenceUnitManager = new DefaultPersistenceUnitManager();

        internalPersistenceUnitManager.setPackagesToScan(packagesToScan);

        String dialectName = connection.getProperties().getProperty("dialect", null);
        if (dialectName == null) {
            throw new IllegalArgumentException("A 'dialect' has to be specified.");
        }
        LOG.info("Found dialect "+dialectName);

        internalPersistenceUnitManager.preparePersistenceUnitInfos();
        PersistenceUnitInfo persistenceUnitInfo = internalPersistenceUnitManager.obtainDefaultPersistenceUnitInfo();
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(dialectName);

        String enhancedId = connection.getProperties().getProperty("hibernate.enhanced_id", "false");
        LOG.info("Found hibernate.enhanced_id" + enhancedId);

        Map<String, Object> jpaPropertyMap = jpaVendorAdapter.getJpaPropertyMap();
        jpaPropertyMap.put("hibernate.archive.autodetection", "false");
        jpaPropertyMap.put("hibernate.id.new_generator_mappings", enhancedId);

        if (persistenceUnitInfo instanceof SmartPersistenceUnitInfo) {
            ((SmartPersistenceUnitInfo) persistenceUnitInfo).setPersistenceProviderPackageName(jpaVendorAdapter.getPersistenceProviderRootPackage());
        }

        EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(persistenceUnitInfo,
                jpaPropertyMap);
        ServiceRegistry serviceRegistry = builder.buildServiceRegistry();

        Configuration configuration = builder.buildHibernateConfiguration(serviceRegistry);

        if (persistenceUnitInfo instanceof SmartPersistenceUnitInfo) {
            for (String managedPackage : ((SmartPersistenceUnitInfo) persistenceUnitInfo).getManagedPackages()) {
                configuration.addPackage(managedPackage);
            }
        }
        return configuration;
    }

    @Override
    public String getShortName() {
        return "extHibernateSpring";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return "Extended Hibernate Spring";
    }

}
