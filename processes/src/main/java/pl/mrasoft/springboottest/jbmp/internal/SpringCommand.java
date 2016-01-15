package pl.mrasoft.springboottest.jbmp.internal;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import pl.mrasoft.springboottest.jbmp.spring.ApplicationContextProvider;

public class SpringCommand implements Command {
    public static final String NAME = "asyncSpring";
    public static final String BEAN_NAME_PARAMETER = "beanName";

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        String beanName = (String) ctx.getData(BEAN_NAME_PARAMETER);

        Object bean = ApplicationContextProvider.getApplicationContext().getBean(beanName);

        ExecutionResults results = ((Command) bean).execute(ctx);

        if (results == null) {
            results = new ExecutionResults();
        }

        return results;
    }
}
