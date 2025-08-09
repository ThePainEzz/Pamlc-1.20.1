package net.friaeren.pamlc.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class CommandOutputCatcher {

    public static String executeAndCapture(MinecraftServer server, String command) {
        // Lista per memorizzare tutte le righe di output
        List<String> outputLines = new ArrayList<>();

        // Creiamo un CommandSource personalizzato
        ServerCommandSource source = new ServerCommandSource(
                new CommandOutput() {
                    @Override
                    public void sendMessage(Text message) {
                        outputLines.add(message.getString());
                    }

                    @Override
                    public boolean shouldReceiveFeedback() {
                        return true;
                    }

                    @Override
                    public boolean shouldTrackOutput() {
                        return true;
                    }

                    @Override
                    public boolean shouldBroadcastConsoleToOps() {
                        return false;
                    }
                },
                server.getOverworld().getSpawnPos().toCenterPos(),
                Vec2f.ZERO,
                server.getOverworld(),
                4, // Livello permessi OP
                "Console",
                Text.literal("Console"),
                server,
                null // Nessun giocatore associato
        );

        // Eseguiamo il comando
        server.getCommandManager().executeWithPrefix(source, command);

        // Ritorniamo tutto unito in una sola stringa
        return String.join("\n", outputLines);
    }
}