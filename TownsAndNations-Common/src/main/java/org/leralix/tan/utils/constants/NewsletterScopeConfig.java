package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.events.newsletter.EventScope;
import org.leralix.tan.events.newsletter.NewsletterType;

import java.util.EnumMap;
import java.util.Map;

public class NewsletterScopeConfig {

    private final EnumMap<NewsletterType, SingleNewsletterConfig> config;

    /**
     * Initialisation of the constants
     * @param configurationSection the "event" section of the config file.
     */
    public NewsletterScopeConfig(ConfigurationSection configurationSection){

        this.config = new EnumMap<>(NewsletterType.class);

        for (NewsletterType type : NewsletterType.values()) {

            EventScope broadcast = EventScope.valueOf(configurationSection.getString(type.name() + ".BROADCAST", "NONE"));
            EventScope newsletter = EventScope.valueOf(configurationSection.getString(type.name() + ".NEWSLETTER", "NONE"));

            config.put(type, new SingleNewsletterConfig(broadcast, newsletter));
        }
    }

    public Map<NewsletterType, SingleNewsletterConfig> getConfig() {
        return config;
    }

    public record SingleNewsletterConfig(EventScope broadcast, EventScope newsletter) { }
}
