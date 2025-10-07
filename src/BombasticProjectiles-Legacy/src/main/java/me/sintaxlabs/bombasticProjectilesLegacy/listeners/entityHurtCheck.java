package me.sintaxlabs.bombasticProjectilesLegacy.listeners;

import me.sintaxlabs.bombasticProjectilesLegacy.main;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import static org.bukkit.Bukkit.getServer;

//Logic Structure:
//----------------
//Check 1: If a mob shot, check damage toggles for players/mobs

//Logic Point: So a mob did NOT shoot

//Check 2: Player was hurt by basically another player. Check perms and toggles
//Check 3: Mob was hurt by a player, Check perms and toggles.
//----------------


public final class entityHurtCheck implements Listener
{
    public static class hurtGlobal
    {
        //So verbose can state the correct amount of mobs hurt by explosions.
        public static int mobCount = 1;
        public static int playerCount = 1;
        //Grabs the Entity so we can check it was a mob.
        public static Entity theMob;
        //Grabs the EntityType so we can properly name it in the verbose list of Mobs Hurt.
        public static EntityType theMobType;
        //Grabs the DamageCause so we can check it outside the nest.
        public static EntityDamageEvent.DamageCause theCause;
        //Grabs our Event so we can cancel it outside its own nest.
        public static EntityDamageEvent theEvent;
    }



    @EventHandler
    public void damageCheck (EntityDamageEvent e)
    {
        hurtGlobal.theEvent = e;
        // Refer to [main].
        // We check for which entities got hurt during the millisecond of the explosion being flagged.
        if (main.Global.kaboom)
        {
            //1.0.5 Fix for mobs getting blocked if perms were enabled.
            //First Check: If a mob shot, lets check damage toggles for players and mobs.
            if (launchCheck.launchGlobal.mobShotStarted)
            {
                if (e.getEntity() instanceof Player)
                {
                    toggleCheck_PlayerDamage();
                }
                else if (e.getEntity() instanceof Creature)
                {
                    hurtGlobal.theMob = e.getEntity();
                    hurtGlobal.theCause = e.getCause();
                    hurtGlobal.theMobType = e.getEntityType();

                    toggleCheck_MobDamage();
                }
            }
            //As of 1.0.5, this basically checks for players hurt by other players.
            //Second Check, okay so a player got hurt, lets run perm and toggle checks.
            else if (e.getEntity() instanceof Player)
            {
                if (!main.Global.configToggleRequirePermission)
                {toggleCheck_PlayerDamage();}
                else
                {
                    if (launchCheck.launchGlobal.permHarmPlayers)
                    {toggleCheck_PlayerDamage();}
                    else
                    {
                        e.setCancelled(true);
                        verbose_NoPermissionPlayerDamage();
                    }
                }

            }
            //As of 1.0.5, this basically checks for mobs hurt by players.
            //Third Check, okay so a mob got hurt by a player, lets run perm and toggle checks.
            else if (e.getEntity() instanceof Creature)
            {
                hurtGlobal.theMob = e.getEntity();
                hurtGlobal.theCause = e.getCause();
                hurtGlobal.theMobType = e.getEntity().getType();

                if (!main.Global.configToggleRequirePermission)
                {toggleCheck_MobDamage();}
                else
                {
                    if (launchCheck.launchGlobal.permHarmMobs)
                    {toggleCheck_MobDamage();}
                    else
                    {
                        e.setCancelled(true);
                        verbose_NoPermissionMobDamage();
                    }
                }
            }
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    // Check toggle for player damage.
    // If FALSE then cancel.
    private static void toggleCheck_PlayerDamage()
    {
        if (!main.Global.configToggleHurtPlayer)
        {
            hurtGlobal.theEvent.setCancelled(true);
            verbose_NoPlayerDamageToggle();
        }
        else
        {verbose_SuccessPlayerDamage();}
    }
    //------------------------------------------------------------------------------------------------------------------
    // Check toggle for mob damage.
    // If FALSE then cancel.
    private static void toggleCheck_MobDamage()
    {
        if (!main.Global.configToggleHurtNamedMobs)
        {
            if (hurtGlobal.theMob.getCustomName() != null)
            {
                hurtGlobal.theEvent.setCancelled(true);
                return;
            }
        }
        //1.0.7 Addition to protect named mobs.
        if (main.Global.protectedEntityList.contains(hurtGlobal.theEvent.getEntity().getType()))
        {
            hurtGlobal.theEvent.setCancelled(true);
            return;
        }
        if (!main.Global.configToggleHurtMobs)
        {
            hurtGlobal.theEvent.setCancelled(true);
            verbose_NoMobDamageToggle();
        }
        else
        {verbose_SuccessMobDamage();}
    }


    //------------------------------------------------------------------------------------------------------------------
    // Toggle Notice Messages
    private static void verbose_NoPlayerDamageToggle()
    {
        if (main.Global.configToggleVerbose)
        {
            //Prevents spamming warnings based on how many mobs the player attempted to hurt.
            while (hurtGlobal.playerCount == 1)
            {
                getServer().broadcastMessage("§eHurtCheck Notice §7- §cPlayer Damage is toggled off.");
                hurtGlobal.playerCount ++;
            }

        }
    }
    private static void verbose_NoMobDamageToggle()
    {
        if (main.Global.configToggleVerbose)
        {
            //Prevents spamming warnings based on how many mobs the player attempted to hurt.
            while (hurtGlobal.mobCount == 1)
            {
                getServer().broadcastMessage("§eHurtCheck Notice §7- §cMob Damage is toggled off.");
                hurtGlobal.mobCount ++;
            }

        }
    }
    //------------------------------------------------------------------------------------------------------------------
    //Success Damage Messages
    private static void verbose_SuccessPlayerDamage()
    {
        if (main.Global.configToggleVerbose)
        {
            while (hurtGlobal.playerCount == 1)
            {
                getServer().broadcastMessage("§aHurtCheck Success §7- §eA player received explosive damage.");
                hurtGlobal.playerCount ++;
            }
        }
    }
    private static void verbose_SuccessMobDamage()
    {
        if (main.Global.configToggleVerbose)
        {
            if(hurtGlobal.theMob instanceof Creature)
            {
                if (hurtGlobal.theCause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                {
                    //Placed a cap on how many mobs it can list to prevent spam/lag.
                    if (hurtGlobal.mobCount < 11)
                    {
                        getServer().broadcastMessage("§aInjured§7: §e" + hurtGlobal.theMobType + " §7(§6" + hurtGlobal.mobCount + "§7)");
                        hurtGlobal.mobCount ++;
                    }

                }
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    //Permission Issue Messages
    private static void verbose_NoPermissionPlayerDamage()
    {
        if (main.Global.configToggleVerbose)
        {
            while (hurtGlobal.playerCount == 1)
            {
                getServer().broadcastMessage("§cHurtCheck Error §7- §eYou do not have permission to hurt players.");
                hurtGlobal.playerCount ++;
            }
        }
    }
    private static void verbose_NoPermissionMobDamage()
    {
        if (main.Global.configToggleVerbose)
        {
            //Prevents spamming warnings based on how many mobs the player attempted to hurt.
            while (hurtGlobal.mobCount == 1)
            {
                getServer().broadcastMessage("§cHurtCheck Error §7- §eYou do not have permission to hurt mobs.");
                hurtGlobal.mobCount++;
            }


        }
    }
}
