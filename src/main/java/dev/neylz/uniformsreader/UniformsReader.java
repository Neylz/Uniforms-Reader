package dev.neylz.uniformsreader;

import dev.neylz.uniformsreader.util.ModRegisteries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Environment(EnvType.CLIENT)
public class UniformsReader implements ModInitializer {
	public static final String MOD_ID = "uniformsreader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModRegisteries.register();

		LOGGER.info("Hello Fabric world!");



	}
}