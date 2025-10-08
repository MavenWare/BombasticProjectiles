package me.sintaxlabs.bombasticProjectiles.listeners;

import me.sintaxlabs.bombasticProjectiles.main;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
//WindCharge
//----------------------

public final class playerProjectileHitEvent implements Listener
{
    public static class pEventInfo
    {
        public static Location pLocation;
        public static World pTarget;
        public static Entity pEntity;
        public static int pDamageType;
        public static boolean goodToBreakBlocks;
        public static int pImpactValue;
        public static String pString;
        public static Player playerWhoShot;
        public static ItemStack pItem;
    }

    @EventHandler
    public void playerProjectileHitCheck(ProjectileHitEvent e)
    {
        //If the player hit their self, cancel the Event.
        if(e.getHitEntity() == launchCheck.launchGlobal.shooter)
        {
            launchCheck.launchGlobal.playerShotStarted = false;
            return;
        }
        //If the player is the shooter, lets begin.
        if (e.getEntity().getShooter() instanceof Player user)
        {
            //If it's confirmed In launchCheck that the player shot then lets continue.
            if (launchCheck.launchGlobal.playerShotStarted)
            {
                //1.0.7 Addition
                //This will make the player the owner of the explosion so they can get XP from ores
                //This also prevents WorldGuard destruction.
                pEventInfo.playerWhoShot = user;

                if (main.Global.configToggleRequirePermission)
                {
                    if (!user.hasPermission("bombasticProjectiles.projectiles"))
                    {
                        return;
                    }
                    //Okay so permission is required and enabled, and they have the Projectiles perm.
                    //If both conditions are true then the explosion can break blocks. Otherwise, cannot.
                    pEventInfo.goodToBreakBlocks = launchCheck.launchGlobal.permBreakBlocks && main.Global.configTogglePlayerBreakBlocks;
                    if (!pEventInfo.goodToBreakBlocks)
                    {
                        if (main.Global.configToggleVerbose)
                        {
                            getServer().broadcast(Component.text("§eProjectile Notice §7- §cPermission or Toggle to BreakBlocks is not granted/enabled."));
                        }
                    }
                }
                //No permission? Okay, lets check if BlockBreak is toggled.
                else
                {
                    if (!main.Global.configTogglePlayerBreakBlocks)
                    {
                        pEventInfo.goodToBreakBlocks = false;
                        if (main.Global.configToggleVerbose)
                        {
                            getServer().broadcast(Component.text("§eProjectile Notice §7- §cPlayerBreakBlocks is toggled off."));
                        }
                    }
                    else
                    {pEventInfo.goodToBreakBlocks = true;}
                }

                //------------------------------------
                // A block was hit.
                //------------------------------------
                if (e.getHitBlock() != null)
                {
                    Location locationBlock = e.getHitBlock().getLocation();
                    World worldBlock = locationBlock.getWorld();
                    pEventInfo.pLocation = locationBlock;
                    pEventInfo.pTarget = worldBlock;
                    pEventInfo.pEntity = e.getEntity();
                    //pEventInfo.pItem = Objects.requireNonNull(pEventInfo.playerWhoShot.getPlayer()).getInventory().getItemInMainHand().add();

                    projectileListCheck();
                }
                //------------------------------------
                // An entity was hit.
                //------------------------------------
                else if (e.getHitEntity() != null)
                {
                    if(e.getHitEntity() instanceof Player)
                    {
                        //Calling this will cancel the explosion to prevent self-exploding.
                        cleanUpProcess();
                    }
                    else
                    {
                        Location locationEntity = e.getHitEntity().getLocation();
                        World worldEntity = e.getHitEntity().getWorld();
                        pEventInfo.pLocation = locationEntity;
                        pEventInfo.pTarget = worldEntity;
                        pEventInfo.pEntity = e.getEntity();
                        //pEventInfo.pItem = Objects.requireNonNull(pEventInfo.playerWhoShot.getPlayer()).getInventory().getItemInMainHand().add();

                        projectileListCheck();
                    }
                }
            }
        }
    }

    private void projectileListCheck()
    {
        String pWorld = pEventInfo.pLocation.getWorld().getName();
        if (main.Global.protectedWorldList.contains(pWorld))
        {
            if (main.Global.configToggleVerbose)
            {pEventInfo.playerWhoShot.sendMessage("§eExplosive projectiles are disabled in this world.");}
            cleanUpProcess();
            return;
        }

        //Projectile Check - Arrow
        if (pEventInfo.pEntity instanceof Arrow)
        {
            if (main.Global.configToggleArrow)
            {
                if (main.Global.configToggleImpactCustom)
                {pEventInfo.pDamageType = main.Global.configImpactArrow;}

                if (pEventInfo.goodToBreakBlocks)
                {
                    pEventInfo.pEntity.remove();
                    whichImpactType();
                }
            }
        }
        //Projectile Check - Thrown Potion
        else if (pEventInfo.pEntity instanceof ThrownPotion)
        {
            if (main.Global.configTogglePotion)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactPotion;}
                whichImpactType();

                //Fix to allow consecutive explosions
                launchCheck.launchGlobal.playerShotStarted = true;
            }
        }
        //Projectile Check - Snowball
        else if (pEventInfo.pEntity instanceof Snowball)
        {
            if (main.Global.configToggleSnowball)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactSnowball;}
                whichImpactType();

                //Fix to allow consecutive explosions
                launchCheck.launchGlobal.playerShotStarted = true;
            }
        }
        //Projectile Check - Egg
        else if (pEventInfo.pEntity instanceof Egg)
        {
            if (main.Global.configToggleEgg)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactEgg;}
                whichImpactType();

                //Fix to allow consecutive explosions
                launchCheck.launchGlobal.playerShotStarted = true;
            }
        }
        //Projectile Check - ExpBottle
        else if (pEventInfo.pEntity instanceof ThrownExpBottle)
        {
            if (main.Global.configToggleExpBottle)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactExpBottle;}
                whichImpactType();

                //Fix to allow consecutive explosions
                launchCheck.launchGlobal.playerShotStarted = true;
            }
        }
        //Projectile Check - Fishing Rod
        else if (pEventInfo.pEntity instanceof FishHook)
        {
            if (main.Global.configToggleFishingBobber)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactFishingBobber;}
                whichImpactType();
            }
        }
        //Projectile Check - Enderpearl
        else if (pEventInfo.pEntity instanceof EnderPearl)
        {
            if (main.Global.configToggleEnderpearl)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactEnderpearl;}
                whichImpactType();
            }
        }
        //Projectile Check - Trident
        else if (pEventInfo.pEntity instanceof Trident)
        {
            if (main.Global.configToggleTrident)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactTrident;}
                whichImpactType();

                // 1.0.9 Fix
                // Tridents can now be deleted upon landing and will get added back to the players inventory.
                GameMode gm = pEventInfo.playerWhoShot.getGameMode();
                if (gm == GameMode.SURVIVAL)
                {
                    pEventInfo.pItem = ((Trident) pEventInfo.pEntity).getItem();
                    ItemMeta meta = ((Trident) pEventInfo.pEntity).getItem().getItemMeta();

                    Inventory inv = pEventInfo.playerWhoShot.getInventory();
                    pEventInfo.pItem.setItemMeta(meta);
                    inv.addItem(pEventInfo.pItem);
                    pEventInfo.pEntity.remove();
                }
            }
        }
        /*
        //Projectile Check - WindCharge
        else if (pEventInfo.pEntity instanceof WindCharge)
        {
            if (main.Global.configToggleWindCharge)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    pEventInfo.pDamageType = main.Global.configImpactWindCharge;}
                whichImpactType();
            }
        }
        */
    }

    // Checks what type of impact we should choose.
    private void whichImpactType()
    {
        if (launchCheck.launchGlobal.playerShotStarted)
        {
            if (!main.Global.configToggleImpactCustom)
            {
                //Explosive Value will be the Config's Global Value.
                pEventInfo.pImpactValue = main.Global.configImpactGlobal;
                pEventInfo.pString = "§aPlayer Projectile Success §7- §eGlobal Impact: §6";
            }
            else
            {
                //Explosive Value will be whichever projectile is chosen since Custom Impact is enabled.
                pEventInfo.pImpactValue = pEventInfo.pDamageType;
                pEventInfo.pString = "§aPlayer Projectile Success §7- §eCustom Impact: §6";
            }
            //----------------------------------------------------------------------------
            if (main.Global.configToggleVerbose)
            {
                getServer().broadcast(Component.text(pEventInfo.pString + pEventInfo.pImpactValue));}
            //----------------------------------------------------------------------------
            main.Global.kaboom = true;

            pEventInfo.pTarget.createExplosion(pEventInfo.pLocation, pEventInfo.pImpactValue, main.Global.configToggleFire, pEventInfo.goodToBreakBlocks);
            cleanUpProcess();
        }
    }


    private static void cleanUpProcess()
    {
        main.Global.kaboom = false;
        entityHurtCheck.hurtGlobal.mobCount = 1;
        entityHurtCheck.hurtGlobal.playerCount = 1;
        launchCheck.launchGlobal.playerShotStarted = false;
    }

}