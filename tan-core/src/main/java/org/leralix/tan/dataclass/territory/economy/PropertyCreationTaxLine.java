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
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetCreatePropertyTax;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class PropertyCreationTaxLine extends ProfitLine {

  public PropertyCreationTaxLine(TownData townData) {
    super(townData);
  }

  @Override
  public double getMoney() {
    return 0;
  }

  @Override
  public FilledLang getLine() {
    return null;
  }

  @Override
  public void addItems(Gui gui, Player player, LangType lang) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

    ItemStack tax =
        HeadUtils.makeSkullURL(
            Lang.GUI_TREASURY_CREATE_PROPERTY_TAX.get(lang),
            "http://textures.minecraft.net/texture/97f82aceb98fe069e8c166ced00242a76660bbe07091c92cdde54c6ed10dcff9",
            Lang.GUI_TREASURY_CREATE_PROPERTY_TAX_DESC1.get(
                lang, Double.toString(territoryData.getTaxOnCreatingProperty())),
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
                        player, territoryData, TransactionHistoryEnum.PROPERTY_BUY_TAX);
                  } else if (event.isRightClick()) {
                    TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                    PlayerChatListenerStorage.register(
                        player,
                        new SetCreatePropertyTax(
                            territoryData, p -> TreasuryMenu.open(player, territoryData)));
                  }
                });

    gui.setItem(4, 2, taxInfo);
  }

  @Override
  public boolean isRecurrent() {
    return false;
  }
}
