package org.leralix.tan.factory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.economy.TanEconomyStandalone;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class Factory {

    private static boolean isTanMockOn = false;
    private static final Map<String , Player> bukkitPlayerList = new HashMap<>();

    public static void initializeConfigs() {

        ClassLoader classLoader = Factory.class.getClassLoader();

        if(!isTanMockOn){
            initialisePluginMock(classLoader);
            isTanMockOn = true;
        }



        File config = new File(classLoader.getResource("fakeConfig.yml").getFile());
        ConfigUtil.addCustomConfig(config, ConfigTag.MAIN);
        File langConf = new File(classLoader.getResource("fakeLang.yml").getFile());
        ConfigUtil.addCustomConfig(langConf, ConfigTag.LANG);

        Lang.loadTranslations(new File(classLoader.getResource("lang").getFile()), "en", false);

        EconomyUtil.setEconomy(new TanEconomyStandalone());
    }

    private static void initialisePluginMock(ClassLoader classLoader) {
        TownsAndNations plugin = Mockito.mock(TownsAndNations.class);
        when(plugin.getDataFolder()).thenReturn(new File(classLoader.getResource("created").getFile()));
        MockedStatic<TownsAndNations> pluginInstance = Mockito.mockStatic(TownsAndNations.class);
        pluginInstance.when(TownsAndNations::getPlugin).thenReturn(plugin);

        MockedStatic<Bukkit> bukkitInstance = Mockito.mockStatic(Bukkit.class);
        bukkitInstance.when(() -> Bukkit.getPlayer(anyString()))
                .thenAnswer(invocation -> Factory.getRandomPlayer(invocation.getArgument(0)));
    }



    public static PlayerData getRandomPlayerData() {
        return PlayerDataStorage.getInstance().register(getRandomPlayer());
    }

    public static @NotNull Player getRandomPlayer() {
        Player player = Mockito.mock(Player.class);
        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getName()).thenReturn(uuid.toString());
        bukkitPlayerList.put(uuid.toString(), player);
        return player;
    }

    public static @NotNull Player getRandomPlayer(String playerName) {
        return bukkitPlayerList.get(playerName);
    }

}
