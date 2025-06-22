package org.leralix.tan.newsletter.storage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.newsletter.news.RegionCreationNews;
import org.leralix.tan.newsletter.news.TownCreatedNews;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.database.SQLiteHandler;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class NewsletterStorageTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
        File dataFolder = new File("src/test/resources");
        DatabaseHandler databaseHandler = new SQLiteHandler(dataFolder + "/database/main.db");
        try {
            databaseHandler.connect();
        } catch (SQLException e) {
            Logger.getLogger("test").log(Level.SEVERE,"[TaN] Error while connecting to the database");
        }
    }

    @Test
    void testCreateTownNewsletter() {
        NewsletterStorage.init();

        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", ITanPlayer);

        NewsletterStorage.register(new TownCreatedNews(townData.getID(), ITanPlayer.getID()));

    }



}