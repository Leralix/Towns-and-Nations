package org.leralix.tan.gui;

import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.GuiUtil;

public abstract class BasicGui {

    protected final Gui gui;
    protected final Player player;
    protected final IconManager iconManager;

    protected BasicGui(Player player, Lang title, int rows){
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        this.gui = Gui.gui()
                .title(Component.text(title.get(playerData)))
                .type(GuiType.CHEST)
                .rows(rows)
                .create();
        this.player = player;
        this.iconManager = IconManager.getInstance();

        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.setDragAction(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
    }

    public void open(){
        gui.open(player);
    }
}
