package io.github.djxy.permissionManager.rules.region.plugins;

import io.github.djxy.permissionManager.logger.Logger;
import io.github.djxy.permissionManager.rules.region.RegionPlugin;
import net.foxdenstudio.sponge.foxguard.plugin.compat.djxy.pm.FGCompat;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

/**
 * Created by Samuel on 2016-08-19.
 */
public class FoxGuardPlugin implements RegionPlugin {

    private static final Logger LOGGER = new Logger(FoxGuardPlugin.class);

    @Override
    public boolean isPlayerInRegion(Player player, Collection<String> regions) {
        // I assume this is an OR operation, not an AND operation,
        // so if they are in at least one of the regions, this method should return true.
        // Invert this if that's not the case. -gravityfox
        for (String region : regions) {
            if (FGCompat.isPlayerInRegion(player, region)) return true;
        }
        return false;
    }

}
