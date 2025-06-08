package org.leralix.tan.factory;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.economy.TanEconomyStandalone;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractionFactory {

    private static boolean isTanMockOn = false;
    private static final Map<String , Player> bukkitPlayerList = new HashMap<>();
    private static final Map<UUID , String> bukkitPlayerListByUUID = new HashMap<>();
    private static final Map<UUID , World> worldList = new HashMap<>();
    private static final Map<Vector2D, Chunk> chunkList = new HashMap<>();

    public static void initializeConfigs() {

        ClassLoader classLoader = AbstractionFactory.class.getClassLoader();

        if(!isTanMockOn){
            initialisePluginMock(classLoader);
            isTanMockOn = true;
        }

        File config = new File(classLoader.getResource("fakeConfig.yml").getFile());
        ConfigUtil.addCustomConfig(config, ConfigTag.MAIN);
        File langConf = new File(classLoader.getResource("fakeLang.yml").getFile());
        ConfigUtil.addCustomConfig(langConf, ConfigTag.LANG);

        Lang.loadTranslations(new File(classLoader.getResource("lang").getFile()), "en", false);

        EconomyUtil.register(new TanEconomyStandalone());
    }

    private static void initialisePluginMock(ClassLoader classLoader) {
        TownsAndNations plugin = Mockito.mock(TownsAndNations.class);
        when(plugin.getDataFolder()).thenReturn(new File(classLoader.getResource("created").getFile()));
        when(plugin.getDatabaseHandler()).thenReturn(Mockito.mock(DatabaseHandler.class));
        when(plugin.getDataFolder()).thenReturn(new File(classLoader.getResource("created").getFile()));

        SQLiteHandler sqliteHandler = new SQLiteHandler("src/test/resources/database/main.db");
        try {
            sqliteHandler.connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(plugin.getDatabaseHandler()).thenReturn(sqliteHandler);

        MockedStatic<TownsAndNations> pluginInstance = Mockito.mockStatic(TownsAndNations.class);
        pluginInstance.when(TownsAndNations::getPlugin).thenReturn(plugin);

        MockedStatic<Bukkit> bukkitInstance = Mockito.mockStatic(Bukkit.class);
        bukkitInstance.when(() -> Bukkit.getPlayer(anyString()))
                .thenAnswer(invocation -> AbstractionFactory.getRandomPlayer((String) invocation.getArgument(0)));

        bukkitInstance.when(() -> Bukkit.getPlayer(any(UUID.class)))
                .thenAnswer( invocation -> AbstractionFactory.getRandomPlayer((UUID) invocation.getArgument(0)));

        bukkitInstance.when(() -> Bukkit.getOfflinePlayer(any(UUID.class)))
                .thenAnswer( invocation -> AbstractionFactory.getRandomPlayer((UUID) invocation.getArgument(0)));


        bukkitInstance.when(Bukkit::getLogger).thenReturn(mock(Logger.class));
        bukkitInstance.when(Bukkit::getItemFactory).thenReturn(mock(ItemFactory.class));

        BukkitScheduler bukkitScheduler = Mockito.mock(BukkitScheduler.class);
        bukkitInstance.when(Bukkit::getScheduler).thenAnswer(invocation -> bukkitScheduler);


        World world = Mockito.mock(World.class);
        when(world.getName()).thenReturn("world");

        Block block = Mockito.mock(Block.class);
        when(world.getBlockAt(any())).thenReturn(block);

        bukkitInstance.when(() -> Bukkit.getWorld(any(UUID.class))).thenAnswer(invocation -> {
            UUID uuid = invocation.getArgument(0);
            return worldList.get(uuid);
        });
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
        bukkitPlayerListByUUID.put(uuid, uuid.toString());
        return player;
    }

    public static @NotNull Player getRandomPlayer(String playerName) {
        return bukkitPlayerList.get(playerName);
    }
    public static Player getRandomPlayer(UUID playerUUID) {
        return getRandomPlayer(bukkitPlayerListByUUID.get(playerUUID));
    }

    public static World createWorld(String worldName, UUID worldUUID) {
        World world = Mockito.mock(World.class);
        when(world.getName()).thenReturn(worldName);
        when(world.getUID()).thenReturn(worldUUID);
        when(world.getBlockAt(any())).thenReturn(Mockito.mock(Block.class));
        worldList.put(worldUUID, world);
        return world;
    }

    public static @NotNull Chunk createChunk(int x, int z, World world) {
        Chunk chunk = Mockito.mock(Chunk.class);
        when(chunk.getX()).thenReturn(x);
        when(chunk.getZ()).thenReturn(z);
        when(chunk.getWorld()).thenReturn(world);
        chunkList.put(new Vector2D(x, z, world.getUID().toString()), chunk);
        return chunk;
    }

    public static Location createLocation(double x, double y, double z, World world) {
        Location position = Mockito.mock(Location.class);
        when(position.getX()).thenReturn(x);
        when(position.getY()).thenReturn(y);
        when(position.getZ()).thenReturn(z);
        when(position.getWorld()).thenReturn(world);
        Vector2D vector2D = new Vector2D((int)x, (int)z, world.getUID().toString());
        when(position.getChunk()).thenAnswer(invocationOnMock -> chunkList.get(vector2D));
        return position;
    }
}
