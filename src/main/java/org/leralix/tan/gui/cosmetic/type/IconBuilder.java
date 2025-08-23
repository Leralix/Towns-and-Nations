package org.leralix.tan.gui.cosmetic.type;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class IconBuilder {

    private String name;
    private final List<String> description;
    private Consumer<InventoryClickEvent> action;
    private boolean hideItemFlags;
    private final IconType iconType;

    public IconBuilder(IconType iconType){
        this.description = new ArrayList<>();
        hideItemFlags = false;
        if(iconType == null) {
            this.iconType = new ItemIconBuillder(Material.BARRIER);
        }
        else{
            this.iconType = iconType;
        }
    }


    public IconBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public IconBuilder setDescription(String... descriptions) {
        this.description.clear();
        this.description.addAll(List.of(descriptions));
        return this;
    }

    public IconBuilder setDescription(Collection<String> description) {
        this.description.clear();
        this.description.addAll(description);
        return this;
    }

    public IconBuilder setAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    public IconBuilder setHideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;
        return this;
    }

    public GuiItem asGuiItem(Player player) {
        ItemStack item = iconType.getItemStack(player);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(description);
            if(hideItemFlags) {
                meta.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
                meta.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
                meta.getItemFlags().add(ItemFlag.HIDE_UNBREAKABLE);
                meta.getItemFlags().add(ItemFlag.HIDE_PLACED_ON);
                meta.getItemFlags().add(ItemFlag.HIDE_DYE);
            }
            item.setItemMeta(meta);
        }
        if(action == null){
            return ItemBuilder.from(item).asGuiItem(event -> event.setCancelled(true));
        }
        else {
            return ItemBuilder.from(item).asGuiItem(event -> action.accept(event));
        }
    }

}
