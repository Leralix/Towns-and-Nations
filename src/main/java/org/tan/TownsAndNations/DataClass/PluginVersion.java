package org.tan.TownsAndNations.DataClass;

/**
 * This class is used to store the version of the plugin and compare it to other versions.
 */
public class PluginVersion {

    /**
     * Major updates, maybe one day
     */
    private final Integer MAJOR;
    /**
     * Minor updates, usually new features
     */
    private final Integer MINOR;
    /**
     * Patch updates, usually bug fixes and small quality of life updates
     */
    private final Integer PATCH;


    public PluginVersion(Integer MAJOR, Integer MINOR, Integer PATCH) {
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
        this.PATCH = PATCH;
    }
    public PluginVersion(String version){
        if(version.startsWith("v"))
            version = version.substring(1);

        String[] split = version.split("\\.");
        this.MAJOR = Integer.parseInt(split[0]);
        this.MINOR = Integer.parseInt(split[1]);
        this.PATCH = Integer.parseInt(split[2]);
    }


    public boolean isOlderThan(PluginVersion version){
        if(this.MAJOR < version.MAJOR)
            return true;
        if(this.MAJOR > version.MAJOR)
            return false;
        if(this.MINOR < version.MINOR)
            return true;
        if(this.MINOR > version.MINOR)
            return false;
        return this.PATCH < version.PATCH;
    }
    public boolean isOlderThan(String version){
        return isOlderThan(new PluginVersion(version));
    }

    @Override
    public String toString(){
        return "v" + MAJOR + "." + MINOR + "." + PATCH;
    }
}
