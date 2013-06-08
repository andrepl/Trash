package com.norcode.bukkit.trash;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Trash extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("trash")) {
				if (sender.hasPermission("trash.command")) {
					openTrash((Player)sender);
				}
				return true;
			}
		} else {
			sender.sendMessage("This command must be run by a player.");
			return true;
		}
		return false;
	}
	
	public void openTrash(Player p) {
		Inventory trash = getServer().createInventory(null, 36, "Trash");
		p.openInventory(trash);
	}
	
	@EventHandler
	public void onCreateSign(SignChangeEvent event) {
		String l1 = event.getLine(0).toLowerCase();
		if (l1.equals("[trash]")||l1.equals("[disposal]")||l1.equals("[bin]")) {
			if (event.getPlayer().hasPermission("trash.sign.create")) {
				event.setLine(0, "§1" + event.getLine(0));
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractSign(PlayerInteractEvent event) {
		BlockState block = null;
		if (event.hasBlock()) {
			block = event.getClickedBlock().getState();
		} else 	if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			List<Block> los = event.getPlayer().getLineOfSight(null, 3);
			for (Block b: los) {
				if (b.getType().getId() != Material.AIR.getId()) {
					block = b.getState();
					break;
				}
			}
		}
		if (block == null) return;
		
		if (block.getType().equals(Material.WALL_SIGN)||block.getType().equals(Material.SIGN_POST)) {
			Sign s = ((Sign)block);
			String l1 = s.getLine(0).toLowerCase();
			if (l1.equals("§1[bin]")||l1.equals("§1[disposal]")||l1.equals("§1[trash]")) {
				if (event.getPlayer().hasPermission("trash.sign.use")) {
					openTrash(event.getPlayer());
				}
			}
		}
	}
}
