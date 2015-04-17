package pl.mrasoft.springboottest.jbmp.internal;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.ExecutorStoreService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.Map;

public class SpringJPAExecutorStoreService implements ExecutorStoreService {

    private EntityManager em;
    private CommandService commandService;
    private AbstractPlatformTransactionManager transactionManager;

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public SpringJPAExecutorStoreService(boolean active) {

    }

    public void setCommandService(CommandService commandService) {
        this.commandService = commandService;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Override
    public void persistRequest(RequestInfo request) {
        commandService.execute(new org.jbpm.shared.services.impl.commands.PersistObjectCommand(request));
    }

    @Override
    public void updateRequest(RequestInfo request) {
        commandService.execute(new org.jbpm.shared.services.impl.commands.MergeObjectCommand(request));

    }

    @Override
    public RequestInfo removeRequest(Long requestId) {
        return commandService.execute(new LockAndCancelRequestInfoCommand(requestId));

    }

    @Override
    public RequestInfo findRequest(Long id) {
        return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<RequestInfo>(id, RequestInfo.class));
    }

    @Override
    public void persistError(ErrorInfo error) {
        commandService.execute(new org.jbpm.shared.services.impl.commands.PersistObjectCommand(error));

    }

    @Override
    public void updateError(ErrorInfo error) {
        commandService.execute(new org.jbpm.shared.services.impl.commands.MergeObjectCommand(error));

    }

    @Override
    public ErrorInfo removeError(Long errorId) {
        ErrorInfo error = findError(errorId);
        commandService.execute(new org.jbpm.shared.services.impl.commands.RemoveObjectCommand(error));
        return error;
    }

    @Override
    public ErrorInfo findError(Long id) {
        return commandService.execute(new org.jbpm.shared.services.impl.commands.FindObjectCommand<ErrorInfo>(id, ErrorInfo.class));
    }

    @Override
    public Runnable buildExecutorRunnable() {
        return SpringExecutorServiceFactory.buildRunable(em, transactionManager);
    }


    private class LockAndCancelRequestInfoCommand implements GenericCommand<RequestInfo> {

        private static final long serialVersionUID = 8670412133363766161L;

        private Long requestId;

        LockAndCancelRequestInfoCommand(Long requestId) {
            this.requestId = requestId;
        }

        @Override
        public RequestInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", requestId);
            params.put("firstResult", 0);
            params.put("maxResults", 1);
            RequestInfo request = null;
            try {
                org.jbpm.shared.services.impl.JpaPersistenceContext ctx = (org.jbpm.shared.services.impl.JpaPersistenceContext) context;
                request = ctx.queryAndLockWithParametersInTransaction("PendingRequestById", params, true, RequestInfo.class);

                if (request != null) {
                    request.setStatus(STATUS.CANCELLED);
                    ctx.merge(request);
                }
            } catch (NoResultException e) {

            }
            return request;
        }

    }

}
