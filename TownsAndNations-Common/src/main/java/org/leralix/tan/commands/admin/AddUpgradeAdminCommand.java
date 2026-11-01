package org.leralix.tan.commands.admin;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class AddUpgradeAdminCommand extends UpgradeAdminCommand {

    public AddUpgradeAdminCommand(PlayerDataStorage playerDataStorage) {
        super(playerDataStorage,
                "addupgrade",
                "Add an upgrade to the territory at your position",
                "/tanadmin addupgrade <territory> <upgradeId>"
        );
    }

    protected void allowTerritory(Player tanPlayer, Territory territory, Upgrade upgrade){
        territory.getNewLevel().levelUp(upgrade);
        TanChatUtils.message(tanPlayer, Lang.BASIC_LEVEL_UP, SoundEnum.LEVEL_UP);
        SoundUtil.playSound(tanPlayer, SoundEnum.ADD);
    }
}
