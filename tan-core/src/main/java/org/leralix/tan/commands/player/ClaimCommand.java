package org.leralix.tan.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.exception.TerritoryException;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class ClaimCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "claim";
  }

  @Override
  public String getDescription() {
    return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan claim <town/region>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    List<String> suggestions = new ArrayList<>();
    if (args.length == 2) {
      suggestions.add("town");
      suggestions.add("region");
    }
    return suggestions;
  }

  @Override
  public void perform(Player player, String[] args) {

    LangType langType = LangType.of(player);

    // Validate argument count
    if (!CommandExceptionHandler.validateArgCountRange(player, args, 2, 4, getSyntax())) {
      return;
    }

    // Determine territory type (town or region)
    TerritoryData territoryData;

    if (args[1].equals("town")) {
      territoryData = TownDataStorage.getInstance().getSync(player);
    } else if (args[1].equals("region")) {
      territoryData = RegionDataStorage.getInstance().getSync(player);
    } else {
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()).getDefault());
      return;
    }

    // Check if territory exists
    if (territoryData == null) {
      if (args[1].equals("town")) {
        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get().getDefault());
      } else {
        TanChatUtils.message(player, Lang.TOWN_NO_REGION.get().getDefault());
      }
      return;
    }

    // Handle chunk coordinates if provided
    if (args.length == 4) {
      // Parse coordinates with error handling
      Optional<Integer> xOpt = CommandExceptionHandler.parseInt(player, args[2], "x coordinate");
      Optional<Integer> zOpt = CommandExceptionHandler.parseInt(player, args[3], "z coordinate");

      if (xOpt.isEmpty() || zOpt.isEmpty()) {
        return;
      }

      try {
        Chunk chunk = player.getWorld().getChunkAt(xOpt.get(), zOpt.get());
        executeClaimChunk(territoryData, player, chunk);
        MapCommand.openMap(player, new MapSettings(args[0], args[1]));
      } catch (TerritoryException e) {
        TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
        CommandExceptionHandler.logCommandExecution(player, "claim", args);
      }
    } else {
      // Claim current chunk
      try {
        executeClaimChunk(territoryData, player);
      } catch (TerritoryException e) {
        TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
        CommandExceptionHandler.logCommandExecution(player, "claim", args);
      }
    }
  }

  /**
   * Executes the chunk claim operation.
   *
   * @param territory The territory claiming the chunk
   * @param player The player executing the claim
   * @throws TerritoryException If the claim operation fails
   */
  private void executeClaimChunk(TerritoryData territory, Player player) throws TerritoryException {
    try {
      territory.claimChunk(player);
    } catch (Exception e) {
      throw new TerritoryException("Chunk claim failed: " + e.getMessage(), e);
    }
  }

  /**
   * Executes the chunk claim operation at specific coordinates.
   *
   * @param territory The territory claiming the chunk
   * @param player The player executing the claim
   * @param chunk The chunk to claim
   * @throws TerritoryException If the claim operation fails
   */
  private void executeClaimChunk(TerritoryData territory, Player player, Chunk chunk)
      throws TerritoryException {
    try {
      territory.claimChunk(player, chunk);
    } catch (Exception e) {
      throw new TerritoryException("Chunk claim failed: " + e.getMessage(), e);
    }
  }
}
