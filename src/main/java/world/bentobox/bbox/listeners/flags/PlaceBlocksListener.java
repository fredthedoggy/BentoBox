package world.bentobox.bbox.listeners.flags;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import world.bentobox.bbox.api.flags.AbstractFlagListener;
import world.bentobox.bbox.api.user.User;
import world.bentobox.bbox.lists.Flags;

/**
 * @author tastybento
 */
public class PlaceBlocksListener extends AbstractFlagListener {

    /**
     * Check blocks being placed in general
     *
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        if (e.getBlock().getType().equals(Material.FIRE)) {
            return;
        }
        checkIsland(e, e.getBlock().getLocation(), Flags.PLACE_BLOCKS);
    }

    /**
     * Handles placing items into ItemFrames
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerHitEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            checkIsland(e, e.getRightClicked().getLocation(), Flags.PLACE_BLOCKS);
        }
    }

    /**
     * Handle placing of fireworks, mine carts, end crystals, doors, chests and boats on land
     * The doors and chests are related to an exploit.
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        switch (e.getClickedBlock().getType()) {
        case FIREWORK:
            checkIsland(e, e.getClickedBlock().getLocation(), Flags.PLACE_BLOCKS);
            return;
        case RAILS:
        case POWERED_RAIL:
        case DETECTOR_RAIL:
        case ACTIVATOR_RAIL:
            if (e.getMaterial() != null && (e.getMaterial() == Material.MINECART || e.getMaterial() == Material.STORAGE_MINECART || e.getMaterial() == Material.HOPPER_MINECART
            || e.getMaterial() == Material.EXPLOSIVE_MINECART || e.getMaterial() == Material.POWERED_MINECART)) {
                checkIsland(e, e.getClickedBlock().getLocation(), Flags.PLACE_BLOCKS);
            }
            return;
        default:
            // Check in-hand items
            // This check protects against an exploit in 1.7.9 against cactus
            // and sugar cane and placing boats on non-liquids
            if (e.getMaterial() != null
            && (e.getMaterial().equals(Material.END_CRYSTAL) || e.getMaterial().equals(Material.WOOD_DOOR)
                    || e.getMaterial().equals(Material.CHEST) || e.getMaterial().equals(Material.TRAPPED_CHEST)
                    || e.getMaterial().equals(Material.IRON_DOOR) || (e.getMaterial().name().contains("BOAT")
                            && !e.getClickedBlock().isLiquid()))) {
                checkIsland(e, e.getPlayer().getLocation(), Flags.PLACE_BLOCKS);
            }
        }
    }

    /**
     * Handles Frost Walking on visitor's islands. This creates ice blocks, which is like placing blocks
     * @param e - event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBlockForm(EntityBlockFormEvent e) {
        if (e.getNewState().getType().equals(Material.FROSTED_ICE) && e.getEntity() instanceof Player) {
            setUser(User.getInstance((Player)e.getEntity()));
            checkIsland(e, e.getBlock().getLocation(), Flags.PLACE_BLOCKS);
        }
    }

}