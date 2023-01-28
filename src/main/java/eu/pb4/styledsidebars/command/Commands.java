package eu.pb4.styledsidebars.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.sidebars.api.SidebarUtils;
import eu.pb4.styledsidebars.CustomSidebar;
import eu.pb4.styledsidebars.GenericModInfo;
import eu.pb4.styledsidebars.ModInit;
import eu.pb4.styledsidebars.config.ConfigManager;
import eu.pb4.styledsidebars.config.SidebarHandler;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("styledsidebars")
                            .requires(Permissions.require("styledsidebars.main", true))
                            .executes(Commands::about)
                            .then(literal("switch")
                                    .requires(Permissions.require("styledsidebars.switch", true))
                                    .then(switchArgument("style")
                                            .executes(Commands::switchStyle)
                                    )
                            )

                            .then(literal("switchothers")
                                    .requires(Permissions.require("styledsidebars.switch.others", 2))
                                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .then(switchArgument("style")
                                                    .executes(Commands::switchStyleOthers)
                                            )
                                    )
                            )

                            .then(literal("reload")
                                    .requires(Permissions.require("styledsidebars.reload", 3))
                                    .executes(Commands::reloadConfig)
                            )
            );

            dispatcher.register(
                    literal("sidebar")
                            .requires(Permissions.require("styledsidebars.switch", true))
                            .then(switchArgument("style")
                                    .executes(Commands::switchStyle)
                            )
            );

        });
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(Text.literal("Reloaded config!"), false);
            for (var entry : ModInit.SIDEBARS.entrySet()) {
                var type = PlayerDataApi.getGlobalDataFor(entry.getKey().player, ModInit.STORAGE);
                String id = ConfigManager.getDefault();
                if (type instanceof NbtString nbtString) {
                    id = nbtString.asString();
                }

                var def = ConfigManager.getStyle(id);

                entry.getValue().setStyle(def != null && def.hasPermission(entry.getKey().player) ? def : null);
            }
        } else {
            context.getSource().sendError(Text.literal("Error occrued while reloading config!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        for (var text : (context.getSource().getEntity() instanceof ServerPlayerEntity ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole())) {
            context.getSource().sendFeedback(text, false);
        }

        return 1;
    }

    public static int switchStyleOthers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String styleId = context.getArgument("style", String.class);
        Collection<ServerPlayerEntity> target = EntityArgumentType.getPlayers(context, "targets");

        if (!ConfigManager.styleExist(styleId)) {
            source.sendFeedback(ConfigManager.getConfig().unknownStyleMessage, false);
            return 0;
        }

        SidebarHandler style = ConfigManager.getStyle(styleId);

        for (ServerPlayerEntity player : target) {
            var sidebar = ModInit.SIDEBARS.get(player.networkHandler);
            sidebar.setStyle(style != null && style.hasPermission(player) ? style : null);
            PlayerDataApi.setGlobalDataFor(player, ModInit.STORAGE, NbtString.of(styleId));
        }

        source.sendFeedback(Text.literal("Changed sidebar of targets to " + style.definition.configName), false);


        return 2;
    }

    private static int switchStyle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ServerCommandSource source = context.getSource();
            String styleId = context.getArgument("style", String.class);

            if (!ConfigManager.styleExist(styleId)) {
                source.sendFeedback(ConfigManager.getConfig().unknownStyleMessage, false);
                return 0;
            }

            SidebarHandler style = ConfigManager.getStyle(styleId);
            ServerPlayerEntity player = source.getPlayer();

            if (player != null && player instanceof ServerPlayerEntity) {
                if (style != null && style.hasPermission(player)) {
                    var sidebar = ModInit.SIDEBARS.get(player.networkHandler);
                    sidebar.setStyle(style);
                    PlayerDataApi.setGlobalDataFor(player, ModInit.STORAGE, NbtString.of(styleId));

                    source.sendFeedback(ConfigManager.getConfig().getSwitchMessage(player, style.definition.configName), false);
                    return 1;
                } else {
                    source.sendFeedback(ConfigManager.getConfig().unknownStyleMessage, false);
                }
            } else {
                source.sendFeedback(Text.literal("Only players can use this command!"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> switchArgument(String name) {
        return CommandManager.argument(name, StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                    for (SidebarHandler style : ConfigManager.getStyles()) {
                        if (style.id.contains(remaining) && style.hasPermission(ctx.getSource().getPlayerOrThrow())) {
                            builder.suggest(style.id);
                        }
                    }

                    return builder.buildFuture();
                });
    }


}
