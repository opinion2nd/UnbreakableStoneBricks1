package me.sajid.unbreakable;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.List;

public class UnbreakableStoneBricks extends JavaPlugin implements Listener, CommandExecutor {

    private NamespacedKey key;

    @Override
    public void onEnable() {
        key = new NamespacedKey(this, "unbreakable_stone");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("ubs").setExecutor(this);
    }

    public ItemStack getBlock(int amount) {
        ItemStack item = new ItemStack(Material.STONE_BRICKS, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Unbreakable Stone Bricks");
        meta.setLore(List.of(
                "Admin item",
                "Cannot be broken by players",
                "TNT can break this block",
                "No item drop on TNT"
        ));
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.isOp()) return true;

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            Player target = Bukkit.getPlayer(args[1]);
            int amount = Integer.parseInt(args[2]);
            if (target != null) target.getInventory().addItem(getBlock(amount));
        }
        return true;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemMeta meta = e.getItemInHand().getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            e.getBlock().getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            if (!e.getPlayer().isOp()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        Iterator<Block> it = e.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                block.setType(Material.AIR);
                it.remove();
            }
        }
    }
}
