package org.leralix.tan.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;

public class RegionInvitationNL extends Newsletter {
    @Override
    public GuiItem createGuiItem(Player player) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        return false;
    }

    @Override
    public NewsletterType getType() {
        return null;
    }
}
