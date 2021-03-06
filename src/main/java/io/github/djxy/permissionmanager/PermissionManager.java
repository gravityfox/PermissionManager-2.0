package io.github.djxy.permissionmanager;

import com.google.inject.Inject;
import io.github.djxy.customcommands.CustomCommands;
import io.github.djxy.permissionmanager.commands.DebugCommands;
import io.github.djxy.permissionmanager.commands.GroupCommands;
import io.github.djxy.permissionmanager.commands.MenuCommands;
import io.github.djxy.permissionmanager.commands.PromotionCommands;
import io.github.djxy.permissionmanager.commands.UserCommands;
import io.github.djxy.permissionmanager.events.PlayerEvent;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.logger.LoggerMode;
import io.github.djxy.permissionmanager.promotion.Promotions;
import io.github.djxy.permissionmanager.rules.home.HomeRuleService;
import io.github.djxy.permissionmanager.rules.home.plugins.RedProtectPluginHome;
import io.github.djxy.permissionmanager.rules.region.RegionRuleService;
import io.github.djxy.permissionmanager.rules.region.plugins.FoxGuardPluginRegion;
import io.github.djxy.permissionmanager.rules.region.plugins.RedProtectPluginRegion;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import io.github.djxy.permissionmanager.util.FileConversionUtil;
import io.github.djxy.permissionmanager.util.ResourceUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
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
@Plugin(id = "permissionmanager", name = "PermissionManager", version = "2.0.1", authors = {"Djxy"})
public class PermissionManager {

    private static final Logger LOGGER = new Logger(PermissionManager.class);

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        return instance;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path path;
    public static Translator translator;

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;

        Logger.setLoggerMode(LoggerMode.DEBUG_SERVER);

        translator = ResourceUtil.loadTranslations();

        path.resolve("users").toFile().mkdirs();
        path.resolve("groups").toFile().mkdirs();
        path.resolve("promotions").toFile().mkdirs();

        FileConversionUtil.convertUsers(path);
        FileConversionUtil.convertGroups(path);
        FileConversionUtil.convertPromotions(path);

        Sponge.getEventManager().registerListeners(this, new PlayerEvent());

        if(Sponge.getPluginManager().isLoaded("foxguard"))
            RegionRuleService.instance.addRegionPlugin(new FoxGuardPluginRegion());
        if(Sponge.getPluginManager().isLoaded("br.net.fabiozumbi12.redprotect")) {
            RegionRuleService.instance.addRegionPlugin(new RedProtectPluginRegion());
            HomeRuleService.instance.addHomePlugin(new RedProtectPluginHome());
        }

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
        CustomCommands.registerObject(new DebugCommands(translator));
        CustomCommands.registerObject(new PromotionCommands(translator));
        CustomCommands.registerObject(new UserCommands(translator).register());
        CustomCommands.registerObject(new GroupCommands(translator).register());
        CustomCommands.registerObject(new MenuCommands(translator));

        Logger.setLoggerMode(LoggerMode.NO_LOG);
    }

    @Listener
    public void onGameStoppedServerEvent(GameStoppedServerEvent event){
        LOGGER.info("PermissionService is about to save the subjects.");
        UserCollection.instance.save();
        GroupCollection.instance.save();
        Promotions.instance.save();
    }

}
