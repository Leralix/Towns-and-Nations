package org.leralix.tan.gui.cosmetic.type;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class IconBuilder {

    private Lang name;
    private final List<Lang> description = new ArrayList<>();
    private Consumer<InventoryClickEvent> action;

    public IconBuilder setName(Lang name) {
        this.name = name;
        return this;
    }

    public IconBuilder setDescription(List<Lang> description) {
        this.description.clear();
        this.description.addAll(description);
        return this;
    }

    public IconBuilder addDescription(Lang line) {
        this.description.add(line);
        return this;
    }

    public IconBuilder addAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    protected abstract ItemStack getItemStack(Player player);

    public GuiItem asGuiItem(Player player) {
        ItemStack item = getItemStack(player);
        ItemMeta meta = item.getItemMeta();

        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        if (meta != null) {
            meta.setDisplayName(name.get(playerData));
            meta.setLore(description.stream().map(lang -> lang.get(playerData)).toList());
            item.setItemMeta(meta);
        }
        return ItemBuilder.from(item).asGuiItem(event -> action.accept(event));
    }
}