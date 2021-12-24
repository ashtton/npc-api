package me.gleeming.npc.skin;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
public class Skin {

    private final UUID uuid;
    private final String name;

    public Skin() {
        this.uuid = UUID.fromString("b28389ef-a416-4bc0-ae6a-7a3a18c8bcc5");
        this.name = null;
    }

    public Skin(UUID uuid) {
        this.uuid = uuid;
        this.name = null;
    }

    public Skin(String name) {
        this.uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        this.name = null;
    }

    public Skin(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
