package eu.pb4.styledsidebars.config;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledsidebars.config.data.ConfigData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Config {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(?<id>[^}]+)}");

    public final ConfigData configData;
    public final TextNode switchMessage;
    public final Text unknownStyleMessage;


    public Config(ConfigData data) {
        this.configData = data;
        this.switchMessage = Placeholders.parseNodes(TextParserUtils.formatNodes(data.messages.switchMsg));
        this.unknownStyleMessage = TextParserUtils.formatText(data.messages.unknownStyle);
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return Placeholders.parseText(this.switchMessage, PLACEHOLDER_PATTERN, Map.of("style", Text.literal(target)));
    }
}
