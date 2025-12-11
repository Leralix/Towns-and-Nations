package org.leralix.tan.gui.utils;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class ConfirmMenu extends BasicGui {

  private final FilledLang message;
  private final Consumer<Player> onConfirm;
  private final Consumer<Player> onCancel;

  private ConfirmMenu(
      Player player,
      ITanPlayer tanPlayer,
      FilledLang message,
      Consumer<Player> onConfirm,
      Consumer<Player> onCancel) {
    super(player, tanPlayer, Lang.HEADER_CONFIRMATION.get(tanPlayer.getLang()), 3);
    this.message = message;
    this.onConfirm = onConfirm;
    this.onCancel = onCancel;
  }

  public static void open(
      Player player, FilledLang message, Consumer<Player> onConfirm, Consumer<Player> onCancel) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new ConfirmMenu(player, tanPlayer, message, onConfirm, onCancel).open();
            });
  }

  @Override
  public void open() {
    GuiItem messageItem =
        iconManager.get(Material.PAPER).setName(message.get(langType)).asGuiItem(player, langType);
    gui.setItem(2, 5, messageItem);

    GuiItem confirmButton =
        iconManager
            .get(Material.EMERALD)
            .setName(Lang.GENERIC_CONFIRM_ACTION.get(langType))
            .setAction(
                action -> {
                  player.closeInventory();
                  if (onConfirm != null) {
                    onConfirm.accept(player);
                  }
                })
            .asGuiItem(player, langType);
    gui.setItem(3, 3, confirmButton);

    GuiItem cancelButton =
        iconManager
            .get(Material.REDSTONE)
            .setName(Lang.GENERIC_CANCEL_ACTION.get(langType))
            .setAction(
                action -> {
                  player.closeInventory();
                  if (onCancel != null) {
                    onCancel.accept(player);
                  }
                })
            .asGuiItem(player, langType);
    gui.setItem(3, 7, cancelButton);

    gui.open(player);
  }
}
