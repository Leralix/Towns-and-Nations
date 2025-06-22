package org.leralix.tan.gui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public abstract class BasicGui {

    protected final Gui gui;
    protected final Player player;
    protected final ITanPlayer tanPlayer;
    protected final IconManager iconManager;

    protected BasicGui(Player player, Lang title, int rows) {
        this(player, title.get(PlayerDataStorage.getInstance().get(player)), rows);
    }

    protected BasicGui(Player player, String title, int rows){
        this.tanPlayer = PlayerDataStorage.getInstance().get(player);
        this.gui = Gui.gui()
                .title(Component.text(title))
                .type(GuiType.CHEST)
                .rows(rows)
                .create();
        this.player = player;
        this.iconManager = IconManager.getInstance();

        gui.setDefaultClickAction(event -> {
            if(event.getClickedInventory().getType() != InventoryType.PLAYER){
                event.setCancelled(true);
            }
        });
        gui.setDragAction(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
    }

    public abstract void open();
}
