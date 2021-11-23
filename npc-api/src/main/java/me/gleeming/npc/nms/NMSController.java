package me.gleeming.npc.nms;

import lombok.Getter;
import org.bukkit.Bukkit;

@SuppressWarnings("unchecked")
public class NMSController {

    @Getter private Class<? extends NPCFakePlayer> nmsFakePlayer;
    @Getter private Class<?> nmsInjector;

    public NMSController() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1);
        String npcPackage = "me.gleeming.npc." + version;

        try {
            nmsInjector = Class.forName(npcPackage + ".NMSInjector");
            nmsFakePlayer = (Class<? extends NPCFakePlayer>) Class.forName(npcPackage + ".NMSFakePlayer");
            System.out.println("[NPC] Successfully loaded the NMS classes for version '" + version + "'.");
        } catch(ClassNotFoundException ex) {
            System.out.println("[NPC] There was an error while loading the NMS classes for version '" + version + "'. Are you sure it's supported?");
        }
    }
}
