package org.leralix.tan.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.enums.Action;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;

import java.util.function.Consumer;

public interface IGUI {


    static Gui createChestGui(String name, int nRow) {
        return Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();
    }
    static GuiItem createBackArrow(Player player, Consumer<Player> openMenuAction) {
        ItemStack getBackArrow = HeadUtils.createCustomItemStack(Material.ARROW, Lang.GUI_BACK_ARROW.get());
        return ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            openMenuAction.accept(player);
        });
    }

    static ItemStack getDecorativeGlass(Action action) {
        if (action == Action.ADD)
            return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        else
            return new ItemStack(Material.RED_STAINED_GLASS_PANE);
    }

}
