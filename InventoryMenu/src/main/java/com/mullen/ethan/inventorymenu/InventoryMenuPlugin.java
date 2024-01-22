package com.mullen.ethan.inventorymenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class InventoryMenuPlugin extends JavaPlugin implements Listener {

	private static InventoryMenuPlugin instance;
	public static InventoryMenuPlugin getInstance() {
		return instance;
	}

	private boolean mismatchWarnings;
	
	@Override
	public void onEnable() {
		
		if(instance != null) throw new IllegalStateException("Tried to call onEnable() with another instance existing, this shouldn't happen I think");
		instance = this;
				
		if(getConfig() == null || !getConfig().contains("mismatchwarnings")) {
			saveConfig();
			getConfig().set("mismatchwarnings", false);
			saveConfig();
		}
		
		mismatchWarnings = getConfig().getBoolean("mismatchwarnings", false);
		Bukkit.getPluginManager().registerEvents(this, this);
		
	}
	
	@Override
	public void onDisable() {
		getConfig().set("mismatchwarnings", mismatchWarnings);
		saveConfig();
	}
	
	/** This method checks if the hypotheticalinventoryview made the right predictions. */
	@EventHandler
	public void hypotheticalTester(InventoryClickEvent event) {
		if(!mismatchWarnings) return;
		
		HypotheticalInventoryView hv = new HypotheticalInventoryView(event);

		new BukkitRunnable() { public void run() {
		
			InventoryView view = event.getWhoClicked().getOpenInventory();
			
			List<String> mismatches = new ArrayList<>();
			ItemStack[] realUpperContents = view.getTopInventory().getContents();
			ItemStack[] realLowerContents = view.getBottomInventory().getContents();
			
			ItemStack currHypo;
			ItemStack currReal;
			
			// Check all inv slots
			for(int i = -1; i < hv.getUpperContents().length + hv.getLowerContents().length; i++) {				
				boolean inUpper = i < hv.getUpperContents().length;
				int slotNum = inUpper ? i : i-hv.getUpperContents().length;
				
				if(i > -1) {
					currHypo = inUpper ? hv.getUpperContents()[slotNum] : hv.getLowerContents()[slotNum];
					currReal = inUpper ? realUpperContents[slotNum] : realLowerContents[slotNum];	
				} else {
					currHypo = hv.getCursor();
					currReal = view.getCursor();
				}

				// Convert air items to null
				if(currHypo != null && currHypo.getType() == Material.AIR) currHypo = null;
				if(currReal != null && currReal.getType() == Material.AIR) currReal = null;
				
				// If they're both null, its a match and we can continue.
				if(currHypo == null && currReal == null) continue;
				
				String mismatchStr = "Mismatch on" + (i == -1 ? "cursor" : "slot " + slotNum) + " [upper=" + inUpper + ", slotNum/rawSlot=" + slotNum + "/" + i + "]";
				
				if(currHypo == null ^ currReal == null) {
					mismatches.add(mismatchStr);
					mismatches.add("  - Null xor failed, predicted: " + currHypo + "; reality: " + currReal);
					continue;
				}
				
				if(currHypo.equals(currReal)) continue;
				
				mismatches.add(mismatchStr);
				if(currHypo.getType() != currReal.getType())
					mismatches.add("  - Type mismatch, predicted: " + currHypo.getType() + "; reality: " + currReal.getType());
				if(currHypo.getAmount() != currReal.getAmount())
					mismatches.add("  - Amount mismatch, predicted: " + currHypo.getAmount() + "; reality: " + currReal.getAmount());
				
			}
		
			if(mismatches.size() > 0) {
				getLogger().warning("HypotheticalInventoryView made incorrect predictions for action " + event.getAction());
				mismatches.forEach(s -> getLogger().warning("  " + s));
				getLogger().warning(" ");
			} else {
//				getLogger().info("HypotheticalInventoryView made correct predictions for action " + event.getAction());
			}
			
		}}.runTaskLater(this, 1L);
	}
	
	public static void PrintItemArray(ItemStack[] arr) {
		String s = "";
		for(int i = 0; i < arr.length; i++) {
			s += arr[i] + ", ";
		}
		InventoryMenuPlugin.getInstance().getLogger().info("Item array length " + arr.length + ":");
		InventoryMenuPlugin.getInstance().getLogger().info(s.substring(0, s.length()-2));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mismatchwarnings")) {
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to toggle warnings.");
				return true;
			}
			mismatchWarnings = !mismatchWarnings;
			sender.sendMessage(ChatColor.GOLD + "Set mismatch warnings to " + mismatchWarnings);
		}
		return false;
	}
	
}