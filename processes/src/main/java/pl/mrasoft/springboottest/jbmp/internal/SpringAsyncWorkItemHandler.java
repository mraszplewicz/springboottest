package pl.mrasoft.springboottest.jbmp.internal;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.time.TimeUtils;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandlerCmdCallback;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class SpringAsyncWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(SpringAsyncWorkItemHandler.class);

    private ExecutorService executorService;

    public SpringAsyncWorkItemHandler(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        if (executorService == null || !executorService.isActive()) {
            throw new IllegalStateException("Executor is not set or is not active");
        }
        boolean autoComplete = false;
        if (workItem.getParameter("AutoComplete") != null) {
            autoComplete = Boolean.parseBoolean(workItem.getParameter("AutoComplete").toString());
        }

        String businessKey = buildBusinessKey(workItem);
        logger.debug("Executing work item {} with built business key {}", workItem, businessKey);
        String beanName = (String) workItem.getParameter("BeanName");
        String cmdClass = SpringCommand.class.getName();

        logger.debug("Command class for this execution is {}", cmdClass);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData(SpringCommand.BEAN_NAME_PARAMETER, beanName);
        ctxCMD.setData("businessKey", businessKey);
        ctxCMD.setData("workItem", workItem);
        ctxCMD.setData("processInstanceId", getProcessInstanceId(workItem));
        ctxCMD.setData("deploymentId", ((WorkItemImpl) workItem).getDeploymentId());
        // in case auto complete is selected skip callback
        if (!autoComplete) {
            ctxCMD.setData("callbacks", AsyncWorkItemHandlerCmdCallback.class.getName());
        }

        if (workItem.getParameter("Retries") != null) {
            ctxCMD.setData("retries", Integer.parseInt(workItem.getParameter("Retries").toString()));
        }
        if (workItem.getParameter("Owner") != null) {
            ctxCMD.setData("owner", workItem.getParameter("Owner"));
        }

        if (workItem.getParameter("RetryDelay") != null) {

            ctxCMD.setData("retryDelay", workItem.getParameter("RetryDelay"));
        }

        Date scheduleDate = new Date();
        if (workItem.getParameter("Delay") != null) {
            long delayInMillis = TimeUtils.parseTimeString((String) workItem.getParameter("Delay"));
            scheduleDate = new Date(System.currentTimeMillis() + delayInMillis);
        }


        logger.trace("Command context {}", ctxCMD);
        Long requestId = executorService.scheduleRequest(cmdClass, scheduleDate, ctxCMD);
        logger.debug("Request scheduled successfully with id {}", requestId);

        if (autoComplete) {
            logger.debug("Auto completing work item with id {}", workItem.getId());
            manager.completeWorkItem(workItem.getId(), null);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        String businessKey = buildBusinessKey(workItem);
        logger.info("Looking up for not cancelled and not done requests for business key {}", businessKey);
        List<RequestInfo> requests = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        if (requests != null) {
            for (RequestInfo request : requests) {
                if (request.getStatus() != STATUS.CANCELLED && request.getStatus() != STATUS.DONE
                        && request.getStatus() != STATUS.ERROR) {
                    logger.info("About to cancel request with id {} and business key {} request state {}",
                            request.getId(), businessKey, request.getStatus());
                    executorService.cancelRequest(request.getId());
                }
            }
        }
    }

    protected String buildBusinessKey(WorkItem workItem) {
        StringBuffer businessKey = new StringBuffer();
        businessKey.append(getProcessInstanceId(workItem));
        businessKey.append(":");
        businessKey.append(workItem.getId());
        return businessKey.toString();
    }

    protected long getProcessInstanceId(WorkItem workItem) {
        return ((WorkItemImpl) workItem).getProcessInstanceId();
    }


}
