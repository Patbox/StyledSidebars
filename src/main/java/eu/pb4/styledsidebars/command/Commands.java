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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import java.util.Locale;

import static net.minecraft.commands.Commands.literal;

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
                                    .then(net.minecraft.commands.Commands.argument("targets", EntityArgument.players())
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

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        if (ConfigManager.loadConfig(context.getSource().registryAccess())) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!"), false);
            for (var entry : ModInit.SIDEBARS.entrySet()) {
                var type = PlayerDataApi.getGlobalDataFor(entry.getKey().player, ModInit.STORAGE);
                String id = ConfigManager.getDefault();
                if (type instanceof StringTag nbtString) {
                    id = nbtString.value();
                }

                var def = ConfigManager.getStyle(id);

                entry.getValue().setStyle(def != null && def.hasPermission(entry.getKey().player) ? def : null);
            }
        } else {
            context.getSource().sendFailure(Component.literal("Error occrued while reloading config!").withStyle(ChatFormatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        for (var text : (context.getSource().getEntity() instanceof ServerPlayer ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole())) {
            context.getSource().sendSuccess(() -> text, false);
        }

        return 1;
    }

    public static int switchStyleOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String styleId = context.getArgument("style", String.class);
        Collection<ServerPlayer> target = EntityArgument.getPlayers(context, "targets");

        if (!ConfigManager.styleExist(styleId)) {
            source.sendSuccess(() -> ConfigManager.getConfig().unknownStyleMessage, false);
            return 0;
        }

        SidebarHandler style = ConfigManager.getStyle(styleId);

        for (ServerPlayer player : target) {
            var sidebar = ModInit.SIDEBARS.get(player.connection);
            sidebar.setStyle(style != null && style.hasPermission(player) ? style : null);
            PlayerDataApi.setGlobalDataFor(player, ModInit.STORAGE, StringTag.valueOf(styleId));
        }

        source.sendSuccess(() -> Component.literal("Changed sidebar of targets to " + style.definition.configName), false);


        return 2;
    }

    private static int switchStyle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            CommandSourceStack source = context.getSource();
            String styleId = context.getArgument("style", String.class);

            if (!ConfigManager.styleExist(styleId)) {
                source.sendSuccess(() -> ConfigManager.getConfig().unknownStyleMessage, false);
                return 0;
            }

            SidebarHandler style = ConfigManager.getStyle(styleId);
            ServerPlayer player = source.getPlayer();

            if (player != null && player instanceof ServerPlayer) {
                if (style != null && style.hasPermission(player)) {
                    var sidebar = ModInit.SIDEBARS.get(player.connection);
                    sidebar.setStyle(style);
                    PlayerDataApi.setGlobalDataFor(player, ModInit.STORAGE, StringTag.valueOf(styleId));

                    source.sendSuccess(() -> ConfigManager.getConfig().getSwitchMessage(player, style.definition.configName), false);
                    return 1;
                } else {
                    source.sendSuccess(() -> ConfigManager.getConfig().unknownStyleMessage, false);
                }
            } else {
                source.sendSuccess(() -> Component.literal("Only players can use this command!"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> switchArgument(String name) {
        return net.minecraft.commands.Commands.argument(name, StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                    for (SidebarHandler style : ConfigManager.getStyles()) {
                        if (style.id.contains(remaining) && style.hasPermission(ctx.getSource().getPlayerOrException())) {
                            builder.suggest(style.id);
                        }
                    }

                    return builder.buildFuture();
                });
    }


}
