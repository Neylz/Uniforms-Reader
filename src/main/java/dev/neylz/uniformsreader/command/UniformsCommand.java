package dev.neylz.uniformsreader.command;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.render.FogShape;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.rmi.UnexpectedException;
import java.text.DecimalFormat;
import java.util.HashMap;

import static dev.neylz.uniformsreader.util.AttributesGetter.filterNames;
import static dev.neylz.uniformsreader.util.AttributesGetter.getFields;


@Environment(EnvType.CLIENT)
public class UniformsCommand {

    private static final HashMap<String, String> UNIFORMS = new HashMap<>();
    static {
//        UNIFORMS.put("modelViewMatrix", "ModelViewMat");  // depends on the context (block or entity)
        UNIFORMS.put("shaderGlintAlpha", "GlintAlpha");
        UNIFORMS.put("shaderFogShape", "FogShape");
        UNIFORMS.put("shaderFogColor", "FogColor");
        UNIFORMS.put("shaderLightDirections", "LightDir");
        UNIFORMS.put("shaderFogStart", "FogStart");
        UNIFORMS.put("shaderFogEnd", "FogEnd");
        UNIFORMS.put("shaderGameTime", "GameTime");
        UNIFORMS.put("inverseViewRotationMatrix", "IViewRotMat");
        UNIFORMS.put("projectionMatrix", "ProjMat");

    }


    private static final DecimalFormat numberFormat = new DecimalFormat("0.#######");
    static {
        numberFormat.setMaximumFractionDigits(7);
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("uniforms")
                .executes(UniformsCommand::run)
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {

        HashMap<String, Object> map;
        try {
            map = parsesFields(filterNames(getFields(RenderSystem.class), UNIFORMS.keySet().toArray(new String[0])));
        } catch (UnexpectedException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }


        for (String key : map.keySet()) {
            context.getSource().sendFeedback(
                    Text.literal( key + ":\n").setStyle(Style.EMPTY.withColor(Formatting.GRAY) )
            .append(Text.literal( map.get(key).toString() ).setStyle(Style.EMPTY.withColor(Formatting.WHITE)) )
            );
        }

        return 1;
    }


    private static HashMap<String, Object> parsesFields(Field[] fields) throws IllegalAccessException, UnexpectedException {
        HashMap<String, Object> map = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);

            Object obj = field.get(null);

            // array
            if (obj.getClass().isArray()) {
                if (obj.getClass().getComponentType().equals(float.class)) {    // shaderFogColor
                    // convert to a Vector of the right size
                    switch (Array.getLength(obj)) {
                        case 1 -> map.put(UNIFORMS.get(field.getName()), Array.get(obj, 0));
                        case 2 -> map.put(UNIFORMS.get(field.getName()), new Vector2f((float[]) obj));
                        case 3 -> map.put(UNIFORMS.get(field.getName()), new Vector3f((float[]) obj));
                        case 4 -> map.put(UNIFORMS.get(field.getName()), new Vector4f((float[]) obj));
                        default -> throw new UnexpectedException("Unexpected array size: " + Array.getLength(obj));
                    }

                } else if (obj.getClass().getComponentType().equals(Vector3f.class)) {  // shaderLightDirections
                    Vector3f[] arr = (Vector3f[]) obj;
                    for (int i = 0; i < arr.length; i++) {
                        map.put(UNIFORMS.get(field.getName()) + "_" + i, arr[i]);
                    }


                } else {
                    throw new UnexpectedException("Unexpected array type: " + obj.getClass().getComponentType());
                }


            // cast fog shape to an int
            } else if (obj.getClass().equals(FogShape.class)) {
                map.put(UNIFORMS.get(field.getName()), ((FogShape) obj).getId());

            // classic object
            } else {
                map.put(UNIFORMS.get(field.getName()), obj);
            }
        }

        return map;

    }
}
