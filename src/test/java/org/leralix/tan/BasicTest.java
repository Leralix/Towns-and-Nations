package org.leralix.tan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.leralix.lib.SphereLib;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public abstract class BasicTest {

    protected ServerMock server;
    protected SphereLib sphereLib;
    protected TownsAndNations townsAndNations;

    @BeforeEach
    protected void setUp() {
        server = MockBukkit.mock();
        sphereLib = MockBukkit.load(SphereLib.class);
        townsAndNations = MockBukkit.load(TownsAndNations.class);
    }

    @AfterEach
    protected void tearDown() {
        MockBukkit.unmock();
    }
}
