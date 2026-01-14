package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoNationMenu {

    private final Player player;
    private final dev.triumphteam.gui.guis.Gui gui;
    private final ITanPlayer tanPlayer;
    private final LangType langType;

    public NoNationMenu(Player player) {
        this.player = player;
        this.tanPlayer = PlayerDataStorage.getInstance().get(player);
        this.langType = tanPlayer.getLang();
        this.gui = GuiUtil.createChestGui(Lang.HEADER_NATION_MENU.getDefault(), 3);
        open();
    }

    private void open() {
        gui.setItem(1, 5, IconManager.getInstance().get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_NATION_NO_NATION.get(tanPlayer))
                .setDescription(Lang.GUI_NATION_NO_NATION_DESC1.get())
                .asGuiItem(player, langType));

        gui.setItem(2, 4, IconManager.getInstance().get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_NATION_BROWSE.get(tanPlayer))
                .setDescription(Lang.GUI_NATION_BROWSE_DESC1.get())
                .setAction(event -> {
                    event.setCancelled(true);
                    new BrowseTerritoryMenu(player, null, BrowseScope.NATIONS, p -> open());
                })
                .asGuiItem(player, langType));

        gui.setItem(2, 6, IconManager.getInstance().get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_NATION_CREATE.get(tanPlayer))
                .setDescription(Lang.GUI_NATION_CREATE_DESC1.get())
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!Constants.enableNation()) {
                        org.leralix.tan.utils.text.TanChatUtils.message(player, Lang.GUI_WARNING_STILL_IN_DEV.get(tanPlayer), org.leralix.lib.data.SoundEnum.NOT_ALLOWED);
                        return;
                    }
                    org.leralix.tan.utils.text.TanChatUtils.message(player, Lang.GUI_NATION_CREATE_IN_CHAT.get(tanPlayer));
                    org.leralix.tan.listeners.chat.PlayerChatListenerStorage.register(player, new org.leralix.tan.listeners.chat.events.CreateNation());
                })
                .asGuiItem(player, langType));

        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.PURPLE_STAINED_GLASS_PANE));
        gui.getFiller().fillBottom(GuiUtil.getUnnamedItem(Material.PURPLE_STAINED_GLASS_PANE));

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, MainMenu::new));

        gui.open(player);
    }
}
