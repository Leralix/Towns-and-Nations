package org.leralix.tan.war.legacy.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.legacy.CreateAttackData;

import java.util.ArrayList;
import java.util.List;

public abstract class WarGoal {

    String type;

    protected WarGoal(){
        this.type = this.getClass().getSimpleName();
    }

    public abstract ItemStack getIcon();

    public abstract String getDisplayName();

    public abstract void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData);

    public abstract void applyWarGoal();

    public abstract boolean isCompleted();

    protected ItemStack buildIcon(Material material, String description){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add(description);
            lore.add(Lang.LEFT_CLICK_TO_SELECT.get());
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public abstract String getCurrentDesc();

    public void sendAttackSuccessToAttackers(Player player) {
        player.sendMessage(Lang.PLAYER_WON_ATTACK.get());
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }

    public void sendAttackSuccessToDefenders(Player player) {
        player.sendMessage(Lang.PLAYER_LOST_ATTACK.get());
        SoundUtil.playSound(player, SoundEnum.BAD);
    }

    public void sendAttackFailedToAttacker(Player player) {
        player.sendMessage(Lang.PLAYER_WON_ATTACK.get());
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }

    public void sendAttackFailedToDefender(Player player) {
        player.sendMessage(Lang.PLAYER_LOST_ATTACK.get());
        SoundUtil.playSound(player, SoundEnum.BAD);
    }
}
