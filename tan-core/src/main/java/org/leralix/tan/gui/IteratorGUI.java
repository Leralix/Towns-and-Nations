package org.leralix.tan.gui;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.utils.deprecated.GuiUtil;

public abstract class IteratorGUI extends BasicGui {

  protected int page;

  protected IteratorGUI(Player player, ITanPlayer tanPlayer, String title, int rows) {
    super(player, tanPlayer, title, rows);
    this.page = 0;
  }

  protected void iterator(List<GuiItem> itemList, Consumer<Player> onLeave) {
    iterator(itemList, onLeave, Material.GRAY_STAINED_GLASS_PANE);
  }

  protected void iterator(
      List<GuiItem> itemList, Consumer<Player> onLeave, Material decorativeMaterial) {
    GuiUtil.createIterator(
        gui,
        itemList,
        page,
        player,
        onLeave,
        p -> nextPage(),
        p -> previousPage(),
        decorativeMaterial);
  }

  protected void previousPage() {
    page--;
    open();
  }

  protected void nextPage() {
    page++;
    open();
  }
}
