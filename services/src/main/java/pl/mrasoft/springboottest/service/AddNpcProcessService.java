package pl.mrasoft.springboottest.service;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrasoft.springboottest.model.Wasteland2NPC;
import pl.mrasoft.springboottest.repository.transaction.annotation.NpcTx;

@Service
public class AddNpcProcessService implements Command {
    private final Logger logger = LoggerFactory.getLogger(AddNpcProcessService.class);

    @Autowired
    private Wasteland2Service wasteland2Service;

    @Override
    @NpcTx
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        logger.info("Executing AddNpcProcessService.execute");

        Wasteland2NPC npc = new Wasteland2NPC("name from jbpm", "location from jbpm");
        wasteland2Service.addNPC(npc);

        logger.info("Executed AddNpcProcessService.execute");
        return null;
    }
}
