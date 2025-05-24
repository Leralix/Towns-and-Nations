package org.leralix.tan.gui.cosmetic.type;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class IconBuilder {

    private String name;
    private final List<String> description;
    private Consumer<InventoryClickEvent> action;

    public IconBuilder(){
        this.description = new ArrayList<>();
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

    protected abstract ItemStack getItemStack(Player player);

    public GuiItem asGuiItem(Player player) {
        ItemStack item = getItemStack(player);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(description);
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(event -> action.accept(event));
    }
}