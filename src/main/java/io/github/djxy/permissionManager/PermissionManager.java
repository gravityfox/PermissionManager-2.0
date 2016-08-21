package io.github.djxy.permissionManager;

import com.google.inject.Inject;
import io.github.djxy.customCommands.CustomCommands;
import io.github.djxy.permissionManager.commands.CreateGroupCommand;
import io.github.djxy.permissionManager.events.PlayerLoginEvent;
import io.github.djxy.permissionManager.logger.Logger;
import io.github.djxy.permissionManager.logger.LoggerMode;
import io.github.djxy.permissionManager.promotion.Promotions;
import io.github.djxy.permissionManager.rules.region.RegionRuleService;
import io.github.djxy.permissionManager.rules.region.plugins.FoxGuardPlugin;
import io.github.djxy.permissionManager.rules.region.plugins.RedProtectPlugin;
import io.github.djxy.permissionManager.subjects.group.GroupCollection;
import io.github.djxy.permissionManager.subjects.user.UserCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * Created by Samuel on 2016-08-07.
 */
@Plugin(id = "permissionmanager", name = "PermissionManager v2", version = "2.0", authors = {"Djxy"})
public class PermissionManager {

    private static final Logger LOGGER = new Logger(PermissionManager.class);

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        return instance;
    }

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path path;

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;

        Sponge.getEventManager().registerListeners(this, new PlayerLoginEvent());

        Logger.setLoggerMode(LoggerMode.DEBUG_SERVER);

        if(Sponge.getPluginManager().isLoaded("foxguard"))
            RegionRuleService.instance.addRegionPlugin(new FoxGuardPlugin());
        if(Sponge.getPluginManager().isLoaded("br.net.fabiozumbi12.redprotect"))
            RegionRuleService.instance.addRegionPlugin(new RedProtectPlugin());

        UserCollection.instance.setDirectory(path.resolve("users"));

        GroupCollection.instance.setDirectory(path.resolve("groups"));
        GroupCollection.instance.load();
        GroupCollection.instance.createDefaultGroup();

        Promotions.instance.setDirectory(path.resolve("promotions"));
        Promotions.instance.load();
    }

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event){
        Sponge.getServiceManager().setProvider(this, org.spongepowered.api.service.permission.PermissionService.class, PermissionService.instance);

        LOGGER.info("PermissionService is now enable.");
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event){
        CustomCommands.registerObject(new CreateGroupCommand());
    }

    @Listener
    public void onGameStoppedServerEvent(GameStoppedServerEvent event){
        LOGGER.info("PermissionService is about to save the subjects.");
        UserCollection.instance.save();
        GroupCollection.instance.save();
    }

}
