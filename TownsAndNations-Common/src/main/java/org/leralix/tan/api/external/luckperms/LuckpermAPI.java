package org.leralix.tan.api.external.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.leralix.tan.api.external.luckperms.context.HasTownCalculator;

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

    public void createContexts(){
        System.out.println("Creating contexts...");
        luckPerms.getContextManager().registerCalculator(new HasTownCalculator());
    }



}
