package eu.pb4.styledsidebars.config;

import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.styledsidebars.config.data.SidebarDefinition;

import java.util.ArrayList;
import java.util.List;

public class DefaultValues {
    public static SidebarDefinition EMPTY_STYLE = new SidebarDefinition();

    public static SidebarDefinition exampleStyleData() {
        SidebarDefinition data = new SidebarDefinition();
        data.configName = "Default";
        data.title = List.of(
                "<b><#66d1f2>Styled Sidebars");
        data.lines = List.of(
                SidebarDefinition.Line.of(""),
                SidebarDefinition.Line.of("<gray>» <white>Time:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:time%"),
                SidebarDefinition.Line.of(""),
                SidebarDefinition.Line.of("<gray>» <white>Players:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:online%</yellow>/<orange>%server:max_players%"),
                SidebarDefinition.Line.of(""),
                SidebarDefinition.Line.of("<gray>» <white>Ping:"),
                SidebarDefinition.Line.of("<gray> ▪ %player:ping_colored% <yellow>ms"),
                SidebarDefinition.Line.of(""),
                SidebarDefinition.Line.of("<red>Please change config in"),
                SidebarDefinition.Line.of("<red><underline>/config/styled-sidebars")
        );

        return data;
    }

    public static SidebarDefinition exampleStyleAnimatedData() {
        SidebarDefinition data = new SidebarDefinition();
        data.configName = "Scrolling example";
        data.title = List.of(
                "<b><dark_gray>→ <rb:1:1:0>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.05>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.1>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.15>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.2>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.25>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.3>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.35>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.4>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.45>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.5>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.55>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.6>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.65>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.7>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.75>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.8>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.85>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.9>Example Sidebar</rb> ←",
                "<b><dark_gray>→ <rb:1:1:0.95>Example Sidebar</rb> ←"
        );
        data.titleChange = 1;
        data.updateRate = 3;
        data.scrollSpeed = 2;
        data.scrollLoop = true;
        data.lines = List.of(
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<rb>Hello %player:name%"),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<gray>» <white>Time:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:time%"),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<gray>» <white>World:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%world:name%"),
                SidebarDefinition.Line.of("<gray> ▪ <orange>%world:time%"),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<gray>» <white>Players:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:online%</yellow>/<orange>%server:max_players%"),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<gray>» <white>Ping:"),
                SidebarDefinition.Line.of("<gray> ▪ %player:ping_colored% <yellow>ms"),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of("<gray>» <white>Position:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%player:pos_x% / %player:pos_y% / %player:pos_z% "),
                SidebarDefinition.Line.of("                                     "),
                SidebarDefinition.Line.of(List.of(
                        "<gray>» <white>TPS / MSPT:",
                        "<gray> ▪ <yellow>%server:tps_colored% / <orange>%server:mspt_colored% ms",
                        "                                     ",
                        "<gray>» <white>Memory:",
                        "<gray> ▪ <yellow>%server:used_ram% / <orange>%server:max_ram% ms"
                ), BuiltinPredicates.modPermissionApi("group.admin", 3))
        );

        return data;
    }

    public static SidebarDefinition examplePagesStyleData() {
        SidebarDefinition data = new SidebarDefinition();
        data.configName = "Paged example";
        data.require = BuiltinPredicates.modPermissionApi("example.permission", 2);
        data.title = List.of("<b><dark_gray>→ <gr:blue:white>Example Paged Sidebar</gr> ←");
        data.pages = new ArrayList<>();
        data.pages.add(List.of(
                SidebarDefinition.Line.of("<rb>Hello world on out MC server!"),
                SidebarDefinition.Line.of("Pls change configs!"),
                SidebarDefinition.Line.of("<gray>[1 / 3]")
        ));

        data.pages.add(List.of(
                SidebarDefinition.Line.of("<green>%server:mod_name styled-sidebars%"),
                SidebarDefinition.Line.of("<gray>%server:mod_description styled-sidebars%"),
                SidebarDefinition.Line.of("<gray>[2 / 3]")
        ));

        data.pages.add(List.of(
                SidebarDefinition.Line.of("<gray>» <white>Time:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:time%"),
                SidebarDefinition.Line.of(""),
                SidebarDefinition.Line.of("<gray>» <white>Players:"),
                SidebarDefinition.Line.of("<gray> ▪ <yellow>%server:online%</yellow>/<orange>%server:max_players%"),
                SidebarDefinition.Line.of("<gray>[3 / 3]")
        ));

        return data;
    }

    public static SidebarDefinition disabledStyleData() {
        var def = new SidebarDefinition();
        def.configName = "Disabled";
        def.updateRate = 9999;
        return def;
    }
}
