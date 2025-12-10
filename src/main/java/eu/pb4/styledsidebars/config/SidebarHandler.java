package eu.pb4.styledsidebars.config;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.StaticPreParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.styledsidebars.config.data.SidebarDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;

public class SidebarHandler {
    public static final SidebarHandler FALLBACK = new SidebarHandler("", DefaultValues.EMPTY_STYLE);

    public final SidebarDefinition definition;
    public final String id;
    public List<TextNode> title;
    @Nullable
    public List<Line> lines;
    @Nullable
    public List<List<Line>> pages;

    public SidebarHandler(String id, SidebarDefinition definition) {
        definition.validate();
        this.definition = definition;
        this.id = id;
        this.updateLines();
    }

    public void updateLines() {
        this.title = definition.title.stream().map(Config.PARSER::parseNode).toList();
        if (this.title.isEmpty()) {
            this.title = List.of(TextNode.empty());
        }

        if (this.definition.lines != null) {
            this.lines = this.definition.lines.stream().map(SidebarHandler::toLine).toList();
            this.pages = null;
        } else if (this.definition.pages != null) {
            this.pages = this.definition.pages.stream().map(x -> x.stream().map(SidebarHandler::toLine).toList()).toList();
            this.lines = null;
        } else {
            this.lines = List.of();
            this.pages = null;
        }
    }

    private static Line toLine(SidebarDefinition.Line line) {
        return new Line(line.values().stream().map(x -> new Tuple<>(Config.PARSER.parseNode(x.getA()), Config.PARSER.parseNode(x.getB()))).toList(), line.require());
    }

    public boolean hasPermission(ServerPlayer player) {
        return this.definition.require == null || this.definition.require.test(PredicateContext.of(player)).success();
    }

    public boolean isEmpty() {
        return (this.lines != null && this.lines.isEmpty()) || (this.pages != null && this.pages.isEmpty()) || (this.pages == null && this.lines == null);
    }

    public record Line(List<Tuple<TextNode, TextNode>> values, MinecraftPredicate predicate) {
        public boolean hasPermission(ServerPlayer player) {
            return this.predicate == null || this.predicate.test(PredicateContext.of(player)).success();
        }
    }
}
