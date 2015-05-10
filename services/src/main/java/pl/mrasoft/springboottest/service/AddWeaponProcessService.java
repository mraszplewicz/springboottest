package pl.mrasoft.springboottest.service;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrasoft.springboottest.model.Wasteland2Weapon;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTx;

@Service
public class AddWeaponProcessService implements Command {
    @Autowired
    private Wasteland2Service wasteland2Service;

    @Override
    @WeaponTx
    public ExecutionResults execute(CommandContext ctx) throws Exception {

        Wasteland2Weapon weapon = new Wasteland2Weapon("name from jbpm", "type from jbpm");
        wasteland2Service.addWeapon(weapon);

//        throw new RuntimeException("Exception from while adding Wasteland2Weapon!");
        return null;
    }
}
