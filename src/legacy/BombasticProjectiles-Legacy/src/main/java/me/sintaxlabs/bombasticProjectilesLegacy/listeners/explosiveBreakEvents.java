package me.sintaxlabs.bombasticProjectilesLegacy.listeners;

import me.sintaxlabs.bombasticProjectilesLegacy.main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class explosiveBreakEvents implements Listener
{
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e)
    {
        if (!main.Global.kaboom) return;
        if (main.Global.protectedEntityList.contains(e.getEntity().getType()))
        {
            e.setCancelled(true);
        }

    }

    //Prevents things like Item Frames or Paintings from destruction if they're in the ProtectedEntities List.
    @EventHandler
    public void hangingEntityCheck(HangingBreakEvent e)
    {
        if (!main.Global.kaboom) return;
        if (main.Global.protectedEntityList.contains(e.getEntity().getType()))
        {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void blockBoom(BlockExplodeEvent e)
    {
        if (!main.Global.kaboom) return;
        // Removes protected materials from the explosion so they won't break
        e.blockList().removeIf(b -> main.Global.protectedBlockList.contains(b.getType()));

        //Toggle for if the blocks should drop into items.
        if (!main.Global.configToggleDropItems)
        {
            e.setYield(0.0f);
        }
    }
}
