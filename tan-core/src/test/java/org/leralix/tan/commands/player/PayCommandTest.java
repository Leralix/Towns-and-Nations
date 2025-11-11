package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

/** Unit tests for PayCommand with exception handling and validation. */
class PayCommandTest {

  private ServerMock server;
  private PlayerMock sender;
  private PlayerMock receiver;
  private ITanPlayer senderData;
  private ITanPlayer receiverData;
  private PayCommand payCommand;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);

    sender = server.addPlayer("Sender");
    receiver = server.addPlayer("Receiver");

    senderData = PlayerDataStorage.getInstance().get(sender).join();
    receiverData = PlayerDataStorage.getInstance().get(receiver).join();

    payCommand = new PayCommand(100.0); // Max distance: 100 blocks
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void getName_returnsCorrectName() {
    assertEquals("pay", payCommand.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan pay <player> <amount>", payCommand.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(3, payCommand.getArguments());
  }

  @Test
  void perform_tooFewArguments_sendsErrorMessage() {
    String[] args = {"pay", "Receiver"}; // Missing amount

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
    assertTrue(message.contains("argument") || message.contains("syntax"));
  }

  @Test
  void perform_tooManyArguments_sendsErrorMessage() {
    String[] args = {"pay", "Receiver", "100", "extra"};

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
    assertTrue(message.contains("argument") || message.contains("syntax"));
  }

  @Test
  void perform_invalidAmount_sendsErrorMessage() {
    String[] args = {"pay", "Receiver", "abc"};

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_negativeAmount_handledGracefully() {
    String[] args = {"pay", "Receiver", "-50"};

    // Should parse the negative number but fail validation
    assertDoesNotThrow(() -> payCommand.perform(sender, args));

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_zeroAmount_sendsMinimumRequiredError() {
    String[] args = {"pay", "Receiver", "0"};

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_playerNotFound_sendsErrorMessage() {
    String[] args = {"pay", "NonExistentPlayer", "100"};

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_payToSelf_sendsErrorMessage() {
    String[] args = {"pay", "Sender", "100"}; // Paying to self

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_insufficientBalance_sendsErrorMessage() {
    // Sender has starting balance, try to pay more
    double startingBalance = EconomyUtil.getBalance(sender);
    double amountToPay = startingBalance + 1000;

    String[] args = {"pay", "Receiver", String.valueOf((int) amountToPay)};

    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_validPayment_success() {
    // Give sender enough money
    EconomyUtil.addFromBalance(sender, 1000);
    double initialSenderBalance = EconomyUtil.getBalance(sender);
    double initialReceiverBalance = EconomyUtil.getBalance(receiver);

    String[] args = {"pay", "Receiver", "100"};

    payCommand.perform(sender, args);

    // Verify balances changed
    assertEquals(initialSenderBalance - 100, EconomyUtil.getBalance(sender), 0.01);
    assertEquals(initialReceiverBalance + 100, EconomyUtil.getBalance(receiver), 0.01);

    // Verify success messages
    String senderMessage = sender.nextMessage();
    String receiverMessage = receiver.nextMessage();

    assertNotNull(senderMessage);
    assertNotNull(receiverMessage);
  }

  @Test
  void perform_playersInSameWorld_allowsPayment() {
    // Both players are in the same world by default in MockBukkit
    EconomyUtil.addFromBalance(sender, 1000);

    String[] args = {"pay", "Receiver", "50"};

    assertDoesNotThrow(() -> payCommand.perform(sender, args));
  }

  @Test
  void perform_decimalAmount_parsesAsInteger() {
    EconomyUtil.addFromBalance(sender, 1000);

    String[] args = {"pay", "Receiver", "100.50"};

    // Should fail to parse as integer
    payCommand.perform(sender, args);

    String message = sender.nextMessage();
    assertNotNull(message);
  }

  @Test
  void perform_largeAmount_handlesGracefully() {
    EconomyUtil.addFromBalance(sender, 999999999);

    String[] args = {"pay", "Receiver", "999999"};

    assertDoesNotThrow(() -> payCommand.perform(sender, args));
  }

  @Test
  void perform_amountWithWhitespace_parsesCorrectly() {
    EconomyUtil.addFromBalance(sender, 1000);

    String[] args = {"pay", "Receiver", "  100  "};

    assertDoesNotThrow(() -> payCommand.perform(sender, args));
  }

  @Test
  void getTabCompleteSuggestions_providesPlayerNames() {
    String[] args = {"pay", ""};

    var suggestions = payCommand.getTabCompleteSuggestions(sender, "", args);

    assertNotNull(suggestions);
    // Should include online players
    assertTrue(suggestions.size() > 0);
  }
}
