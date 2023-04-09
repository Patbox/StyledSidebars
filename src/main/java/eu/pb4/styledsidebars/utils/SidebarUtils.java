package eu.pb4.styledsidebars.utils;

import eu.pb4.sidebars.api.ScrollableSidebar;
import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.lines.LineBuilder;
import eu.pb4.styledsidebars.holder.SidebarHolders;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public final class SidebarUtils {

    public static Sidebar create(final Text text, final Sidebar.Priority priority, final Consumer<LineBuilder> lineBuilderConsumer) {
        var sidebar = new Sidebar(text, priority);
        sidebar.set(lineBuilderConsumer);
        return sidebar;
    }

    public static ScrollableSidebar createScrolling(final Text text, final Sidebar.Priority priority, final int speed, final Consumer<LineBuilder> lineBuilderConsumer) {
        var sidebar = new ScrollableSidebar(text, priority, speed);
        sidebar.set(lineBuilderConsumer);
        return sidebar;
    }

    public static void updateTexts(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        if (isVisible(handler, sidebar)) {
            SidebarHolders.of(handler).updateCurrentSidebar(sidebar);
        }
    }

    public static boolean isVisible(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        return SidebarHolders.of(handler).getCurrentSidebar() == sidebar;
    }

    public static void updatePriorities(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        SidebarHolders.of(handler).updateCurrentSidebar(sidebar);
    }

    public static void requestStateUpdate(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        SidebarHolders.of(handler).updateCurrentSidebar(sidebar);
    }

    public static void addSidebar(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        if (!SidebarHolders.of(handler).getSidebarSet().contains(sidebar)) {
            SidebarHolders.of(handler).addSidebar(sidebar);
        }
    }

    public static void removeSidebar(final ServerPlayNetworkHandler handler, final Sidebar sidebar) {
        if (SidebarHolders.of(handler).getSidebarSet().contains(sidebar)) {
            SidebarHolders.of(handler).removeSidebar(sidebar);
        }
    }
}