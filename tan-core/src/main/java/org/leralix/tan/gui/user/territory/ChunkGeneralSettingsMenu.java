package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class ChunkGeneralSettingsMenu extends BasicGui {

  private final TerritoryData territoryData;

  private ChunkGeneralSettingsMenu(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(player, tanPlayer, Lang.HEADER_CHUNK_GENERAL_SETTINGS.get(tanPlayer.getLang()), 3);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new ChunkGeneralSettingsMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    // Add all general chunk settings
    int slot = 11;
    for (GeneralChunkSetting setting : GeneralChunkSetting.values()) {
      gui.setItem(slot++, getSettingButton(setting));
    }

    gui.setItem(
        3, 1, GuiUtil.createBackArrow(player, p -> ChunkSettingsMenu.open(player, territoryData)));

    gui.open(player);
  }

  private GuiItem getSettingButton(GeneralChunkSetting setting) {
    boolean isEnabled =
        territoryData.getChunkSettings().getChunkSetting().getOrDefault(setting, false);

    return ItemBuilder.from(setting.getIcon(isEnabled, langType))
        .asGuiItem(
            event -> {
              territoryData.getChunkSettings().getChunkSetting().put(setting, !isEnabled);
              open();
            });
  }
}
