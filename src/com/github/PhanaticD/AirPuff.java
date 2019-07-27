package com.github.PhanaticD;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class AirPuff extends AirAbility implements AddonAbility
{
    private int cooldown;
    private int range;
    private Location location;
    private String configprefix;
    
    public AirPuff(final Player player) {
        //this code is run when this object is created, which we did in the listener when a player left clicks
        super(player);

        //set up variables
        cooldown = ConfigManager.getConfig().getInt(configprefix + "Cooldown", 2000);
        range = ConfigManager.getConfig().getInt(configprefix + "Range", 20);
        location = player.getEyeLocation();

        //start() will make progress() run every tick until remove() is called
        start();
    }
    
    @Override
    public long getCooldown() {
        return cooldown;
    }
    
    @Override
    public Location getLocation() {
        return location;
    }
    
    @Override
    public String getName() {
        return "AirPuff";
    }
    
    @Override
    public boolean isHarmlessAbility() {
        return false;
    }
    
    @Override
    public boolean isSneakAbility() {
        return false;
    }
    
    @Override
    public void progress() {
        //remove the move if they no longer have permission to use the move
        if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
            remove();
            return;
        }

        //assuming location is where your move is, remove the move when its in a area blocked from building by other plugins
        if (GeneralMethods.isRegionProtectedFromBuild(this, location)){
            remove();
            return;
        }

        //remove if the location of the move is out of range of the player
        if (!player.getWorld().equals(location.getWorld()) || player.getEyeLocation().distance(location) > range) {
            remove();
            return;
        }

        //move the location of the move forward
        location.add(player.getEyeLocation().getDirection());

        //this is to check that the move does not go through solid blocks
        if (!getTransparentMaterialSet().contains(location.getBlock().getType())){
            remove();
            return;
        }

        //show particles at the location of the move
        playAirbendingParticles(location, 1, 0.275F, 0.275F, 0.275F);

        //check for if your move actually "hits" something
        //this code will check for entities in a 1 block radius of its current location
        for (final Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 1)) {
            //skip players in protected areas or /b invincible toggled
            if (GeneralMethods.isRegionProtectedFromBuild(this, entity.getLocation()) || ((entity instanceof Player) && Commands.invincible.contains(((Player) entity).getName()))) {
                continue;
            }
            //damage mob then stop the move. making sure it does not damage yourself
            if (entity instanceof LivingEntity && entity.getEntityId() != player.getEntityId()) {
                DamageHandler.damageEntity(entity, 1, this);
                this.remove();
                return;
            }
        }
    }

    @Override
    public void remove(){
        super.remove();
        //add cooldown when the move is removed
        bPlayer.addCooldown(this);
    }
    
    @Override
    public String getAuthor() {
        return "YourNameHere";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Does Nothing";
    }

    @Override
    public String getInstructions() {
        return "Click to do nothing";
    }
    
    @Override
    public void load() {
        //registers the spigot events, we have a interact event called click
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new AirPuffListener(), ProjectKorra.plugin);

        ProjectKorra.log.info(getName() + " " + getVersion() + " by " + getAuthor() + " has been loaded!");

        //config
        configprefix = "ExtraAbilities." + getAuthor() + "." + getName() + ".";
        ConfigManager.getConfig().addDefault(configprefix + "Cooldown", 2000);
        ConfigManager.getConfig().addDefault(configprefix + "Range", 20);
        ConfigManager.defaultConfig.save();

        //permissions, default in this case will allow anyone to use the move
        if (Bukkit.getPluginManager().getPermission("bending.ability.AirPuff") == null) {
            Bukkit.getPluginManager().addPermission(new Permission("bending.ability.AirPuff"));
            Bukkit.getPluginManager().getPermission("bending.ability.AirPuff").setDefault(PermissionDefault.TRUE);
        }

        //register for collisions with other moves as a small ability
        ProjectKorra.getCollisionInitializer().addSmallAbility(this);
    }
    
    @Override
    public void stop() {
        ProjectKorra.log.info(getName() + " " + getVersion() + " by " + getAuthor() + " stopped! ");
        super.remove();
    }
    
}

