package pl.mrasoft.springboottest.jbmp.internal;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.executor.api.RequestInfo;
import org.kie.internal.executor.api.STATUS;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomExecutorQueryService extends org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl {

    private CommandService commandService;
    private EntityManager em;

    public CustomExecutorQueryService(EntityManager em, boolean active) {
        super(active);
        this.em = em;
    }

    @Override
    public void setCommandService(CommandService commandService) {
        super.setCommandService(commandService);
        this.commandService = commandService;
    }

    @Override
    public RequestInfo getRequestForProcessing() {

        // need to do the lock here to avoid many executor services fetch the same element
        RequestInfo request = commandService.execute(new LockAndUpdateRequestInfoCommand(em));

        return request;
    }

    private class LockAndUpdateRequestInfoCommand implements GenericCommand<RequestInfo> {

        private EntityManager em;

        public LockAndUpdateRequestInfoCommand(EntityManager em) {
            this.em = em;
        }

        @Override
        public RequestInfo execute(Context context) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("now", new Date());
            params.put("owner", ExecutorService.EXECUTOR_ID);
            RequestInfo request = null;
            try {
                CustomJpaPersistenceContext ctx = new CustomJpaPersistenceContext(em);
                List<RequestInfo> requestList = ctx.queryAndLockWithParametersInTransaction("PendingRequestsForProcessing", params, RequestInfo.class);
                request = requestList.get(0);
                if (request != null) {
                    request.setStatus(STATUS.RUNNING);
                    // update date on when it was started to be executed
                    ((org.jbpm.executor.entities.RequestInfo)request).setTime(new Date());
                    ctx.merge(request);
                }
            } catch (NoResultException e) {

            }
            return request;
        }

    }
}
