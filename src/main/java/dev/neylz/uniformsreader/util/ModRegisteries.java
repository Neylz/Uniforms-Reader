package dev.neylz.uniformsreader.util;

import dev.neylz.uniformsreader.command.UniformsCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;




public class ModRegisteries {

    public static void register() {
        registerClientCommands();
    }


    private static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register(UniformsCommand::register);
    }
}
