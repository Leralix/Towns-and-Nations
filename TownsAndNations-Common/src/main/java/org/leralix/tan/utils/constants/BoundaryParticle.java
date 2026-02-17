package org.leralix.tan.utils.constants;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.war.info.BoundaryType;

import java.util.EnumMap;
import java.util.Map;

public class BoundaryParticle {

    private final Map<BoundaryType, Particle> boundaries;

    public BoundaryParticle(ConfigurationSection config){
        boundaries = new EnumMap<>(BoundaryType.class);
        for(BoundaryType type : BoundaryType.values()){
            String value = config.getString(type.toString());
            boundaries.put(type, fromString(value));
        }
    }

    private Particle fromString(String particleName){
        try{
            return Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            return Particle.COMPOSTER;
        }
    }

    public Particle getParticle(BoundaryType type){
        return boundaries.get(type);
    }

}
