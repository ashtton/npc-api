# NPC API
This is a lightweight packet based npc library made for spigot\
Lots more features are planned for this library, this is only a pre-release.
### Config Options
* Default Players - The players the npc is shown to by default
* Skin - The skin that the NPC will use
* Nametag Visible - Whether or not the npc will show a nametag
* Listener - The interaction listener for the npc
* Show to all - Whether or not everyone on the server can see the npc
### Example
```java
// Package: me.gleeming.example
public class Example extends JavaPlugin {
    public void onEnable() {
        // Spawns a npc that the whole server
        // can see that strikes lightning
        new NPC(Location, new NPCConfiguration()
                .skin(new Skin("Thor"))
                .listener(player -> {
                    player.getWorld().strikeLightningEffect(player.getLocation());
                })
        );
    }
}

// Package: me.gleeming.example.listener
public class ExampleListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Spawns a npc with the own players
        // skin that only that player can see
        new NPC(player.getLocation(), new NPCConfiguration()
                .skin(new Skin(player.getUniqueId()))
                .addPlayer(player)   
                .listener(clickedPlayer -> {
                    clickedPlayer.sendMessage("You clicked your NPC!");
                })
        );
    }
}
```
