package io.github.djxy.permissionmanager.rules.economy;

import io.github.djxy.permissionmanager.rules.CommandRule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Samuel on 2016-08-16.
 */
public class EconomyRule extends CommandRule {

    private double cost;

    public EconomyRule() {
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean canApply(Player player) {
        if(!super.isFromCommand(player))
            return true;

        Optional<EconomyService> service = Sponge.getServiceManager().provide(EconomyService.class);

        if(!service.isPresent())
            return false;

        EconomyService economyService = service.get();
        Account account = economyService.getOrCreateAccount(player.getUniqueId()).get();

        return account.getBalance(economyService.getDefaultCurrency()).doubleValue() >= cost;
    }

    @Override
    public void apply(Player player) {
        if(!isFromCommand(player))
            return;

        Optional<EconomyService> service = Sponge.getServiceManager().provide(EconomyService.class);

        if(!service.isPresent())
            return;

        EconomyService economyService = service.get();
        Account account = economyService.getOrCreateAccount(player.getUniqueId()).get();

        account.withdraw(economyService.getDefaultCurrency(), BigDecimal.valueOf(cost), Cause.of(NamedCause.source(this)));
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        cost = node.getNode("cost").getDouble(0);
        super.deserialize(node);
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.getNode("cost").setValue(cost);
        super.serialize(node);
    }

}
