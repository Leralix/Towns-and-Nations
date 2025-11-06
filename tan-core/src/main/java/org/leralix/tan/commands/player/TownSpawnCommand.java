package org.leralix.tan.commands.player;

import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownSpawnCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "spawn";
  }

  @Override
  public String getDescription() {
    return Lang.SPAWN_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan spawn";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();
    // Incorrect syntax
    if (args.length != 1) {
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
      return;
    }

    // No town
    if (!tanPlayer.hasTown()) {
      TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
      return;
    }

    TownData townData = tanPlayer.getTownSync();
    if (townData == null) {
      TanChatUtils.message(
          player, Lang.PLAYER_NO_TOWN.get(langType)); // Should not happen if hasTown() is true
      return;
    }

    EnableTownSpawn enableTownSpawn = townData.getNewLevel().getStat(EnableTownSpawn.class);
    // Spawn Unlocked
    if (!enableTownSpawn.isEnabled()) {
      TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
      return;
    }

    // Spawn set
    if (!townData.isSpawnSet()) {
      TanChatUtils.message(player, Lang.SPAWN_NOT_SET.get(langType));
      return;
    }

    TeleportationRegister.teleportToTownSpawn(tanPlayer, townData);
  }
}
