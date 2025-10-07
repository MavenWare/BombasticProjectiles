package me.sintaxlabs.bombasticProjectilesLegacy.listeners;

import me.sintaxlabs.bombasticProjectilesLegacy.main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import static org.bukkit.Bukkit.getServer;

//Projectile Check Order
//----------------------
//Arrow
//Potion
//Snowball
//Trident
//----------------------

public final class mobProjectileHitEvent implements Listener
{

    public static class mEventInfo
    {
        public static Location mLocation;
        public static World mTarget;
        public static Entity mEntity;
        public static int mDamageType;
        public static int mImpactValue;
        public static String mString;
    }



    @EventHandler
    public void mobProjectileHitCheck(ProjectileHitEvent e)
    {
        if (e.getEntity().getShooter() instanceof Player)
        {
            return;
        }

        // The shooter is a mob, lets begin.
        if (e.getEntity().getShooter() instanceof Creature)
        {
            //------------------------------------
            // A block was hit.
            //------------------------------------
            if (e.getHitBlock() != null)
            {
                if (main.Global.configToggleMobsExplodeBlocks)
                {
                    Location locationBlock = e.getHitBlock().getLocation();
                    World worldBlock = locationBlock.getWorld();
                    mEventInfo.mLocation = locationBlock;
                    mEventInfo.mTarget = worldBlock;
                    mEventInfo.mEntity = e.getEntity();

                    projectileListCheck();
                }
            }
            //------------------------------------
            // An entity was hit.
            //------------------------------------
            else if (e.getHitEntity() != null)
            {
                if (main.Global.configToggleMobsExplodeEntities)
                {
                    Location locationEntity = e.getHitEntity().getLocation();
                    World worldEntity = e.getHitEntity().getWorld();
                    mEventInfo.mLocation = locationEntity;
                    mEventInfo.mTarget = worldEntity;
                    mEventInfo.mEntity = e.getEntity();

                    projectileListCheck();
                }
            }
        }
    }
    private void projectileListCheck()
    {
        //Denies explosion if world is protected.
        String mWorld = mobProjectileHitEvent.mEventInfo.mLocation.getWorld().getName();
        if (main.Global.protectedWorldList.contains(mWorld))
        {
            cleanUpProcess();
            return;
        }
        //Projectile Check - Arrow
        if (mEventInfo.mEntity instanceof Arrow)
        {
            if (main.Global.configToggleArrow)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    mEventInfo.mDamageType = main.Global.configImpactArrow;}
                whichImpactType();
            }
        }
        //Projectile Check - Thrown Potion
        else if (mEventInfo.mEntity instanceof ThrownPotion)
        {
            if (main.Global.configTogglePotion)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    mEventInfo.mDamageType = main.Global.configImpactPotion;}
                whichImpactType();
            }
        }
        //Projectile Check - Snowball
        else if (mEventInfo.mEntity instanceof Snowball)
        {
            if (main.Global.configToggleSnowball)
            {
                if (main.Global.configToggleImpactCustom)
                {
                    mEventInfo.mDamageType = main.Global.configImpactSnowball;}
                whichImpactType();
            }
        }
    }

    // Checks what type of impact we should choose if either Mob-Explode Check is true.
    private void whichImpactType()
    {
        if (launchCheck.launchGlobal.mobShotStarted)
        {
            if (!main.Global.configToggleMobBreakBlocks)
            {
                if (main.Global.configToggleVerbose)
                {
                    getServer().broadcastMessage("§eProjectile Notice §7- §cMobBreakBlocks is toggled off.");
                }
                return;
            }
            if (main.Global.configToggleMobImpact)
            {
                //Explosive Value will be the Config's Mob Value.
                mEventInfo.mImpactValue = main.Global.configImpactMob;
                mEventInfo.mString = "§aMob Projectile Success §7- §eMob Impact: §6";
            }
            else if (!main.Global.configToggleImpactCustom)
            {
                //Explosive Value will be the Config's Global Value.
                mEventInfo.mImpactValue = main.Global.configImpactGlobal;
                mEventInfo.mString = "§aMob Projectile Success §7- §eGlobal Impact: §6";
            }
            else
            {
                //Explosive Value will be whichever projectile is chosen since Custom Impact is enabled.
                mEventInfo.mImpactValue = mEventInfo.mDamageType;
                mEventInfo.mString = "§aMob Projectile Success §7- §eCustom Impact: §6";
            }
            //----------------------------------------------------------------------------
            if (main.Global.configToggleVerbose)
            {
                getServer().broadcastMessage(mEventInfo.mString + mEventInfo.mImpactValue);}
            //----------------------------------------------------------------------------
            main.Global.kaboom = true;
            mEventInfo.mTarget.createExplosion(mEventInfo.mLocation, mEventInfo.mImpactValue, main.Global.configToggleFire, main.Global.configToggleMobBreakBlocks);
            cleanUpProcess();
        }
    }

    private static void cleanUpProcess()
    {
        main.Global.kaboom = false;
        entityHurtCheck.hurtGlobal.mobCount = 1;
        entityHurtCheck.hurtGlobal.playerCount = 1;
        launchCheck.launchGlobal.mobShotStarted = false;
        if (main.Global.configToggleMobBreakBlocks)
        {mEventInfo.mEntity.remove();}
    }
}