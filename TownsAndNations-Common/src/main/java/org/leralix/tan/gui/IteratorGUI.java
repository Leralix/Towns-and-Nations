package org.leralix.tan.gui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;

import java.util.List;
import java.util.function.Consumer;

public abstract class IteratorGUI extends BasicGui {

    protected int page;

    protected IteratorGUI(Player player, Lang title, int rows) {
        super(player, title, rows);
        this.page = 0;
    }

    protected IteratorGUI(Player player, FilledLang title, int rows) {
        super(player, title, rows);
        this.page = 0;
    }


    protected void iterator(List<GuiItem> itemList, Consumer<Player> onLeave) {
        iterator(itemList, onLeave, Material.GRAY_STAINED_GLASS_PANE);
    }

    protected void iterator(List<GuiItem> itemList, Consumer<Player> onLeave, Material decorativeMaterial) {
        createIterator(gui, itemList, page, player, tanPlayer, onLeave, p -> nextPage(), p -> previousPage(), decorativeMaterial);
    }

    protected void previousPage() {
        page--;
        open();
    }

    protected void nextPage() {
        page++;
        open();
    }

    public static void createIterator(
            Gui gui,
            List<GuiItem> guItems,
            int page,
            Player player,
            ITanPlayer tanPlayer,
            Consumer<Player> backArrowAction,
            Consumer<Player> nextPageAction,
            Consumer<Player> previousPageAction,
            Material decorativeMaterial
    ) {
        createIterator(gui, guItems, page, player, tanPlayer, backArrowAction, nextPageAction, previousPageAction, getUnnamedItem(decorativeMaterial));
    }

    public static void createIterator(
            Gui gui,
            List<GuiItem> guItems,
            int page,
            Player player,
            ITanPlayer tanPlayer,
            Consumer<Player> backArrowAction,
            Consumer<Player> nextPageAction,
            Consumer<Player> previousPageAction,
            GuiItem decorativeGlassPane
    ) {

        int pageSize = (gui.getRows() - 1) * 9;
        int startIndex = page * pageSize;
        boolean lastPage;
        int totalSize = guItems.size();

        int endIndex;
        if (startIndex + pageSize > totalSize) {
            endIndex = totalSize;
            lastPage = true;
        } else {
            lastPage = false;
            endIndex = startIndex + pageSize;
        }

        for (int i = 0; i < pageSize; i++) {
            gui.removeItem(i);
        }

        int slot = 0;

        for (int i = startIndex; i < endIndex; i++) {
            gui.setItem(slot, guItems.get(i));
            slot++;
        }
        int lastRow = gui.getRows();

        gui.setItem(lastRow, 1, createBackArrow(player, backArrowAction, tanPlayer.getLang()));

        gui.setItem(lastRow, 7, IconManager.getInstance().get(IconKey.PREVIOUS_PAGE_ICON)
                .setName(Lang.GUI_PREVIOUS_PAGE.get(tanPlayer))
                .setAction(action -> {
                    if (page == 0) {
                        return;
                    }
                    previousPageAction.accept(player);
                })
                .asGuiItem(player, tanPlayer.getLang())
        );

        gui.setItem(lastRow, 8, IconManager.getInstance().get(IconKey.NEXT_PAGE_ICON)
                .setName(Lang.GUI_NEXT_PAGE.get(tanPlayer))
                .setAction(action -> {
                    if (lastPage) {
                        return;
                    }
                    nextPageAction.accept(player);
                })
                .asGuiItem(player, tanPlayer.getLang())
        );

        gui.getFiller().fillBottom(decorativeGlassPane);
    }
}
