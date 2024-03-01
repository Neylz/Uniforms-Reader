package dev.neylz.uniformsreader;

import net.fabricmc.api.ClientModInitializer;

public class UniformsReaderClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("Hello Fabric world! (Client side)");
    }
}
