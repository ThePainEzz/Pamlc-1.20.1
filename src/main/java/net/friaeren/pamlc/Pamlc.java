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
            if (tickCounter >= 2400) {
                tickCounter = 0;
                checkPlayers(server);
            }
        });
    }

    private void checkPlayers(MinecraftServer server) {
        // Replace with the advancement you care about
        Identifier advancementId = new Identifier("minecraft", "end/kill_dragon");
        Advancement advancement = server.getAdvancementLoader().get(advancementId);

        if (advancement == null) {
            System.err.println("[Pamlc] Advancement not found: " + advancementId);
            return;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean hasAdvancement = player.getAdvancementTracker().getProgress(advancement).isDone();

            if (!hasAdvancement) {
                // Run the Pufferfish Skills command
                int levelreset = 10;
                String command = String.format(
                        "puffish_skills experience set %s mining 1235",
                        player.getName().getString()
                );
                server.getCommandManager().executeWithPrefix(
                        player.getCommandSource().withLevel(4), // Level 4 = operator
                        command
                );

                System.out.println("[Pamlc] Reset " + player.getName().getString() + "'s skill XP to level " + levelreset);
            }
        }
    }
}
