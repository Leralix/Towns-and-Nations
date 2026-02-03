package org.leralix.tan.gui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public abstract class BasicGui {

    protected final Gui gui;
    protected final Player player;
    protected final ITanPlayer tanPlayer;
    protected final LangType langType;
    protected final IconManager iconManager;

    protected BasicGui(Player player, Lang title, int rows){
        this(player, title.get(), rows);
    }

    protected BasicGui(Player player, FilledLang title, int rows){
        this.player = player;
        this.tanPlayer = PlayerDataStorage.getInstance().get(player);
        this.langType = tanPlayer.getLang();
        this.iconManager = IconManager.getInstance();

        this.gui = Gui.gui()
                .title(Component.text(title.get(langType)))
                .type(GuiType.CHEST)
                .rows(rows)
                .create();

        gui.setDefaultClickAction(event -> {
            if(event.getClickedInventory().getType() != InventoryType.PLAYER){
                event.setCancelled(true);
            }
        });
        gui.setDragAction(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
    }

    public abstract void open();
}
