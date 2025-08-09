package net.friaeren.pamlc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Pamlc implements ModInitializer {

    private int tickCounter = 0;

    @Override
    public void onInitialize() {
        System.out.println("[Pamlc] Hey we are working here... maybe...");

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
        // testing shit on god
        Identifier advancementId = new Identifier("prominent", "level_10"); // template: ...new Identifier("nomemod","path/nomeachivement")
        Identifier advancementIdAlt = new Identifier("bosses_of_mass_destruction", "adventure/night_lich_defeat"); // same thing as above
        Advancement advancement = server.getAdvancementLoader().get(advancementId); //carica l'achievement da client a server
        Advancement advancementAlt = server.getAdvancementLoader().get(advancementIdAlt); //same, ho messo anche l'alt per il livello 10 perché nelle quest ci sta

        System.out.println("[Pamlc] Tick..."); //print ogni volta che finisce il counter dei tick

        if (advancement == null || advancementAlt == null) { //catch se non trova l'achievement
            System.err.println("[Pamlc] Advancement not found: " + advancementId + " or this one, have fun figuring out " + advancementAlt);
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) { // loop per ogni player nel server
            boolean hasAdvancement = player.getAdvancementTracker().getProgress(advancement).isDone(); //check se ha l'achievement

            if (!hasAdvancement) { // se non lo ha
                // Run pufferfish command :D
                int levelreset = 10; //boh volevo fare un print nel server figo
                String command = String.format(
                        "puffish_skills experience set %s mining 1235", // !!! DA SOSTITUIRE "mining" CON LA CATEGORIA DELLE SKIL DELLA PROMINENCE (dovrebbe essere prom qualcosa, cercatela)
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
