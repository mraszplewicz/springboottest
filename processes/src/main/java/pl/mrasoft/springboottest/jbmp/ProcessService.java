package pl.mrasoft.springboottest.jbmp;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class ProcessService {

    @Autowired
    private RuntimeManager runtimeManager;

    @Transactional("jbpmTransactionManager")
    public ProcessInstance startProcess(String processId) {

        return getKieSession().startProcess(processId);
    }

    @Transactional("jbpmTransactionManager")
    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {

        return getKieSession().startProcess(processId, parameters);
    }

    private KieSession getKieSession() {
        RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtimeEngine.getKieSession();

        return ksession;
    }


}
