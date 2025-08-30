package me.sintaxlabs.bombasticProjectiles112.listeners;

import me.sintaxlabs.bombasticProjectiles112.main;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import static org.bukkit.Bukkit.getServer;

//Projectile Check Order
//----------------------
//Arrow
//Egg
//EnderPearl
//ExpBottle
//FishingBobber
//Potion
//Snowball
//Trident
//----------------------

public class launchCheck implements Listener
{

    public static class launchGlobal
    {
        public static boolean playerShotStarted;
        public static boolean mobShotStarted;
        public static boolean permProjectiles;
        public static boolean permBreakBlocks;
        public static boolean permHarmPlayers;
        public static boolean permHarmMobs;
        public static Player shooter;

    }
    @EventHandler
    public void projectileLaunchCheck (ProjectileLaunchEvent e)
    {
        launchGlobal.mobShotStarted = false;
        launchGlobal.playerShotStarted = false;
        var object = e.getEntity();
        var shooter = object.getShooter();

        if (shooter instanceof Player player)
        {
            launchGlobal.shooter = player;
            launchGlobal.playerShotStarted = true;





            if (main.Global.configToggleRequirePermission)
            {

                if (!player.hasPermission("bombasticProjectiles.projectiles"))
                {
                    if (main.Global.configToggleVerbose)
                    {
                        getServer().broadcastMessage("§e'bombasticProjectiles.projectiles' §cis required if Perms are required.");
                    }
                    return;
                }
                else
                {
                    launchGlobal.permProjectiles = true;

                    if (player.hasPermission("bombasticProjectiles.breakBlocks"))
                    {
                        launchGlobal.permBreakBlocks = true;
                    }
                    if (player.hasPermission("bombasticProjectiles.harmPlayers"))
                    {
                        launchGlobal.permHarmPlayers = true;
                    }
                    if (player.hasPermission("bombasticProjectiles.harmMobs"))
                    {
                        launchGlobal.permHarmMobs = true;
                    }
                }
            }
            //Arrow CHECK ----------------------------------------------------------
            if (e.getEntity() instanceof Arrow)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleArrow)
                    {invalidMessage();}
                }
            }

            //Egg CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof Egg)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleEgg)
                    {invalidMessage();}
                }
            }

            //EnderPearl CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof EnderPearl)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleEnderpearl)
                    {invalidMessage();}
                }
            }

            //ExpBottle CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof ThrownExpBottle)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleExpBottle)
                    {invalidMessage();}
                }
            }

            //FishingBobber CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof FishHook)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleFishingBobber)
                    {invalidMessage();}
                }
            }

            //Potion CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof ThrownPotion)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configTogglePotion)
                    {invalidMessage();}
                }
            }

            //Snowball CHECK ----------------------------------------------------------
            else if (e.getEntity() instanceof Snowball)
            {
                if (main.Global.configToggleVerbose)
                {
                    if (!main.Global.configToggleSnowball)
                    {invalidMessage();}
                }
            }
        }
        else
        {
            launchGlobal.mobShotStarted = true;
        }
    }
    private static void invalidMessage()
    {
        launchGlobal.playerShotStarted = false;
        getServer().broadcastMessage("§cThis item is not enabled for exploding.");
    }
}
