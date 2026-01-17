package org.leralix.tan.commands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.BasicTest;
import org.leralix.tan.commands.admin.AdminCommandManager;
import org.leralix.tan.commands.debug.DebugCommandManager;
import org.leralix.tan.commands.player.PlayerCommandManager;
import org.leralix.tan.commands.server.ServerCommandManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CommandPermissionConsistencyTest extends BasicTest {

    @Test
    void pluginYmlHasPermissionNodesForAllRegisteredSubCommands() throws IllegalAccessException {
        YamlConfiguration pluginYml = loadPluginYml();
        ConfigurationSection permissionsSection = pluginYml.getConfigurationSection("permissions");
        assertNotNull(permissionsSection, "plugin.yml must contain a permissions section");

        assertPermissionsExist(permissionsSection, "tan.base.commands", new PlayerCommandManager());
        assertPermissionsExist(permissionsSection, "tan.admin.commands", new AdminCommandManager());
        assertPermissionsExist(permissionsSection, "tan.admin.commands", new DebugCommandManager());
        assertPermissionsExist(permissionsSection, "tan.server.commands", new ServerCommandManager());
    }

    private static YamlConfiguration loadPluginYml() {
        InputStream in = CommandPermissionConsistencyTest.class.getClassLoader().getResourceAsStream("plugin.yml");
        assertNotNull(in, "plugin.yml must be on the test classpath");
        return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    private static void assertPermissionsExist(ConfigurationSection permissionsSection, String permissionRoot, CommandManager manager) throws IllegalAccessException {
        List<SubCommand> subCommands = extractSubCommands(manager);
        assertFalse(subCommands.isEmpty(), "No subcommands found for manager " + manager.getName());

        for (SubCommand subCommand : subCommands) {
            String subName = subCommand.getName();
            assertNotNull(subName, "Subcommand name cannot be null");
            String expectedPermission = permissionRoot + "." + subName;
            ConfigurationSection node = permissionsSection.getConfigurationSection(expectedPermission);
            assertNotNull(node, "Missing permission node in plugin.yml: " + expectedPermission);
        }
    }

    private static List<SubCommand> extractSubCommands(CommandManager manager) throws IllegalAccessException {
        for (Class<?> c = manager.getClass(); c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                Object v = f.get(manager);
                if (v == null) {
                    continue;
                }

                List<SubCommand> res = tryExtractFromValue(v);
                if (!res.isEmpty()) {
                    return res;
                }
            }
        }

        fail("Unable to find subcommands field on manager: " + Objects.requireNonNullElse(manager.getName(), manager.getClass().getName()));
        return List.of();
    }

    private static List<SubCommand> tryExtractFromValue(Object v) {
        List<SubCommand> res = new ArrayList<>();

        if (v instanceof Map<?, ?> map) {
            for (Object o : map.values()) {
                res.addAll(tryExtractFromValue(o));
            }
            return res;
        }

        if (v instanceof Collection<?> collection) {
            for (Object o : collection) {
                if (o instanceof SubCommand sc) {
                    res.add(sc);
                }
            }
            return res;
        }

        if (v instanceof Iterable<?> iterable) {
            for (Object o : iterable) {
                if (o instanceof SubCommand sc) {
                    res.add(sc);
                }
            }
            return res;
        }

        if (v instanceof Object[] array) {
            for (Object o : array) {
                if (o instanceof SubCommand sc) {
                    res.add(sc);
                }
            }
            return res;
        }

        if (v instanceof SubCommand sc) {
            return List.of(sc);
        }

        return res;
    }
}
