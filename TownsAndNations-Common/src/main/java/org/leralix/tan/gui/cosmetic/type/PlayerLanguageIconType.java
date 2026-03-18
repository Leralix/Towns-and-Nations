package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.json.PlayerJsonStorage;

public class PlayerLanguageIconType extends IconType {
    @Override
    protected ItemStack getItemStack(Player player) {
        ITanPlayer tanPlayer = PlayerJsonStorage.getInstance().get(player);
        LangType playerLang = tanPlayer.getLang();
        return IconManager.getInstance().get(playerLang.getIconKey()).asGuiItem(player, playerLang).getItemStack();
    }
}
