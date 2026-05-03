package org.leralix.tan.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.scope.DisplayableEnum;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BasicGui {

    protected final Gui gui;
    protected final Player player;
    protected final ITanPlayer tanPlayer;
    protected final LangType langType;
    protected final IconManager iconManager;

    protected BasicGui(Player player, Lang title, int rows){
        this(player, title.get(), rows);
    }

    protected BasicGui(Player player, FilledLang title, int rows){
        this.player = player;
        this.tanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(player);
        this.langType = tanPlayer.getLang();
        this.iconManager = IconManager.getInstance();

        this.gui = Gui.gui()
                .title(Component.text(title.get(langType)))
                .type(GuiType.CHEST)
                .rows(rows)
                .create();

        gui.setDefaultClickAction(event -> {
            if(event.isShiftClick()){
                event.setCancelled(true);
            }
            if(event.getClickedInventory().getType() != InventoryType.PLAYER){
                event.setCancelled(true);
            }
        });
        gui.setDragAction(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
    }

    public abstract void open();

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
        itemMeta.setHideTooltip(true);
        item.setItemMeta(itemMeta);
        return ItemBuilder.from(item).asGuiItem(event -> event.setCancelled(true));
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
