package org.leralix.tan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.leralix.lib.SphereLib;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public abstract class BasicTest {

    protected ServerMock server;
    protected SphereLib sphereLib;
    protected TownsAndNations townsAndNations;
    protected PlayerDataStorage playerDataStorage;
    protected LangType langType = LangType.ENGLISH;

    @BeforeEach
    protected void setUp() {
        server = MockBukkit.mock();
        sphereLib = MockBukkit.load(SphereLib.class);
        townsAndNations = MockBukkit.load(TownsAndNations.class);
        playerDataStorage = townsAndNations.getPlayerDataStorage();
    }

    @AfterEach
    protected void tearDown() {
        MockBukkit.unmock();
    }
}
