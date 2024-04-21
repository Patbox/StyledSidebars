package eu.pb4.styledsidebars.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.DynamicTextNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import eu.pb4.styledsidebars.config.data.ConfigData;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class Config {
    public static final ParserContext.Key<Function<String, @Nullable Text>> KEY = DynamicTextNode.key("styled_sidebars");
    public static final NodeParser DYNAMIC_PARSER = NodeParser.builder()
            .globalPlaceholders()
            .simplifiedTextFormat()
            .quickText()
            .placeholders(TagLikeParser.PLACEHOLDER_USER, KEY)
            .staticPreParsing()
            .build();

    public static final NodeParser PARSER = NodeParser.builder()
            .globalPlaceholders()
            .simplifiedTextFormat()
            .quickText()
            .staticPreParsing()
            .build();

    public final ConfigData configData;
    public final TextNode switchMessage;
    public final Text unknownStyleMessage;


    public Config(ConfigData data) {
        this.configData = data;
        this.switchMessage = DYNAMIC_PARSER.parseNode(data.messages.switchMsg);
        this.unknownStyleMessage = PARSER.parseText(data.messages.unknownStyle, ParserContext.of());
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return this.switchMessage.toText(ParserContext.of(KEY, Map.of("style", Text.literal(target))::get));
    }
}
