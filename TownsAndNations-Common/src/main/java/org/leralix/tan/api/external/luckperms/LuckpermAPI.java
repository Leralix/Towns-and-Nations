package org.leralix.tan.api.external.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.leralix.tan.api.external.luckperms.context.TanContextCalculator;
import org.leralix.tan.storage.stored.*;

/**
 * The Main class of the Luckperms API integration into Towns and Nations
 * This class should only be called if Luckperms is installed. Otherwise, a {@link ClassNotFoundException} will be thrown
 */
public class LuckpermAPI {

    /**
     * Luckperms API endpoints
     */
    private final LuckPerms luckPerms;

    public LuckpermAPI(){
        this.luckPerms = LuckPermsProvider.get();
    }

    public void createContexts(
                               PlayerDataStorage playerDataStorage,
                               TownDataStorage townDataStorage,
                               RegionDataStorage regionDataStorage,
                               NationDataStorage nationDataStorage,
                               NewClaimedChunkStorage chunkStorage
    ){
        luckPerms.getContextManager().registerCalculator(
                new TanContextCalculator(
                        playerDataStorage,
                        townDataStorage,
                        regionDataStorage,
                        nationDataStorage,
                        chunkStorage
                )
        );
    }



}
