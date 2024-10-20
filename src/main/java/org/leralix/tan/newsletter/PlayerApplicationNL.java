package org.leralix.tan.newsletter;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;

public class PlayerApplicationNL extends Newsletter {

    String playerID;
    String townID;

    public PlayerApplicationNL(Player player, TownData townData) {
        super();
        playerID = player.getUniqueId().toString();
        townID = townData.getID();
    }

    @Override
    public GuiItem createGuiItem(Player player) {
        ItemStack itemStack = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.NEWSLETTER_PLAYER_APPLICATION.get(),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get());

        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            PlayerGUI.openTownApplications(player);
            event.setCancelled(true);
        });
    }
}
