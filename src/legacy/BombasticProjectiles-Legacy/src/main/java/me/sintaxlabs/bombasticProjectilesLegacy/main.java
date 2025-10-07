////////////////////////////////////
///                              ///
///         Sintax Labs          ///
///                              ///
////////////////////////////////////

// https://github.com/MavenWare
// Supports: 1.12.x

//Dated: October 6, 2025


//Logic Structure:
//----------------
//1.    launchCheck

//      entityHurtCheck can occur at any point during 2a/2b

//2a.   mobProjEvent
//2b.   playerProjEvent


package me.sintaxlabs.bombasticProjectilesLegacy;
import me.sintaxlabs.bombasticProjectilesLegacy.listeners.*;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class main extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        saveDefaultConfig();
        loadProtectionLists();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new explosiveBreakEvents(), this);
        getServer().getPluginManager().registerEvents(new launchCheck(), this);
        getServer().getPluginManager().registerEvents(new entityHurtCheck(), this);
        getServer().getPluginManager().registerEvents(new mobProjectileHitEvent(), this);
        getServer().getPluginManager().registerEvents(new playerProjectileHitEvent(), this);

        // KEEP THESE IN ORDER OF CONFIG.
        Global.configToggleRequirePermission = this.getConfig().getBoolean("RequirePermission");
        Global.configImpactGlobal = this.getConfig().getInt("GlobalImpact");
        Global.configToggleImpactCustom = this.getConfig().getBoolean("CustomImpact");

        Global.configToggleFire = this.getConfig().getBoolean("Fire");
        Global.configToggleDropItems = this.getConfig().getBoolean("DropItems");
        Global.configToggleMobBreakBlocks = this.getConfig().getBoolean("MobBreakBlocks");
        Global.configTogglePlayerBreakBlocks = this.getConfig().getBoolean("PlayerBreakBlocks");
        Global.configToggleHurtMobs = this.getConfig().getBoolean("HurtMobs");
        Global.configToggleHurtPlayer = this.getConfig().getBoolean("HurtPlayer");
        Global.configToggleHurtNamedMobs = this.getConfig().getBoolean("HurtNamedMobs");

        Global.configToggleMobsExplodeEntities = this.getConfig().getBoolean("mobs-Explode-Entities");
        Global.configToggleMobsExplodeBlocks = this.getConfig().getBoolean("mobs-Explode-Blocks");
        Global.configToggleMobImpact = this.getConfig().getBoolean("MobImpact");
        Global.configImpactMob = this.getConfig().getInt("MobImpactDamage");

        Global.configToggleEgg = this.getConfig().getBoolean("Egg");
        Global.configToggleArrow = this.getConfig().getBoolean("Arrow");
        Global.configTogglePotion = this.getConfig().getBoolean("Potion");
        Global.configToggleSnowball = this.getConfig().getBoolean("Snowball");
        Global.configToggleExpBottle = this.getConfig().getBoolean("ExpBottle");
        Global.configToggleEnderpearl = this.getConfig().getBoolean("Enderpearl");
        Global.configToggleFishingBobber = this.getConfig().getBoolean("FishingBobber");

        Global.configImpactEgg = this.getConfig().getInt("Egg-Impact");
        Global.configImpactArrow = this.getConfig().getInt("Arrow-Impact");
        Global.configImpactPotion = this.getConfig().getInt("Potion-Impact");
        Global.configImpactSnowball = this.getConfig().getInt("Snowball-Impact");
        Global.configImpactExpBottle = this.getConfig().getInt("ExpBottle-Impact");
        Global.configImpactEnderpearl = this.getConfig().getInt("Enderpearl-Impact");
        Global.configImpactFishingBobber = this.getConfig().getInt("FishingBobber-Impact");

        Global.configToggleVerbose = this.getConfig().getBoolean("verbose");

        if (Global.configToggleVerbose)
        {
            getLogger().warning("BombasticProjectiles - Verbose is enabled. Not recommended for public servers.");
        }
    }



    public static class Global
    {
        // KEEP THESE IN ORDER OF CONFIG.
        public static boolean configToggleRequirePermission;
        public static int configImpactGlobal;
        public static boolean configToggleImpactCustom;

        public static boolean configToggleFire;
        public static boolean configToggleDropItems;
        public static boolean configTogglePlayerBreakBlocks;
        public static boolean configToggleMobBreakBlocks;
        public static boolean configToggleHurtMobs;
        public static boolean configToggleHurtPlayer;
        public static boolean configToggleHurtNamedMobs;

        public static boolean configToggleMobsExplodeEntities;
        public static boolean configToggleMobsExplodeBlocks;
        public static boolean configToggleMobImpact;
        public static int configImpactMob;

        public static boolean configToggleEgg;
        public static boolean configToggleArrow;
        public static boolean configTogglePotion;
        public static boolean configToggleSnowball;
        public static boolean configToggleExpBottle;
        public static boolean configToggleEnderpearl;
        public static boolean configToggleFishingBobber;

        public static int configImpactEgg;
        public static int configImpactArrow;
        public static int configImpactPotion;
        public static int configImpactSnowball;
        public static int configImpactExpBottle;
        public static int configImpactEnderpearl;
        public static int configImpactFishingBobber;

        public static boolean configToggleVerbose;

        // Used as flag for when an explosion happens.
        // Various checks ONLY occur during the millisecond of the explosion being flagged.
        public static boolean kaboom;

        public static Set<Material> protectedBlockList;
        public static Set<EntityType> protectedEntityList;
        public static Set<String> protectedWorldList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage("§eUsage: §6/bprojectile reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload"))
        {
            if (sender.hasPermission("bombasticProjectiles.reload"))
            {
                reloadConfig();
                refreshConfig();


                sender.sendMessage("§7[§6BombasticProjectiles§7] §aConfig reloaded.");
            } else {
                sender.sendMessage("§cYou don't have permission to do that.");
            }
            return true;
        }
        sender.sendMessage("§cUnknown subcommand.");
        return true;
    }

    private void refreshConfig()
    {
        // KEEP THESE IN ORDER OF CONFIG.
        Global.configToggleRequirePermission = this.getConfig().getBoolean("RequirePermission");
        Global.configImpactGlobal = this.getConfig().getInt("GlobalImpact");
        Global.configToggleImpactCustom = this.getConfig().getBoolean("CustomImpact");

        Global.configToggleFire = this.getConfig().getBoolean("Fire");
        Global.configToggleDropItems = this.getConfig().getBoolean("DropItems");
        Global.configToggleMobBreakBlocks = this.getConfig().getBoolean("MobBreakBlocks");
        Global.configTogglePlayerBreakBlocks = this.getConfig().getBoolean("PlayerBreakBlocks");
        Global.configToggleHurtMobs = this.getConfig().getBoolean("HurtMobs");
        Global.configToggleHurtPlayer = this.getConfig().getBoolean("HurtPlayer");
        Global.configToggleHurtNamedMobs = this.getConfig().getBoolean("HurtNamedMobs");

        Global.configToggleMobsExplodeEntities = this.getConfig().getBoolean("mobs-Explode-Entities");
        Global.configToggleMobsExplodeBlocks = this.getConfig().getBoolean("mobs-Explode-Blocks");
        Global.configToggleMobImpact = this.getConfig().getBoolean("MobImpact");
        Global.configImpactMob = this.getConfig().getInt("MobImpactDamage");

        Global.configToggleEgg = this.getConfig().getBoolean("Egg");
        Global.configToggleArrow = this.getConfig().getBoolean("Arrow");
        Global.configTogglePotion = this.getConfig().getBoolean("Potion");
        Global.configToggleSnowball = this.getConfig().getBoolean("Snowball");
        Global.configToggleExpBottle = this.getConfig().getBoolean("ExpBottle");
        Global.configToggleEnderpearl = this.getConfig().getBoolean("Enderpearl");
        Global.configToggleFishingBobber = this.getConfig().getBoolean("FishingBobber");

        Global.configImpactEgg = this.getConfig().getInt("Egg-Impact");
        Global.configImpactArrow = this.getConfig().getInt("Arrow-Impact");
        Global.configImpactPotion = this.getConfig().getInt("Potion-Impact");
        Global.configImpactSnowball = this.getConfig().getInt("Snowball-Impact");
        Global.configImpactExpBottle = this.getConfig().getInt("ExpBottle-Impact");
        Global.configImpactEnderpearl = this.getConfig().getInt("Enderpearl-Impact");
        Global.configImpactFishingBobber = this.getConfig().getInt("FishingBobber-Impact");

        Global.configToggleVerbose = this.getConfig().getBoolean("verbose");
        loadProtectionLists();
    }

    private void loadProtectionLists()
    {
        Set<Material> protectedBlocks = new HashSet<>();
        Set<EntityType> protectedEntities = new HashSet<>();
        Set<String> protectedWorlds = new HashSet<>();
        FileConfiguration config = getConfig();
        List<String> blockNames = config.getStringList("ProtectedBlocks");
        List<String> entityNames = config.getStringList("ProtectedEntities");
        List<String> worldNames = config.getStringList("ProtectedWorlds");

        for (String BlockName1 : blockNames)
        {
            try
            {
                Material material = Material.valueOf(BlockName1.toUpperCase());
                protectedBlocks.add(material);
            } catch (IllegalArgumentException e)
            {
                getLogger().warning("Invalid Block Type in config: " + BlockName1);
            }
        }

        for (String EntityName1 : entityNames)
        {
            try
            {
                EntityType entityy = EntityType.valueOf(EntityName1.toUpperCase());
                protectedEntities.add(entityy);
            } catch (IllegalArgumentException e)
            {
                getLogger().warning("Invalid Entity Type in config: " + EntityName1);
            }
        }

        for (String WorldName1 : worldNames)
        {
            try
            {
                protectedWorlds.add((WorldName1));
            } catch (IllegalArgumentException e)
            {
                getLogger().warning("Invalid World Name in config: " + WorldName1);
            }
        }

        Global.protectedBlockList = protectedBlocks;
        Global.protectedEntityList = protectedEntities;
        Global.protectedWorldList = protectedWorlds;
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}

//Cut Code to delete an entity and spawn it in the inventory player who caused the Event.
//
//pEventInfo.pEntity.remove();
//Inventory inv = pEventInfo.playerWhoShot.getInventory();
//inv.addItem(ItemStack.of(Material.ARROW, 1));
//
//Old Logic
//Player player = ((Player) shooter).getPlayer();
//assert player != null;