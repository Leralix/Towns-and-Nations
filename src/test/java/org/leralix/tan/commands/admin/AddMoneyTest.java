package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.factory.AbstractionFactory;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AddMoneyTest {


    public static CommandSender sender;

    @BeforeAll
    static void initialise(){
        sender = Mockito.mock(CommandSender.class);
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void standardUse() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        AddMoney.addMoney(sender, new String[]{"addmoney", "FakePlayer", "100"}, fakePlayer);

        assertEquals(200, fakePlayer.getBalance());
    }

    @Test
    void negativeValue() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        AddMoney.addMoney(sender, new String[]{"addmoney", "FakePlayer", "-50"}, fakePlayer);

        assertEquals(50, fakePlayer.getBalance());
    }


    @Test
    void wrongValue() {
        ITanPlayer fakePlayer = AbstractionFactory.getRandomITanPlayer();

        AddMoney.addMoney(sender, new String[]{"addmoney", "FakePlayer", "50€"}, fakePlayer);

        assertEquals(100, fakePlayer.getBalance());
    }
}
