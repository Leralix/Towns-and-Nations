package org.leralix.tan.commands.admin;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class AddUpgradeAdminCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public AddUpgradeAdminCommand(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "addupgrade";
    }

    @Override
    public String getDescription() {
        return "Add an upgrade to the territory at your position";
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin addupgrade <upgradeId>";
    }

    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = playerDataStorage.get(player).getLang();
        if (args.length != 1) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType));
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        IClaimedChunk claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(chunk);
        if (!(claimedChunk instanceof TerritoryChunk territoryChunk)) {
            TanChatUtils.message(player, Lang.ADMIN_UNCLAIM_CHUNK_NOT_CLAIMED.get(langType), SoundEnum.NOT_ALLOWED);
            return;
        }

        Territory owner = territoryChunk.getOwnerInternal();
        String upgradeId = args[0];
        Upgrade upgrade = Constants.getUpgradeStorage().getUpgrade(owner, upgradeId);
        if (upgrade == null) {
            TanChatUtils.message(player, Lang.UPGRADE_NOT_FOUND.get(langType), SoundEnum.NOT_ALLOWED);
            return;
        }

        int currentLevel = owner.getNewLevel().getLevel(upgrade);
        if (currentLevel >= upgrade.getMaxLevel()) {
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }

        owner.getNewLevel().levelUp(upgrade);
        TanChatUtils.message(player, Lang.BASIC_LEVEL_UP.get(langType), SoundEnum.LEVEL_UP);
        SoundUtil.playSound(player, SoundEnum.ADD);
    }
}
