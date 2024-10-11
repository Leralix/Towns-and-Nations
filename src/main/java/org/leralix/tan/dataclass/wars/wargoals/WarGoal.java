package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class WarGoal {

    String type;

    protected WarGoal(){
        this.type = this.getClass().getSimpleName();
    }

    public abstract ItemStack getIcon();

    public abstract String getDisplayName();

    public abstract void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit);

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
