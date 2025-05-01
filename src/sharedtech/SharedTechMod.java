package sharedtech;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.net.Administration.*;
import mindustry.net.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.CoreBlock.*;

public class SharedTechMod extends Mod {
    
    // Settings
    private boolean debugMode = false;
    
    public SharedTechMod() {
        Log.info("SharedTechMod constructor loaded");
        
        // Register event handlers when game is loaded
        Events.on(ServerLoadEvent.class, e -> {
            Log.info("SharedTechMod: Server loaded, initializing shared tech system");
            initServer();
        });
        
        // Handle when players join
        Events.on(PlayerJoin.class, event -> {
            if (!Vars.net.server()) return;
            
            Player player = event.player;
            Log.info("Player joined: " + player.name);
            
            // Short delay to ensure player data is fully loaded
            Time.runTask(30f, () -> {
                syncTechTreeToPlayer(player);
                syncBlueprintsToPlayer(player);
            });
        });
        
        // Listen for research events to sync to all players
        Events.on(ResearchEvent.class, event -> {
            if (!Vars.net.server()) return;
            
            UnlockableContent content = event.content;
            if (debugMode) Log.info("Research detected: " + content.name);
            
            // Broadcast research to all players
            broadcastResearch(content);
        });
        
        // Handle when a player unlocks a schematic
        Events.on(SchematicCreateEvent.class, event -> {
            if (!Vars.net.server()) return;
            
            broadcastSchematic(event.schematic);
        });
    }

    @Override
    public void init() {
        // Register server commands for administration
        Vars.netServer.admins.addActionFilter(action -> {
            // Allow all actions through - just here for extensibility
            return true;
        });
        
        // Register command to manually sync tech tree
        Vars.netServer.registerCommand("synctechall", "Synchronize tech tree to all players", args -> {
            syncTechTreeToAllPlayers();
            return "Tech tree synchronized to all players.";
        });
        
        // Register command to toggle debug mode
        Vars.netServer.registerCommand("techmoddebug", "Toggle debug mode for SharedTechMod", args -> {
            debugMode = !debugMode;
            return "Debug mode: " + (debugMode ? "ON" : "OFF");
        });
    }
    
    private void initServer() {
        if (!Vars.net.server()) return;
        
        Log.info("SharedTechMod: Server initialization");
        
        // Setup auto-sync timer to periodically ensure all players are in sync
        Timer.schedule(() -> {
            if (Vars.net.active() && Vars.state.isGame()) {
                if (debugMode) Log.info("Running tech sync timer...");
                syncTechTreeToAllPlayers();
                syncBlueprintsToAllPlayers();
            }
        }, 60f, 300f); // First run after 60 seconds, then every 5 minutes
    }
    
    private void syncTechTreeToAllPlayers() {
        if (!Vars.net.server()) return;
        
        // Get all researched tech from server state
        for (UnlockableContent content : Vars.content.items()) {
            if (content.unlocked()) {
                broadcastResearch(content);
            }
        }
        
        for (UnlockableContent content : Vars.content.blocks()) {
            if (content.unlocked()) {
                broadcastResearch(content);
            }
        }
        
        for (UnlockableContent content : Vars.content.units()) {
            if (content.unlocked()) {
                broadcastResearch(content);
            }
        }
        
        if (debugMode) Log.info("Tech tree sync completed for all players");
    }
    
    private void syncTechTreeToPlayer(Player player) {
        if (!Vars.net.server()) return;
        
        if (debugMode) Log.info("Syncing tech tree to player: " + player.name);
        
        // Send all researched content to the specific player
        for (UnlockableContent content : Vars.content.items()) {
            if (content.unlocked()) {
                sendResearchToPlayer(content, player);
            }
        }
        
        for (UnlockableContent content : Vars.content.blocks()) {
            if (content.unlocked()) {
                sendResearchToPlayer(content, player);
            }
        }
        
        for (UnlockableContent content : Vars.content.units()) {
            if (content.unlocked()) {
                sendResearchToPlayer(content, player);
            }
        }
    }
    
    private void broadcastResearch(UnlockableContent content) {
        if (!Vars.net.server()) return;
        
        Call.researchComplete(content);
        
        if (debugMode) Log.info("Broadcasting research: " + content.name);
    }
    
    private void sendResearchToPlayer(UnlockableContent content, Player player) {
        if (!Vars.net.server()) return;
        
        // Send a packet specifically to this player to unlock the content
        if (Vars.net.server() && player.con != null) {
            Call.clientPacketReliable(player.con, "researchComplete", content.name);
            
            if (debugMode) Log.info("Sent research to " + player.name + ": " + content.name);
        }
    }
    
    private void syncBlueprintsToAllPlayers() {
        if (!Vars.net.server()) return;
        
        // Share all schematics with all players
        if (Vars.schematics != null && Vars.schematics.all() != null) {
            Vars.schematics.all().each(this::broadcastSchematic);
        }
        
        if (debugMode) Log.info("Blueprint sync completed for all players");
    }
    
    private void syncBlueprintsToPlayer(Player player) {
        if (!Vars.net.server()) return;
        
        if (debugMode) Log.info("Syncing blueprints to player: " + player.name);
        
        // Send all schematics to the specific player
        if (Vars.schematics != null && Vars.schematics.all() != null) {
            Vars.schematics.all().each(schematic -> {
                sendSchematicToPlayer(schematic, player);
            });
        }
    }
    
    private void broadcastSchematic(Schematic schematic) {
        if (!Vars.net.server()) return;
        
        // This is a simplified version - in a real implementation,
        // you would need to properly serialize the schematic
        // and send it to all clients
        
        if (debugMode) Log.info("Broadcasting schematic: " + schematic.name());
        
        for (Player player : Groups.player) {
            sendSchematicToPlayer(schematic, player);
        }
    }
    
    private void sendSchematicToPlayer(Schematic schematic, Player player) {
        if (!Vars.net.server() || player.con == null) return;
        
        // This would need actual implementation using the Mindustry API
        // to properly send schematics to clients
        Call.clientPacketReliable(player.con, "schematicReceive", schematic);
        
        if (debugMode) Log.info("Sent schematic to " + player.name + ": " + schematic.name());
    }
}
