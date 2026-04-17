package org.leralix.tan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.leralix.lib.SphereLib;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public abstract class BasicTest {

    protected ServerMock server;
    protected SphereLib sphereLib;
    protected TownsAndNations townsAndNations;
    protected PlayerDataStorage playerDataStorage;
    protected TownStorage townStorage;
    protected RegionStorage regionStorage;
    protected NationStorage nationStorage;
    protected LandmarkStorage landmarkStorage;
    protected FortStorage fortDataStorage;
    protected ClaimStorage claimStorage;
    protected LangType langType = LangType.ENGLISH;

    @BeforeEach
    protected void setUp() {
        server = MockBukkit.mock();
        sphereLib = MockBukkit.load(SphereLib.class);
        townsAndNations = MockBukkit.load(TownsAndNations.class);
        playerDataStorage = townsAndNations.getPlayerDataStorage();
        regionStorage = townsAndNations.getRegionStorage();
        nationStorage = townsAndNations.getNationStorage();
        townStorage = townsAndNations.getTownStorage();
        landmarkStorage = townsAndNations.getLandmarkStorage();
        claimStorage = townsAndNations.getClaimStorage();
        fortDataStorage = townsAndNations.getFortStorage();
    }

    @AfterEach
    protected void tearDown() {
        MockBukkit.unmock();
    }
}
