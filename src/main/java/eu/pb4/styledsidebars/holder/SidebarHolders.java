package eu.pb4.styledsidebars.holder;

import eu.pb4.sidebars.interfaces.SidebarHolder;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public interface SidebarHolders {

    static SidebarHolder of(final ServerPlayNetworkHandler handler) {
        return (SidebarHolder) handler;
    }

}