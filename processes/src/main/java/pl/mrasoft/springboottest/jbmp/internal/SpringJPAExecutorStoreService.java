package pl.mrasoft.springboottest.jbmp.internal;

import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

public class SpringJPAExecutorStoreService extends JPAExecutorStoreService {

    private EntityManagerFactory emf;
    private AbstractPlatformTransactionManager transactionManager;

    public void setEmf(EntityManagerFactory emf) {
        super.setEmf(emf);
        this.emf = emf;
    }

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public SpringJPAExecutorStoreService(boolean active) {
        super(active);
    }

    @Override
    public Runnable buildExecutorRunnable() {
        return SpringExecutorServiceFactory.buildRunable(emf, transactionManager);
    }

}
