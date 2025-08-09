package net.friaeren.pamlc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.friaeren.pamlc.util.CommandOutputCatcher;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pamlc implements ModInitializer {

    private int tickCounter = 0;
    private HashMap<Integer, Integer> levels;
    private Logger logger;

    @Override
    public void onInitialize() {
        System.out.println("[Pamlc] Hey we are working here... maybe...");
        logger = Logger.getLogger(this.getClass().getName());

        levels = new HashMap<Integer, Integer>();
        levels.put(10, 1235);
        levels.put(15, 2253);
        levels.put(20, 3637);
        levels.put(25, 5472);
        levels.put(30, 7965);
        levels.put(35, 11272);
        levels.put(40, 15765);
        levels.put(50, 29823);



        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // 20 ticks = 1 second, so 2400 ticks = 120 seconds
            if (tickCounter >= 15*20) { // ho messo 15 sec per testare in game rq
                tickCounter = 0;
                checkPlayers(server);
            }
        });
    }

    private void checkPlayers(MinecraftServer server) {

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) { // loop per ogni player nel server

            for (Integer i : levels.keySet().stream().sorted().toList()) {
                // testing shit on god
                Identifier advancementId = new Identifier("pamlc", "level_" + i); // template: ...new Identifier("nomemod","path/nomeachivement")
                Advancement advancement = server.getAdvancementLoader().get(advancementId); //carica l'achievement da client a server

                //System.out.println("[Pamlc] Checking level " + i + " for " + player.getName().getString());

                //if (advancement == null) { //catch se non trova l'achievement
                //    System.err.println("[Pamlc] Advancement not found: " + advancementId);
                //    continue;
                //}


                boolean hasAdvancement = player.getAdvancementTracker().getProgress(advancement).isDone(); //check se ha l'achievement

                String command = String.format( //reset exp
                        "puffish_skills experience get %s puffish_skills:prom",
                        player.getName().getString()
                );

                int currentExp = getExp(server, command);
                if (!hasAdvancement && currentExp>levels.get(i)) { // se non lo ha
                    // Run pufferfish command :D

                    //logger.log(Level.INFO, "XP del player: " + currentExp);

                    String resetCommand = String.format(
                            "puffish_skills experience set %s puffish_skills:prom %d",
                            player.getName().getString(), levels.get(i)
                    );

                    server.getCommandManager().executeWithPrefix(
                            player.getCommandSource().withLevel(4).withSilent(), // op admin level
                            resetCommand
                    );

                    System.out.println("[Pamlc] Reset " + player.getName().getString() + "'s skill XP to level " + i); // print per i log del server

                    break;
                }
            }
        }
    }

    private int getExp(MinecraftServer server, String command) {
        String out = CommandOutputCatcher.executeAndCapture(server, command);
        logger.log(Level.INFO, out);
        // TODO: manipolare la stringa in modo da ottenere l'exp
        String[] parts = out.split(" ");
        return Integer.parseInt(parts[parts.length-5]);
    }
}
