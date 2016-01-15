package pl.mrasoft.springboottest.jbmp.internal;


import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.ExecutorStoreService;
import org.kie.internal.executor.api.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManager;

public class SpringExecutorServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringExecutorServiceFactory.class);

    private static ExecutorService serviceInstance;

    public static synchronized ExecutorService newExecutorService(EntityManager em, AbstractPlatformTransactionManager transactionManager) {
        if (serviceInstance == null) {
            serviceInstance = configure(em, transactionManager);
        }
        return serviceInstance;
    }

    public static synchronized void resetExecutorService(ExecutorService executorService) {
        if (executorService.equals(serviceInstance)) {
            serviceInstance = null;
        }
    }

    private static ExecutorService configure(EntityManager em, AbstractPlatformTransactionManager transactionManager) {

        // create instances of executor services

        ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
        Executor executor = new ExecutorImpl();
        ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();

        // create executor for persistence handling
        SpringTransactionalCommandService commandService = new SpringTransactionalCommandService(em, transactionManager);

        ExecutorStoreService storeService = new SpringJPAExecutorStoreService(true);
        ((SpringJPAExecutorStoreService) storeService).setCommandService(commandService);
        ((SpringJPAExecutorStoreService) storeService).setEm(em);
        ((SpringJPAExecutorStoreService) storeService).setTransactionManager(transactionManager);

        ((ExecutorImpl) executor).setExecutorStoreService(storeService);

        // set executor on all instances that requires it
        ((ExecutorQueryServiceImpl) queryService).setCommandService(commandService);
        ((ExecutorRequestAdminServiceImpl) adminService).setCommandService(commandService);


        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
        ((ExecutorServiceImpl) service).setQueryService(queryService);
        ((ExecutorServiceImpl) service).setExecutor(executor);
        ((ExecutorServiceImpl) service).setAdminService(adminService);


        return service;
    }

    public static ExecutorRunnable buildRunable(EntityManager em, AbstractPlatformTransactionManager transactionManager) {
        ExecutorRunnable runnable = new ExecutorRunnable();
        AvailableJobsExecutor jobExecutor = null;

        jobExecutor = buildJobExecutor(em, transactionManager);
        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }


    private static AvailableJobsExecutor buildJobExecutor(EntityManager em, AbstractPlatformTransactionManager transactionManager) {
        AvailableJobsExecutor jobExecutor;
        jobExecutor = new AvailableJobsExecutor();
        ClassCacheManager classCacheManager = new ClassCacheManager();
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);

        SpringTransactionalCommandService cmdService = new SpringTransactionalCommandService(em, transactionManager);
        ExecutorStoreService storeService = new SpringJPAExecutorStoreService(true);
        ((SpringJPAExecutorStoreService) storeService).setCommandService(cmdService);
        ((SpringJPAExecutorStoreService) storeService).setEm(em);
        ((SpringJPAExecutorStoreService) storeService).setTransactionManager(transactionManager);

        ((ExecutorQueryServiceImpl) queryService).setCommandService(cmdService);
        jobExecutor.setClassCacheManager(classCacheManager);
        jobExecutor.setQueryService(queryService);
        jobExecutor.setExecutorStoreService(storeService);

        return jobExecutor;
    }

//    public static ExecutorRunnable buildRunable() {
//        ExecutorRunnable runnable = new ExecutorRunnable();
//        AvailableJobsExecutor jobExecutor = null;
//        try {
//            jobExecutor = InitialContext.doLookup("java:module/AvailableJobsExecutor");
//        } catch (Exception e) {
//            jobExecutor = new AvailableJobsExecutor();
//            ClassCacheManager classCacheManager = new ClassCacheManager();
//
//            InMemoryExecutorStoreService storeService = new InMemoryExecutorStoreService(true);
//            InMemoryExecutorQueryServiceImpl queryService = new InMemoryExecutorQueryServiceImpl(true);
//            queryService.setStoreService(storeService);
//
//            jobExecutor.setClassCacheManager(classCacheManager);
//            jobExecutor.setQueryService(queryService);
//            jobExecutor.setExecutorStoreService(storeService);
//        }
//        runnable.setAvailableJobsExecutor(jobExecutor);
//        return runnable;
//    }
}
