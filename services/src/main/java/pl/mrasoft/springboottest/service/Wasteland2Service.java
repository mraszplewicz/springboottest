package pl.mrasoft.springboottest.service;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mrasoft.springboottest.jbmp.ProcessService;
import pl.mrasoft.springboottest.model.Wasteland2NPC;
import pl.mrasoft.springboottest.model.Wasteland2Weapon;
import pl.mrasoft.springboottest.repository.Wasteland2NpcRepository;
import pl.mrasoft.springboottest.repository.Wasteland2WeaponRepository;
import pl.mrasoft.springboottest.repository.transaction.annotation.NpcTx;
import pl.mrasoft.springboottest.repository.transaction.annotation.NpcTxRO;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTx;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTxRO;

import java.util.List;

@Service
public class Wasteland2Service {

    @Autowired
    private ProcessService processService;


    @Autowired
    private Wasteland2NpcRepository npcRepository;

    @Autowired
    private Wasteland2WeaponRepository weaponRepository;

    @WeaponTxRO
    public List<Wasteland2Weapon> findLastWeapons(long limit) {
        return weaponRepository.findAllWithPages(0, limit);
    }

    @NpcTxRO
    public List<Wasteland2NPC> findLastNPCs(long limit) {
        return npcRepository.findAllWithPages(0, limit);
    }

    @WeaponTx
    public void addWeapon(Wasteland2Weapon weapon) {
        weaponRepository.save(weapon);
    }

    @NpcTx
    public void addNPC(Wasteland2NPC npc) {
        npcRepository.save(npc);
    }

    @WeaponTx
    public List<Wasteland2Weapon> addWeaponAndFindLast(Wasteland2Weapon weapon, long limit) {
        addWeapon(weapon);
        return weaponRepository.findAllWithPagesRW(0, limit);
    }

    @NpcTx
    public List<Wasteland2NPC> addNpcAndFindLast(Wasteland2NPC npc, long limit) {
        addNPC(npc);
        return npcRepository.findAllWithPagesRW(0, limit);
    }

    public void startAddNpcAndWeaponProcess() {
        processService.startProcess("defaultPackage.sample");
    }

}
