package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.user.territory.EconomicHistoryMenu;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetTerritoryTax;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerTaxLine extends ProfitLine {

  double actualTaxes = 0;
  double missingTaxes = 0;

  public PlayerTaxLine(TownData townData) {
    super(townData);
    double flatTax = townData.getTax();
    for (String playerID : townData.getPlayerIDList()) {
      ITanPlayer othertanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
      OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
      if (!othertanPlayer.getTownRank().isPayingTaxes()) {
        continue;
      }
      if (EconomyUtil.getBalance(otherPlayer) < flatTax) missingTaxes += flatTax;
      else actualTaxes += flatTax;
    }
  }

  @Override
  protected double getMoney() {
    return actualTaxes;
  }

  @Override
  public FilledLang getLine() {
    if (missingTaxes > 0)
      return Lang.PLAYER_TAX_MISSING_LINE.get(
          StringUtil.getColoredMoney(getMoney()), StringUtil.formatMoney(missingTaxes));
    else return Lang.PLAYER_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
  }

  @Override
  public void addItems(Gui gui, Player player, LangType lang) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);

    ItemStack lowerTax =
        HeadUtils.makeSkullB64(
            Lang.GUI_TREASURY_LOWER_TAX.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
            Lang.GUI_DECREASE_1_DESC.get(lang),
            Lang.GUI_DECREASE_10_DESC.get(lang));
    ItemStack increaseTax =
        HeadUtils.makeSkullB64(
            Lang.GUI_TREASURY_INCREASE_TAX.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
            Lang.GUI_INCREASE_1_DESC.get(lang),
            Lang.GUI_INCREASE_10_DESC.get(lang));
    ItemStack tax =
        HeadUtils.makeSkullB64(
            Lang.GUI_TREASURY_FLAT_TAX.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
            Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(
                lang, StringUtil.formatMoney(territoryData.getTax())),
            Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(lang),
            Lang.RIGHT_CLICK_TO_SET_TAX.get(lang));

    GuiItem lowerTaxButton =
        ItemBuilder.from(lowerTax)
            .asGuiItem(
                event -> {
                  event.setCancelled(true);
                  if (!territoryData.doesPlayerHavePermission(
                      tanPlayer, RolePermission.MANAGE_TAXES)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                    return;
                  }

                  double currentTax = territoryData.getTax();
                  int amountToRemove = event.isShiftClick() && currentTax > 9 ? 10 : 1;

                  if (currentTax <= 0) {
                    TanChatUtils.message(player, Lang.GUI_TREASURY_CANT_TAX_LESS.get(lang));
                    return;
                  }
                  SoundUtil.playSound(player, SoundEnum.REMOVE);

                  territoryData.addToTax(-amountToRemove);
                  TreasuryMenu.open(player, territoryData);
                });
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
                        player, territoryData, TransactionHistoryEnum.PLAYER_TAX);
                  } else if (event.isRightClick()) {
                    TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                    PlayerChatListenerStorage.register(
                        player,
                        new SetTerritoryTax(
                            territoryData, p -> TreasuryMenu.open(player, territoryData)));
                  }
                });
    GuiItem increaseTaxButton =
        ItemBuilder.from(increaseTax)
            .asGuiItem(
                event -> {
                  event.setCancelled(true);

                  if (!territoryData.doesPlayerHavePermission(
                      tanPlayer, RolePermission.MANAGE_TAXES)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                    return;
                  }

                  int amountToAdd = event.isShiftClick() ? 10 : 1;

                  territoryData.addToTax(amountToAdd);
                  SoundUtil.playSound(player, SoundEnum.ADD);
                  TreasuryMenu.open(player, territoryData);
                });

    gui.setItem(2, 2, lowerTaxButton);
    gui.setItem(2, 3, taxInfo);
    gui.setItem(2, 4, increaseTaxButton);
  }

  @Override
  public boolean isRecurrent() {
    return true;
  }
}
