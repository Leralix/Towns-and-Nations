package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetRentPropertyRate;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;


public class PropertyRentTaxLine extends ProfitLine {

    double taxes = 0;

    public PropertyRentTaxLine(TownData townData) {
        super(townData);
        for (PropertyData propertyData : townData.getPropertiesInternal()) {
            if (propertyData.isRented()) {
                taxes += propertyData.getRentPrice() * townData.getTaxOnRentingProperty();
            }
        }
    }

    @Override
    public double getMoney() {
        return taxes;
    }

    @Override
    public FilledLang getLine() {
        return Lang.PROPERTY_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, ITanPlayer playerData, LangType lang) {

        gui.setItem(4, 3, IconManager.getInstance().get(IconKey.PROPERTY_RENT_TAX_ICON)
                .setName(Lang.GUI_TREASURY_RENT_PROPERTY_TAX.get(lang))
                .setDescription(Lang.GUI_TREASURY_PROPERTY_RENT_TAX_DESC1.get(String.format("%.2f", territoryData.getTaxOnRentingProperty() * 100)))
                .setClickToAcceptMessage(
                        Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY,
                        Lang.RIGHT_CLICK_TO_SET_TAX
                )
                .setAction(event -> {
                            event.setCancelled(true);

                            if (!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TAXES)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                                return;
                            }

                            if (event.isLeftClick()) {
                                new TerritoryTransactionHistory(player, territoryData, TransactionType.RENTING_PROPERTY, p -> new TreasuryMenu(player, territoryData));
                            } else if (event.isRightClick()) {
                                TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                                PlayerChatListenerStorage.register(player, lang, new SetRentPropertyRate(territoryData));
                            }
                        }
                )
                .asGuiItem(player, lang)
        );

    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
