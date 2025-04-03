package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.factory.Factory;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SetMoneyTest {


    public static CommandSender sender;

    @BeforeAll
    static void initialise(){
        sender = Mockito.mock(CommandSender.class);
        Factory.initializeConfigs();
    }

    @Test
    void standardUse() {
        PlayerData fakePlayer = Factory.getRandomPlayerData();

        SetMoney.setMoney(sender, new String[]{"setmoney", "FakePlayer", "10"}, fakePlayer);

        assertEquals(10, fakePlayer.getBalance());
    }

    @Test
    void negativeValue() {
        PlayerData fakePlayer = Factory.getRandomPlayerData();

        SetMoney.setMoney(sender, new String[]{"addmoney", "FakePlayer", "-500"}, fakePlayer);

        assertEquals(-500, fakePlayer.getBalance());
    }


    @Test
    void wrongValue() {
        PlayerData fakePlayer = Factory.getRandomPlayerData();

        SetMoney.setMoney(sender, new String[]{"addmoney", "FakePlayer", "50€"}, fakePlayer);

        assertEquals(100, fakePlayer.getBalance());
    }
}
