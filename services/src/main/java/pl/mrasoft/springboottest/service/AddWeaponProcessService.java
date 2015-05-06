package pl.mrasoft.springboottest.service;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrasoft.springboottest.model.Wasteland2Weapon;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTx;

@Service
public class AddWeaponProcessService implements Command {
    private final Logger logger = LoggerFactory.getLogger(AddWeaponProcessService.class);

    @Autowired
    private Wasteland2Service wasteland2Service;

    @Override
    @WeaponTx
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        logger.info("Executing AddWeaponProcessService.execute");

//        Thread.sleep(3000);
        Wasteland2Weapon weapon = new Wasteland2Weapon("name from jbpm", "type from jbpm");
        wasteland2Service.addWeapon(weapon);

        logger.info("Executed AddWeaponProcessService.execute");
        return null;
    }
}
