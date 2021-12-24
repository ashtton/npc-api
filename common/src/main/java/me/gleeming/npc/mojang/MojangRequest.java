package me.gleeming.npc.mojang;

import lombok.SneakyThrows;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * We have to fetch mojang for skins
 * because whenever players are not
 * on the server, this is the only
 * way we have of fetching their skin
 */
public class MojangRequest {
    // Caches because speed and also mojang
    // has an api limit on requests that are
    // all coming from the same central ip
    private static final HashMap<String, UUID> cachedUUID = new HashMap<>();
    private static final HashMap<UUID, MojangSkin> cachedSkins = new HashMap<>();

    /**
     * Retrieves a players uuid from mojang api
     *
     * @param name Player Name
     * @return Fetched Name
     */
    @SneakyThrows
    public static UUID getUUID(String name) {
        if(cachedUUID.containsKey(name)) return cachedUUID.get(name);

        HttpURLConnection con = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        UUID uuid = UUID.fromString(Document.parse(response.toString()).getString("id"));
        cachedUUID.put(name, uuid);
        return uuid;
    }

    /**
     * Fetches a skin by UUID from mojang
     *
     * @param uuid Player Unique ID
     * @return Fetched Skin
     */
    @SneakyThrows
    public static MojangSkin getSkin(UUID uuid) {
        if(cachedSkins.containsKey(uuid)) return cachedSkins.get(uuid);

        HttpURLConnection con = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false").openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        Document profileDocument = Document.parse(response.toString());
        Document propertyDocument = ((List<Document>) profileDocument.get("properties")).get(0);

        MojangSkin fetchedSkin = new MojangSkin(propertyDocument.getString("value"), propertyDocument.getString("signature"));
        cachedSkins.put(uuid, fetchedSkin);
        return fetchedSkin;
    }

    /**
     * Fetches a skin by Name from mojang
     *
     * @param name Player Name
     * @return Fetched Skin
     */
    public static MojangSkin getSkin(String name) {
        return getSkin(getUUID(name));
    }
}
