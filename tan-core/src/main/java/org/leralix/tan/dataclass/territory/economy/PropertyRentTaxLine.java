package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetRentPropertyRate;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class PropertyRentTaxLine extends ProfitLine {

  double taxes = 0;

  public PropertyRentTaxLine(TownData townData) {
    super(townData);
    for (PropertyData propertyData : townData.getProperties()) {
      taxes += propertyData.getRentPrice() * townData.getTaxOnRentingProperty() / 100;
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
  public void addItems(Gui gui, Player player, LangType lang) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

    ItemStack tax =
        HeadUtils.makeSkullURL(
            Lang.GUI_TREASURY_RENT_PROPERTY_TAX.get(lang),
            "https://textures.minecraft.net/texture/e19997593f2c592b9fbd4f15ead1673b76f519d7ab3efa15edd19448d1a20bfc",
            Lang.GUI_TREASURY_PROPERTY_RENT_TAX_DESC1.get(
                lang, String.format("%.2f", territoryData.getTaxOnRentingProperty() * 100)),
            Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(lang),
            Lang.RIGHT_CLICK_TO_SET_TAX.get(lang));

    GuiItem taxInfo =
        ItemBuilder.from(tax)
            .asGuiItem(
                event -> {
                  event.setCancelled(true);

                  if (!territoryData.doesPlayerHavePermission(
                      tanPlayer, RolePermission.MANAGE_TAXES)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                    return;
                  }

                  if (event.isLeftClick()) {
                    EconomicHistoryMenu.open(
                        player, territoryData, TransactionHistoryEnum.PROPERTY_RENT_TAX);
                  } else if (event.isRightClick()) {
                    TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                    PlayerChatListenerStorage.register(
                        player, new SetRentPropertyRate(territoryData));
                  }
                });

    gui.setItem(4, 3, taxInfo);
  }

  @Override
  public boolean isRecurrent() {
    return true;
  }
}
