package org.leralix.tan.listeners.chat.events;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.ConquerWarGoal;

public class SelectNbChunksForConquer extends ChatListenerEvent {

  private final War war;
  private final Consumer<Player> fallback;
  private final WarRole warRole;

  public SelectNbChunksForConquer(War war, WarRole warRole, Consumer<Player> fallback) {
    this.war = war;
    this.warRole = warRole;
    this.fallback = fallback;
  }

  @Override
  protected boolean execute(Player player, String message) {
    Integer amount = parseStringToInt(message);
    if (amount == null) {
      TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
      return false;
    }

    int maxAmountOfChunkToCapture = Constants.getNbChunkToCaptureMax();
    if (amount > maxAmountOfChunkToCapture) {
      TanChatUtils.message(
          player,
          Lang.VALUE_EXCEED_MAXIMUM_ERROR.get(player, Integer.toString(maxAmountOfChunkToCapture)));
      return false;
    }

    war.addGoal(warRole, new ConquerWarGoal(amount));

    openGui(fallback, player);
    SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
    return true;
  }
}
