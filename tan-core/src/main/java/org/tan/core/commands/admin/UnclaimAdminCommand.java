package org.tan.core.commands.admin;

import java.util.Collections;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class UnclaimAdminCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "unclaim";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_UNCLAIM_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tanadmin unclaim";
  }

  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {
    // Validate argument count (expects exactly 1 - just the command)
    if (!CommandExceptionHandler.validateArgCount((CommandSender) player, args, 1, getSyntax())) {
      return;
    }

    LangType langType = LangType.of(player);
    Chunk chunk = player.getLocation().getChunk();
    ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
    if (claimedChunk instanceof TerritoryChunk territoryChunk) {
      NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(territoryChunk);

      TerritoryData owner = territoryChunk.getOwner();
      ChunkCap chunkCap = owner.getNewLevel().getStat(ChunkCap.class);
      if (chunkCap.isUnlimited()) {
        Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(langType, owner.getColoredName());
      } else {
        String currentChunks = Integer.toString(owner.getNumberOfClaimedChunk());
        String maxChunks = Integer.toString(chunkCap.getMaxAmount());
        Lang.CHUNK_UNCLAIMED_SUCCESS_LIMITED.get(
            langType, owner.getColoredName(), currentChunks, maxChunks);
      }
    } else {
      TanChatUtils.message(
          player, Lang.ADMIN_UNCLAIM_CHUNK_NOT_CLAIMED.get(langType), SoundEnum.NOT_ALLOWED);
    }
  }
}
