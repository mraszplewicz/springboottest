package pl.mrasoft.springboottest.jbmp.internal;

import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.executor.api.ExecutionResults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Component
public class LockProcessInstanceCommandEnd {

    @PersistenceContext(unitName = "org.jbpm.persistence.spring.local")
    private EntityManager em;

    @Transactional("jbpmTransactionManager")
    public void completeWorkItem(RuntimeEngine engine, WorkItem workItem, ExecutionResults results) {

        lockProcessInstance(workItem.getProcessInstanceId());

        engine.getKieSession().getWorkItemManager().completeWorkItem(workItem.getId(), results.getData());
    }

    @Transactional("jbpmTransactionManager")
    public void lockProcessInstance(long processInstanceId) {
        em.find(ProcessInstanceInfo.class, processInstanceId, LockModeType.PESSIMISTIC_WRITE);
    }

}
