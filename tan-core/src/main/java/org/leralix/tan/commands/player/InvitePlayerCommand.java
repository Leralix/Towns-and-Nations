package org.leralix.tan.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.utils.ChatUtils;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class InvitePlayerCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "invite";
  }

  @Override
  public String getDescription() {
    return Lang.TOWN_INVITE_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/tan invite <playerName>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    List<String> suggestions = new ArrayList<>();
    if (args.length == 2) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        suggestions.add(p.getName());
      }
    }
    return suggestions;
  }

  @Override
  public void perform(Player player, String[] args) {
    // Validate argument count
    if (!CommandExceptionHandler.validateArgCount((CommandSender) player, args, 2, getSyntax())) {
      return;
    }

    invite(player, args[1]);
  }

  private static void invite(Player player, String playerToInvite) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    TownData townData = tanPlayer.getTownSync();
    LangType langType = tanPlayer.getLang();

    if (townData == null) {
      TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
      return;
    }

    boolean hasPermission =
        townData.doesPlayerHavePermission(tanPlayer, RolePermission.INVITE_PLAYER);
    if (!hasPermission) {
      TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
      return;
    }

    // Find and validate player
    Optional<OfflinePlayer> offlinePlayerOpt =
        CommandExceptionHandler.findPlayer((CommandSender) player, playerToInvite);
    if (offlinePlayerOpt.isEmpty()) {
      return;
    }

    // Check if player is online
    Player invite = offlinePlayerOpt.get().getPlayer();
    if (invite == null) {
      TanChatUtils.message(player, Lang.PLAYER_NOT_FOUND.get(langType));
      return;
    }

    if (townData.isFull()) {
      TanChatUtils.message(player, Lang.INVITATION_TOWN_FULL.get(langType));
      return;
    }

    ITanPlayer inviteStat = PlayerDataStorage.getInstance().getSync(invite);
    if (inviteStat.getTownId() != null) {
      if (inviteStat.getTownId().equals(townData.getID())) {
        TanChatUtils.message(
            player, Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get(langType, invite.getName()));
        return;
      }
      TownData inviteStatTown = inviteStat.getTownSync();
      TanChatUtils.message(
          player,
          Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.get(
              langType, invite.getName(), inviteStatTown.getName()));
      return;
    }

    TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(), townData.getID());

    TanChatUtils.message(player, Lang.INVITATION_SENT_SUCCESS.get(langType, invite.getName()));

    LangType receiverLang = inviteStat.getLang();
    TanChatUtils.message(
        invite, Lang.INVITATION_RECEIVED_1.get(receiverLang, player.getName(), townData.getName()));
    ChatUtils.sendClickableCommand(
        invite, Lang.INVITATION_RECEIVED_2.get(receiverLang), "tan join " + townData.getID());
  }
}
