package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.UpgradeStatus;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

public class MobSpawnSettingsMenu extends IteratorGUI {

  private final TownData townData;

  private MobSpawnSettingsMenu(Player player, ITanPlayer tanPlayer, TownData townData) {
    super(player, tanPlayer, Lang.GUI_TOWN_CHUNK_MOB.get(tanPlayer), 6);
    this.townData = townData;
  }

  public static void open(Player player, TownData townData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new MobSpawnSettingsMenu(player, tanPlayer, townData).open();
            });
  }

  @Override
  public void open() {
    iterator(getMobSettings(), p -> ChunkSettingsMenu.open(player, townData));
    gui.open(player);
  }

  private List<GuiItem> getMobSettings() {
    List<GuiItem> items = new ArrayList<>();

    for (MobChunkSpawnEnum mobType : MobChunkSpawnStorage.getMobSpawnStorage().values()) {
      UpgradeStatus status = townData.getChunkSettings().getSpawnControl(mobType);

      String statusText =
          status.canSpawn()
              ? Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get(langType)
              : Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get(langType);

      GuiItem mobButton =
          ItemBuilder.from(HeadUtils.makeSkullB64(mobType.name(), mobType.getTexture()))
              .asGuiItem(
                  action -> {
                    status.setActivated(!status.canSpawn());
                    open();
                  });

      items.add(mobButton);
    }

    return items;
  }
}
