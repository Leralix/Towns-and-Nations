package org.leralix.tan.commands.player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.exception.TerritoryException;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class UnclaimCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "unclaim";
  }

  @Override
  public String getDescription() {
    return Lang.UNCLAIM_CHUNK_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan unclaim";
  }

  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();

    // Validate argument count (1 or 4 arguments)
    if (args.length != 1 && args.length != 4) {
      TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
      return;
    }

    Chunk chunk;
    try {
      if (args.length == 1) {
        chunk = player.getLocation().getChunk();
      } else {
        // Parse coordinates with error handling
        Optional<Integer> xOpt = CommandExceptionHandler.parseInt(player, args[2], "x coordinate");
        Optional<Integer> yOpt = CommandExceptionHandler.parseInt(player, args[3], "y coordinate");

        if (xOpt.isEmpty() || yOpt.isEmpty()) {
          return;
        }

        chunk = player.getLocation().getWorld().getChunkAt(xOpt.get(), yOpt.get());
      }

      // Validate chunk is claimed
      if (!NewClaimedChunkStorage.getInstance().isChunkClaimed(chunk)) {
        TanChatUtils.message(player, Lang.CHUNK_NOT_CLAIMED.get(langType));
        return;
      }

      // Unclaim the chunk
      ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
      executeUnclaimChunk(claimedChunk, player);

      // Open map if coordinates were provided
      if (args.length == 4) {
        MapCommand.openMap(player, new MapSettings(args[0], args[1]));
      }
    } catch (TerritoryException e) {
      TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
      CommandExceptionHandler.logCommandExecution(player, "unclaim", args);
    }
  }

  /**
   * Executes the chunk unclaim operation.
   *
   * @param claimedChunk The claimed chunk to unclaim
   * @param player The player executing the unclaim
   * @throws TerritoryException If the unclaim operation fails
   */
  private void executeUnclaimChunk(ClaimedChunk2 claimedChunk, Player player)
      throws TerritoryException {
    try {
      claimedChunk.unclaimChunk(player);
    } catch (Exception e) {
      throw new TerritoryException("Chunk unclaim failed: " + e.getMessage(), e);
    }
  }
}
