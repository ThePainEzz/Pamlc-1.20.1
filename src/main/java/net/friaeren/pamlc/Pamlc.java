package net.friaeren.pamlc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import java.util.HashMap;

public class Pamlc implements ModInitializer {

    private int tickCounter = 0;
    private HashMap<Integer, Integer> levels;

    @Override
    public void onInitialize() {
        System.out.println("[Pamlc] Hey we are working here... maybe...");

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
        for (Integer i : levels.keySet()){
            // testing shit on god
            Identifier advancementId = new Identifier("prominent", "level_" + i); // template: ...new Identifier("nomemod","path/nomeachivement")
            // advancementIdAlt = new Identifier("bosses_of_mass_destruction", "adventure/night_lich_defeat"); // same thing as above
            Advancement advancement = server.getAdvancementLoader().get(advancementId); //carica l'achievement da client a server
            //Advancement advancementAlt = server.getAdvancementLoader().get(advancementIdAlt); //same, ho messo anche l'alt per il livello 10 perché nelle quest ci sta

            System.out.println("[Pamlc] Tick..."); //print ogni volta che finisce il counter dei tick

            if (advancement == null) { //catch se non trova l'achievement
                System.err.println("[Pamlc] Advancement not found: " + advancementId);
                return;
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) { // loop per ogni player nel server
                boolean hasAdvancement = player.getAdvancementTracker().getProgress(advancement).isDone(); //check se ha l'achievement

                if (!hasAdvancement) { // se non lo ha
                    // Run pufferfish command :D
                    int levelreset = 0; //boh volevo fare un print nel server figo

                    String command = String.format( //reset exp
                            "puffish_skills experience set %s prom 1235", // !!! DA SOSTITUIRE "mining" CON LA CATEGORIA DELLE SKIL DELLA PROMINENCE (dovrebbe essere prom qualcosa, cercatela)
                            player.getName().getString()
                    );
                    // Run command on server, admin
                    server.getCommandManager().executeWithPrefix(
                            player.getCommandSource().withLevel(4), // Level 4 = operator
                            command
                    );

                    System.out.println("[Pamlc] Reset " + player.getName().getString() + "'s skill XP to level " + levelreset); // print per i log del server
                }
            }
        }
    }
}
