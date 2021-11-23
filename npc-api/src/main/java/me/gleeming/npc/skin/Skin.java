package me.gleeming.npc.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.gleeming.npc.mojang.MojangRequest;
import me.gleeming.npc.mojang.MojangSkin;
import org.bukkit.Bukkit;

import java.util.UUID;

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

    /**
     * Converts the values to a game profile
     * @return Game Profile
     */
    public GameProfile toGameProfile(boolean displayCosmetics) {
        UUID uuid = displayCosmetics ? this.uuid : UUID.randomUUID();
        String name = this.name == null ? UUID.randomUUID().toString().substring(0, 15) : this.name;

        GameProfile gameProfile = new GameProfile(uuid, name);

        MojangSkin skin;
        if(uuid != null) skin = MojangRequest.getSkin(uuid);
        else skin = MojangRequest.getSkin(name);

        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        return gameProfile;
    }
}
