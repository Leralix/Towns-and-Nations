package org.leralix.tan.commands.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.enums.ClaimAction;
import org.leralix.tan.enums.ClaimType;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class MapCommand extends PlayerSubCommand {

  @Override
  public String getName() {
    return "map";
  }

  @Override
  public String getDescription() {
    return Lang.MAP_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan map";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return new ArrayList<>();
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 1) {
      openMap(player, new MapSettings());
      return;
    }
    if (args.length == 3) {
      openMap(player, new MapSettings(args[1], args[2]));
      return;
    }
    LangType langType = PlayerDataStorage.getInstance().getSync(player).getLang();
    TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType), SoundEnum.NOT_ALLOWED);
    TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
  }

  public static void openMap(Player player, MapSettings settings) {
    Chunk currentChunk = player.getLocation().getChunk();
    LangType langType = PlayerDataStorage.getInstance().getSync(player).getLang();
    int radius = 4;
    Map<Integer, Component> text = new HashMap<>();
    Component claimType =
        Component.text(Lang.MAP_CLAIM_TYPE.get(langType)).color(NamedTextColor.GRAY);
    text.put(-4, claimType);
    Component typeButton = settings.getMapTypeButton(langType);
    text.put(-3, typeButton);
    Component actionButton = settings.getClaimTypeButton(langType);
    text.put(-2, actionButton);

    // Envoi de l'en-tête
    player.sendMessage("╭─────────⟢⟐⟣─────────╮");
    for (int dz = -radius; dz <= radius; dz++) {
      Component newLine = Component.text("   ");
      for (int dx = -radius; dx <= radius; dx++) {
        int chunkX = currentChunk.getX();
        int chunkZ = currentChunk.getZ();

        chunkX += dx;
        chunkZ += dz;

        ClaimedChunk2 claimedChunk =
            NewClaimedChunkStorage.getInstance()
                .get(chunkX, chunkZ, player.getWorld().getUID().toString());
        Component icon = claimedChunk.getMapIcon(langType);

        if (dx == 0 && dz == 0) {

          if (claimedChunk instanceof TerritoryChunk territoryChunk
              && territoryChunk.isOccupied()) {
            icon =
                icon.hoverEvent(icon.hoverEvent())
                    .clickEvent(icon.clickEvent())
                    .replaceText(
                        builder ->
                            builder
                                .matchLiteral("🟧")
                                .replacement("🟠")); // Hashed orange square emoji
          } else {
            icon =
                icon.hoverEvent(icon.hoverEvent())
                    .clickEvent(icon.clickEvent())
                    .replaceText(
                        builder ->
                            builder
                                .match("⬛|⬜|✖")
                                .replacement(
                                    "🌑")); // For some reason, the only round emoji with the same
            // size as ⬛ is this emoji
          }
        }

        ClaimAction claimAction = settings.getClaimActionType();
        ClaimType mapType = settings.getClaimType();
        icon =
            icon.clickEvent(
                ClickEvent.runCommand(
                    "/tan "
                        + claimAction.toString().toLowerCase()
                        + " "
                        + mapType.toString().toLowerCase()
                        + " "
                        + chunkX
                        + " "
                        + chunkZ));
        newLine = newLine.append(icon);
      }
      if (text.containsKey(dz)) {
        newLine = newLine.append(text.get(dz));
      }
      player.sendMessage(newLine);
    }
    player.sendMessage("╰─────────⟢⟐⟣─────────╯");
  }
}
