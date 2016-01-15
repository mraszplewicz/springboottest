package pl.mrasoft.springboottest.jbmp.internal;


import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

    @Autowired
    private ExecutorService executorService;


    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
        handlers.put(SpringCommand.NAME, new SpringAsyncWorkItemHandler(executorService));
        return handlers;
    }
}
