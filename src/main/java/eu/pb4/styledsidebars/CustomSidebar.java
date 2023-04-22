package eu.pb4.styledsidebars;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.SidebarInterface;
import eu.pb4.sidebars.api.SidebarUtils;
import eu.pb4.sidebars.api.lines.SidebarLine;
import eu.pb4.styledsidebars.config.SidebarHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomSidebar implements SidebarInterface {
    private SidebarHandler handler;
    public final ServerPlayNetworkHandler player;
    private int pos = 0;
    private int page = 0;
    private int title = 0;

    public CustomSidebar(SidebarHandler handler, ServerPlayNetworkHandler player) {
        this.handler = handler;
        this.player = player;

        if (this.isActive()) {
            SidebarUtils.addSidebar(player, this);
        }
    }

    @Override
    public Sidebar.Priority getPriority() {
        return Sidebar.Priority.LOW;
    }

    @Override
    public int getUpdateRate() {
        return this.isActive() ? this.handler.definition.updateRate : 20;
    }

    @Override
    public Text getTitleFor(ServerPlayNetworkHandler handler) {
        var title = (this.title / this.handler.definition.titleChange);

        if (title >= this.handler.title.size()) {
            title = 0;
            this.title = 0;
        }

        this.title++;

        return this.handler.title.get(title).toText(PlaceholderContext.of(handler.player).asParserContext(), true);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public List<SidebarLine> getLinesFor(ServerPlayNetworkHandler handler) {
        List<TextNode> list = new ArrayList<>();

        {
            List<SidebarHandler.Line> lines;

            if (this.handler.lines != null) {
                lines = this.handler.lines;
            } else {
                var page = (this.page / this.handler.definition.pageChange);

                if (page >= this.handler.pages.size()) {
                    page = 0;
                    this.page = 0;
                }

                lines = this.handler.pages.get(page);
                this.page++;
            }

            for (var line : lines) {
                if (line.hasPermission(handler.player)) {
                    for (var node : line.textNode()) {
                        list.add(node);
                    }
                }
            }
        }

        if (list.size() > 14) {
            this.pos++;
            int index = this.pos / this.handler.definition.scrollSpeed;

            if (this.handler.definition.scrollLoop) {
                if (index >= list.size()) {
                    this.pos = 0;
                }
                var looping = new ArrayList<TextNode>(list.size() * 2);
                looping.addAll(list);
                looping.addAll(list);
                list = looping.subList(index, index + 14);
            } else {
                if (index + 14 > list.size()) {
                    this.pos = 0;
                    index = 0;
                }

                list = list.subList(index, Math.min(index + 14, list.size()));
            }
        }

        var out = new ArrayList<SidebarLine>(list.size());

        var context = PlaceholderContext.of(handler.player).asParserContext();
        int size = list.size();
        for (var node : list) {
            out.add(SidebarLine.create(--size, node.toText(context)));
        }
        return out;
    }

    @Override
    public boolean isActive() {
        return this.handler != null && !this.handler.isEmpty();
    }

    @Override
    public void disconnected(ServerPlayNetworkHandler handler) {}

    public void setStyle(SidebarHandler handler) {
        if (this.handler == handler) {
            return;
        }

        this.pos = 0;
        this.title = 0;
        this.page = 0;
        if (this.isActive()) {
            this.handler = handler;
            if (handler == null) {
                SidebarUtils.removeSidebar(this.player, this);
            } else {
                SidebarUtils.updateTexts(this.player, this);
            }
        } else {
            this.handler = handler;
            if (handler != null) {
                SidebarUtils.addSidebar(this.player, this);
                SidebarUtils.requestStateUpdate(this.player);
                SidebarUtils.updateTexts(this.player, this);
            }
        }
    }
}
