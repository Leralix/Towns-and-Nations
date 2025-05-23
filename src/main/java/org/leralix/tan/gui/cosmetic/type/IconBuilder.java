package org.leralix.tan.gui.cosmetic.type;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class IconBuilder {

    private String name;
    private final List<String> description = new ArrayList<>();
    private Consumer<InventoryClickEvent> action;

    public IconBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public IconBuilder setDescription(List<String> description) {
        this.description.clear();
        this.description.addAll(description);
        return this;
    }

    public IconBuilder addDescription(String line) {
        this.description.add(line);
        return this;
    }

    public IconBuilder addAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    protected abstract ItemStack getItemStack();

    public GuiItem  asGuiItem() {
        ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(description);
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(event -> action.accept(event));
    }
}