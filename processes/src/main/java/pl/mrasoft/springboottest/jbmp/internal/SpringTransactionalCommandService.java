package pl.mrasoft.springboottest.jbmp.internal;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.drools.persistence.TransactionManager;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.api.command.Command;
import org.kie.internal.command.Context;
import org.kie.spring.persistence.KieSpringTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManager;

public class SpringTransactionalCommandService implements CommandService {

    private static final Logger logger = LoggerFactory.getLogger(SpringTransactionalCommandService.class);

    private EntityManager em;
    private Context context;
    private TransactionManager txm;

    public SpringTransactionalCommandService(EntityManager em, AbstractPlatformTransactionManager transactionManager) {
        this.em = em;
        this.txm = new KieSpringTransactionManager(transactionManager);
    }

    public Context getContext() {
        return context;
    }

    protected void setEm(EntityManager em) {
        this.em = em;
    }

    public <T> T execute(Command<T> command) {
        boolean transactionOwner = false;
        T result = null;

        try {
            transactionOwner = txm.begin();
            JpaPersistenceContext context = new JpaPersistenceContext(em);

            result = ((GenericCommand<T>) command).execute(context);
            txm.commit(transactionOwner);
            context.close(transactionOwner);
            return result;

        } catch (RuntimeException re) {
            rollbackTransaction(re, transactionOwner);
            throw re;
        } catch (Exception t1) {
            rollbackTransaction(t1, transactionOwner);
            throw new RuntimeException("Wrapped exception see cause", t1);
        }

    }

    private void rollbackTransaction(Exception t1, boolean transactionOwner) {
        try {
            logger.warn("Could not commit session", t1);
            txm.rollback(transactionOwner);
        } catch (Exception t2) {
            logger.error("Could not rollback", t2);
            throw new RuntimeException("Could not commit session or rollback", t2);
        }
    }
}
