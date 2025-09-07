package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetBuyPropertyRate;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;


public class PropertySellTaxLine extends ProfitLine {


    public PropertySellTaxLine(TownData townData) {
        super(townData);
    }

    @Override
    public double getMoney() {
        return 0;
    }

    @Override
    public String getLine() {
        return null;
    }

    @Override
    public void addItems(Gui gui, Player player) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        ItemStack tax = HeadUtils.makeSkullURL(Lang.GUI_TREASURY_BUY_PROPERTY_TAX.get(), "http://textures.minecraft.net/texture/97f82aceb98fe069e8c166ced00242a76660bbe07091c92cdde54c6ed10dcff9",
                Lang.GUI_TREASURY_PROPERTY_RENT_TAX_DESC1.get(String.format("%.2f", territoryData.getTaxOnBuyingProperty() * 100)),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                Lang.RIGHT_CLICK_TO_SET_TAX.get());

        GuiItem taxInfo = ItemBuilder.from(tax).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TAXES)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
            if (event.isLeftClick()) {
                new EconomicHistoryMenu(player, territoryData, TransactionHistoryEnum.PROPERTY_BUY_TAX);
            } else if (event.isRightClick()) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_SET_TAX_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new SetBuyPropertyRate(territoryData));
                player.closeInventory();
            }
        });

        gui.setItem(4, 4, taxInfo);
    }

    @Override
    public boolean isRecurrent() {
        return false;
    }
}
