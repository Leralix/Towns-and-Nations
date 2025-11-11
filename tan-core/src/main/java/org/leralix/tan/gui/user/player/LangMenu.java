package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class LangMenu extends IteratorGUI {

  private LangMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_SELECT_LANGUAGE.get(tanPlayer.getLang()), 3);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new LangMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    GuiUtil.createIterator(
        gui, getLangItems(), page, player, PlayerMenu::open, p -> nextPage(), p -> previousPage());

    gui.setItem(3, 6, getPlayerGUI());

    gui.open(player);
  }

  private List<GuiItem> getLangItems() {
    List<GuiItem> guiItems = new ArrayList<>();
    for (LangType lang : LangType.values()) {
      ItemStack langIcon = lang.getIcon(langType);
      GuiItem langGui =
          ItemBuilder.from(langIcon)
              .asGuiItem(
                  event -> {
                    tanPlayer.setLang(lang);
                    TanChatUtils.message(
                        player, Lang.GUI_LANGUAGE_CHANGED.get(tanPlayer, lang.getName()));
                    PlayerMenu.open(player);
                  });
      guiItems.add(langGui);
    }
    return guiItems;
  }

  private GuiItem getPlayerGUI() {
    return IconManager.getInstance()
        .get(IconKey.HELP_TRANSLATION_ICON)
        .setName(Lang.HELP_US_TRANSLATE.get(tanPlayer))
        .setAction(
            event -> {
              Component textComponent =
                  Component.text(Lang.CLICK_HERE_TO_OPEN_BROWSER.get(tanPlayer))
                      .clickEvent(
                          ClickEvent.openUrl("https://crowdin.com/project/town-and-nation"));
              player.sendMessage(textComponent);
              player.closeInventory();
            })
        .asGuiItem(player, langType);
  }
}
