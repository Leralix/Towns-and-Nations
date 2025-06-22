package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.factory.AbstractionFactory;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SetMoneyTest {


    public static CommandSender sender;

    @BeforeAll
    static void initialise(){
        sender = Mockito.mock(CommandSender.class);
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void standardUse() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        SetMoney.setMoney(sender, new String[]{"setmoney", "FakePlayer", "10"}, fakePlayer);

        assertEquals(10, fakePlayer.getBalance());
    }

    @Test
    void negativeValue() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        SetMoney.setMoney(sender, new String[]{"addmoney", "FakePlayer", "-500"}, fakePlayer);

        assertEquals(-500, fakePlayer.getBalance());
    }


    @Test
    void wrongValue() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        SetMoney.setMoney(sender, new String[]{"addmoney", "FakePlayer", "50€"}, fakePlayer);

        assertEquals(100, fakePlayer.getBalance());
    }
}
