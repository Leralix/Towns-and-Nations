package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.inventory.ItemStack;
import org.leralix.tan.utils.gameplay.ItemStackSerializer;

public class CustomIcon implements ICustomIcon {

    private final String base64Item;

    public CustomIcon(ItemStack icon) {
        this.base64Item = ItemStackSerializer.serializeItemStack(icon);
    }

    public ItemStack getIcon() {
        return ItemStackSerializer.deserializeItemStack(base64Item);
    }
}
