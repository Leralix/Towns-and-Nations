package org.tan.towns_and_nations;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.Tasks.TaxPayments;
import org.tan.towns_and_nations.Tasks.TestTask;
import org.tan.towns_and_nations.commands.CommandManager;
import org.tan.towns_and_nations.commands.DebugCommand;
import org.tan.towns_and_nations.listeners.*;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.utils.ConfigUtil;
import org.tan.towns_and_nations.utils.TeamUtils;

import java.util.*;
import java.util.logging.Logger;

public final class TownsAndNations extends JavaPlugin {

    private static TownsAndNations plugin;
    static Logger logger;
    public ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        getLogger().info("\u001B[33m----------------Towns & Nations------------------\u001B[0m");

        logger.info("[TaN] Loading Plugin");

        logger.info("[TaN] -Loading Configs");

        ConfigUtil.loadCustomConfig("config.yml");
        ConfigUtil.saveResource("townLevelUpRequirement.yml");
        ConfigUtil.loadCustomConfig("townLevelUpRequirement.yml");

        logger.info("[TaN] -Loading Lang");
        Lang.loadTranslations("english.yml");
        logger.info(Lang.LANGUAGE_SUCCESSFULLY_LOADED.getTranslation());

        logger.info("[TaN] -Loading Stats");

        //Loading data
        PlayerStatStorage.loadStats();
        TownDataStorage.loadStats();
        ClaimedChunkStorage.loadStats();

        logger.info("[TaN] -Loading Scheduled commands");
        TestTask.startSchedule();
        TaxPayments.scheduleMidnightTask();







        //API luckperms
        /*
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
        }
        */

        EnableEventList();
        Objects.requireNonNull(getCommand("tan")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("tandebug")).setExecutor(new DebugCommand());

        logger.info("[TaN] Loading ProtocolLib");

        System.out.println(ProtocolLibrary.getProtocolManager());

        protocolManager = ProtocolLibrary.getProtocolManager();

        if(protocolManager == null) {
            getLogger().severe("Failed to get ProtocolManager from ProtocolLib. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                logger.info("[TaN] Test dans protocolLib");

                //TeamUtils.handlePlayerInfoPacket(event);
            }
        });



        
        logger.info("[TaN] Plugin successfully loaded");
    }


    @Override
    public void onDisable() {
        logger.info("[TaN] Savings Data");

        TownDataStorage.saveStats();
        ClaimedChunkStorage.saveStats();
        PlayerStatStorage.saveStats();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("[TaN] Plugin disabled");
    }


    private void EnableEventList() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new BreakBlockListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public static TownsAndNations getPlugin() {
        return plugin;
    }

    public static Logger getPluginLogger() {
        return logger;
    }




}