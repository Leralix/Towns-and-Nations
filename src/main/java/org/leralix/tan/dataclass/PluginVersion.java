package org.leralix.tan.dataclass;

/**
 * This class is used to store the version of the plugin and compare it to other versions.
 */
public class PluginVersion {

    /**
     * Major updates, maybe one day
     */
    private final Integer major;
    /**
     * Minor updates, usually new features
     */
    private final Integer minor;
    /**
     * Patch updates, usually bug fixes and small quality of life updates
     */
    private final Integer patch;


    public PluginVersion(Integer major, Integer minor, Integer patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    public PluginVersion(String version){
        if(version.startsWith("v"))
            version = version.substring(1);

        String[] split = version.split("\\.");
        this.major = Integer.parseInt(split[0]);
        this.minor = Integer.parseInt(split[1]);
        this.patch = Integer.parseInt(split[2]);
    }


    public boolean isOlderThan(PluginVersion version){
        if(this.major < version.major)
            return true;
        if(this.major > version.major)
            return false;
        if(this.minor < version.minor)
            return true;
        if(this.minor > version.minor)
            return false;
        return this.patch < version.patch;
    }

    @Override
    public String toString(){
        return "v" + major + "." + minor + "." + patch;
    }
}
