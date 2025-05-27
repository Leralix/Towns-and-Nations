package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PlayerLanguageIconType extends IconType {
    @Override
    protected ItemStack getItemStack(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        return playerData.getLang().getIcon();
    }
}
