package eu.pb4.styledsidebars.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import eu.pb4.placeholders.api.parsers.StaticPreParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.styledsidebars.config.data.ConfigData;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class Config {
    public final ConfigData configData;
    public final TextNode switchMessage;
    public final Text unknownStyleMessage;


    public Config(ConfigData data) {
        this.configData = data;
        this.switchMessage = NodeParser.merge(TextParserV1.DEFAULT, new PatternPlaceholderParser(PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN, DynamicNode::of),
                        StaticPreParser.INSTANCE).parseNode(data.messages.switchMsg);
        this.unknownStyleMessage = TextParserV1.DEFAULT.parseText(data.messages.unknownStyle, ParserContext.of());
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return this.switchMessage.toText(ParserContext.of(DynamicNode.NODES, Map.of("style", Text.literal(target))));
    }
}
