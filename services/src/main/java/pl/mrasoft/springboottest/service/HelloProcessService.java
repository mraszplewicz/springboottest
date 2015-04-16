package pl.mrasoft.springboottest.service;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrasoft.springboottest.model.Wasteland2NPC;

@Service
public class HelloProcessService implements Command {
    @Autowired
    private Wasteland2Service wasteland2Service;

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {

        Wasteland2NPC npc = new Wasteland2NPC("name from jbpm", "location from jbpm");
        wasteland2Service.addNPC(npc);

        return null;
    }
}
