package org.leralix.tan.api.external.papi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.api.external.papi.entries.OtherPlayerChatMode;
import org.leralix.tan.api.external.papi.entries.PapiEntry;
import org.leralix.tan.api.external.papi.entries.PlayerBalance;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.ChatScope;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

class PlaceHolderAPITest extends BasicTest {

  private PlaceHolderAPI placeHolderAPI;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    placeHolderAPI = new PlaceHolderAPI();
  }

  @Test
  void removePlaceholderTest_noChanges() {

    String name = "player_balance";
    String correctedName = placeHolderAPI.removePlaceholder(name);

    assertEquals(name, correctedName);
  }

  @Test
  void removePlaceholderTest_withChanges() {

    String name = "territory_with_id_{T26}_exist";
    String wantedName = "territory_with_id_{}_exist";

    String correctedName = placeHolderAPI.removePlaceholder(name);

    assertEquals(wantedName, correctedName);
  }

  @Test
  void removePlaceholderTest_withMultipleChanges() {

    String name = "territory_with_id_{T26}_and_{T27}_exist";
    String wantedName = "territory_with_id_{}_and_{}_exist";

    String correctedName = placeHolderAPI.removePlaceholder(name);

    assertEquals(wantedName, correctedName);
  }

  @Test
  void onRequestMiss() {

    Player player = server.addPlayer();

    PapiEntry papiEntry = new PlayerBalance();
    placeHolderAPI.registerEntry(papiEntry);

    assertEquals(
        PlaceHolderAPI.PLACEHOLDER_NOT_FOUND,
        placeHolderAPI.onRequest(player, "player_not_balance"));
  }

  @Test
  void onRequestHit() {

    Player player = server.addPlayer();

    placeHolderAPI.registerEntry(new PlayerBalance());

    assertNotEquals(
        PlaceHolderAPI.PLACEHOLDER_NOT_FOUND, placeHolderAPI.onRequest(player, "player_balance"));
  }

  @Test
  void onRequestHitWithParam() {

    Player player = server.addPlayer("playerName");
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();

    placeHolderAPI.registerEntry(new OtherPlayerChatMode());

    assertEquals(
        ChatScope.GLOBAL.getName(tanPlayer.getLang()),
        placeHolderAPI.onRequest(player, "chat_mode_{playerName}"));

    LocalChatStorage.setPlayerChatScope(player, ChatScope.ALLIANCE);

    assertEquals(
        ChatScope.ALLIANCE.getName(tanPlayer.getLang()),
        placeHolderAPI.onRequest(player, "chat_mode_{playerName}"));
  }
}
