package io.github.djxy.permissionManager.rules;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionManager.rules.cooldown.CooldownRule;
import io.github.djxy.permissionManager.rules.economy.EconomyRule;
import io.github.djxy.permissionManager.rules.home.HomeRule;
import io.github.djxy.permissionManager.rules.region.RegionRule;
import io.github.djxy.permissionManager.rules.time.TimeRule;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-16.
 */
public class Rules {

    public final static Rules instance = new Rules();

    private final ConcurrentHashMap<String, Class<? extends Rule>> rules = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends Rule>, String> ruleNames = new ConcurrentHashMap<>();

    private Rules() {
        addRule("cooldown", CooldownRule.class);
        addRule("economy", EconomyRule.class);
        addRule("home", HomeRule.class);
        addRule("region", RegionRule.class);
        addRule("time", TimeRule.class);
    }

    public void addRule(String name, Class<? extends Rule> rule){
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(rule);

        rules.put(name, rule);
        ruleNames.put(rule, name);
    }

    public String getName(Class<? extends Rule> clazz){
        Preconditions.checkNotNull(clazz);

        return ruleNames.get(clazz);
    }

    public Rule getRule(String name){
        Preconditions.checkNotNull(name);

        if(!rules.containsKey(name))
            return null;

        try {
            return rules.get(name).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}