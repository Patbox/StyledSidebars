package eu.pb4.styledsidebars;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.styledsidebars.command.Commands;
import eu.pb4.styledsidebars.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModInit implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Styled Sidebars");
    public static final String ID = "styled-sidebars";


    public static final Map<ServerPlayNetworkHandler, CustomSidebar> SIDEBARS = new HashMap<>();

    public static final Identifier STORAGE = new Identifier("styled-sidebars","selected");

    @Override
    public void onInitialize() {
        try {
            GenericModInfo.build(FabricLoader.getInstance().getModContainer(ID).get());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Commands.register();
        ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
            CardboardWarning.checkAndAnnounce();
            ConfigManager.loadConfig();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var type = PlayerDataApi.getGlobalDataFor(handler.player, STORAGE);
            String id = ConfigManager.getDefault();
            if (type instanceof NbtString nbtString) {
                id = nbtString.asString();
            }

            var def = ConfigManager.getStyle(id);

            var sidebar = new CustomSidebar(def != null && def.hasPermission(handler.player) ? def : null, handler);
            SIDEBARS.put(handler, sidebar);
        });

        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            SIDEBARS.remove(handler);
        }));
    }

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }
}
