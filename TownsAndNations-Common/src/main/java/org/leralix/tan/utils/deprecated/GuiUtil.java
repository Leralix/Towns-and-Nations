package org.leralix.tan.utils.deprecated;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.scope.DisplayableEnum;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiUtil {

    private GuiUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Gui createChestGui(String name, int nRow) {
        return Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();
    }

    public static GuiItem createBackArrow(Player player, Consumer<Player> openMenuAction, LangType langType) {

        return IconManager.getInstance().get(Material.ARROW)
                .setName(Lang.GUI_BACK_ARROW.get(langType))
                .setAction(event -> {
                    event.setCancelled(true);
                    openMenuAction.accept(player);
                })
                .asGuiItem(player, langType);

    }

    public static GuiItem getUnnamedItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        return ItemBuilder.from(item).asGuiItem(event -> event.setCancelled(true));
    }

    public static void createIterator(
            Gui gui,
            List<GuiItem> guItems,
            int page,
            Player player,
            ITanPlayer tanPlayer,
            Consumer<Player> backArrowAction,
            Consumer<Player> nextPageAction,
            Consumer<Player> previousPageAction
    ) {

        createIterator(gui, guItems, page, player, tanPlayer, backArrowAction, nextPageAction, previousPageAction, Material.GRAY_STAINED_GLASS_PANE);
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
        ItemStack decorativeGlassPane = new ItemStack(decorativeMaterial);
        ItemMeta itemMeta = decorativeGlassPane.getItemMeta();
        itemMeta.setDisplayName("");
        decorativeGlassPane.setItemMeta(itemMeta);
        createIterator(gui, guItems, page, player, tanPlayer, backArrowAction, nextPageAction, previousPageAction, decorativeGlassPane);
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
            ItemStack decorativeGlassPane
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
        GuiItem panel = ItemBuilder.from(decorativeGlassPane).asGuiItem(event -> event.setCancelled(true));

        int lastRow = gui.getRows();

        gui.setItem(lastRow, 1, GuiUtil.createBackArrow(player, backArrowAction, tanPlayer.getLang()));

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

        gui.getFiller().fillBottom(panel);
    }

    public static <E extends Enum<E> & DisplayableEnum> GuiItem getNextScopeButton(
            IconManager iconManager,
            BasicGui basicGui,
            E currentValue,
            Consumer<E> valueUpdater,
            LangType langType,
            Player player
    ) {
        List<FilledLang> description = new ArrayList<>();

        for (E enumConstant : currentValue.getDeclaringClass().getEnumConstants()) {
            String name = enumConstant.getDisplayName(langType);
            if (enumConstant == currentValue) {
                description.add(Lang.BROWSE_ITERATOR_SELECTED_OPTION.get(name));
            } else {
                description.add(Lang.BROWSE_ITERATOR_UNSELECTED_OPTION.get(name));
            }
        }

        return iconManager.get(IconKey.CHANGE_SCOPE_ICON)
                .setName(Lang.BROWSE_SELECT.get(langType, currentValue.getDisplayName(langType)))
                .setDescription(description)
                .setClickToAcceptMessage(
                        Lang.GUI_GENERIC_RIGHT_CLICK_TO_BACK,
                        Lang.GUI_GENERIC_LEFT_CLICK_TO_NEXT
                )
                .setAction(action -> {
                    E next = action.isLeftClick() ?
                            getNextEnumValue(currentValue) :
                            getPreviousEnumValue(currentValue);

                    valueUpdater.accept(next);
                    SoundUtil.playSound(player, SoundEnum.ADD);
                    basicGui.open();
                })
                .asGuiItem(player, langType);
    }

    private static <E extends Enum<E>> E getNextEnumValue(E current) {
        E[] values = current.getDeclaringClass().getEnumConstants();
        int nextIndex = (current.ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    private static <E extends Enum<E>> E getPreviousEnumValue(E current) {
        E[] values = current.getDeclaringClass().getEnumConstants();
        int prevIndex = (current.ordinal() - 1 + values.length) % values.length;
        return values[prevIndex];
    }
}
