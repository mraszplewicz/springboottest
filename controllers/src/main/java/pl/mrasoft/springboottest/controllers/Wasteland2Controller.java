package pl.mrasoft.springboottest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.mrasoft.springboottest.controllers.dto.Wasteland2NPC;
import pl.mrasoft.springboottest.controllers.dto.Wasteland2Weapon;
import pl.mrasoft.springboottest.service.Wasteland2Service;

import java.util.List;


@RestController
@RequestMapping("/wasteland2")
public class Wasteland2Controller {

    @Autowired
    private Wasteland2Service wasteland2Service;


    @RequestMapping("/weapons/last/{limit}")
    public Wasteland2Weapon[] findLastWeapons(
            @PathVariable("limit") long limit) {
        List<pl.mrasoft.springboottest.model.Wasteland2Weapon> weapons = wasteland2Service.findLastWeapons(limit);

        return transformWeaponsToDtos(weapons);
    }

    @RequestMapping("/npcs/last/{limit}")
    public Wasteland2NPC[] findLastNPCs(
            @PathVariable("limit") long limit) {
        List<pl.mrasoft.springboottest.model.Wasteland2NPC> npcs = wasteland2Service.findLastNPCs(limit);

        return transformNpcsToDtos(npcs);
    }

    @RequestMapping("/weapons/add")
    public void addWeapon(
            @RequestParam(value = "name", defaultValue = "ak47") String name,
            @RequestParam(value = "type", defaultValue = "rifle") String type) {
        wasteland2Service.addWeapon(new pl.mrasoft.springboottest.model.Wasteland2Weapon(name, type));
    }

    @RequestMapping("/npcs/add")
    public void addNPC(
            @RequestParam(value = "name", defaultValue = "npc1") String name,
            @RequestParam(value = "location", defaultValue = "location1") String location) {
        wasteland2Service.addNPC(new pl.mrasoft.springboottest.model.Wasteland2NPC(name, location));
    }

    @RequestMapping("/weapons/addandlast/{limit}")
    public Wasteland2Weapon[] addWeaponAndFindLast(
            @RequestParam(value = "name", defaultValue = "m16") String name,
            @RequestParam(value = "type", defaultValue = "rifle") String type,
            @PathVariable("limit") long limit) {
        List<pl.mrasoft.springboottest.model.Wasteland2Weapon> weapons =
                wasteland2Service.addWeaponAndFindLast(new pl.mrasoft.springboottest.model.Wasteland2Weapon(name, type), limit);

        return transformWeaponsToDtos(weapons);
    }

    @RequestMapping("/npcs/addandlast/{limit}")
    public Wasteland2NPC[] addNpcAndFindLast(
            @RequestParam(value = "name", defaultValue = "npc1") String name,
            @RequestParam(value = "location", defaultValue = "location1") String location,
            @PathVariable("limit") long limit) {
        List<pl.mrasoft.springboottest.model.Wasteland2NPC> npcs = wasteland2Service
                .addNpcAndFindLast(new pl.mrasoft.springboottest.model.Wasteland2NPC(name, location), limit);

        return transformNpcsToDtos(npcs);
    }

    private Wasteland2Weapon[] transformWeaponsToDtos(List<pl.mrasoft.springboottest.model.Wasteland2Weapon> weapons) {
        Wasteland2Weapon[] weaponDtos = weapons.stream()
                .map(w -> new Wasteland2Weapon(w.getId(), w.getName(), w.getType()))
                .toArray(size -> new Wasteland2Weapon[size]);

        return weaponDtos;
    }

    private Wasteland2NPC[] transformNpcsToDtos(List<pl.mrasoft.springboottest.model.Wasteland2NPC> npcs) {
        Wasteland2NPC[] npcsDtos = npcs.stream()
                .map(w -> new Wasteland2NPC(w.getId(), w.getName(), w.getLocation()))
                .toArray(size -> new Wasteland2NPC[size]);

        return npcsDtos;
    }

}
