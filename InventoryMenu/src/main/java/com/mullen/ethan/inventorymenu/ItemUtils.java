package com.mullen.ethan.inventorymenu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

	public static ItemStack createItem(Material mat, int amount, String displayName, List<String> lore) {
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta meta = item.getItemMeta();
		if(displayName != null) meta.setDisplayName(displayName);
		if(lore != null) meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material mat, String displayName) {
		return createItem(mat, 1, displayName, null);
	}
	public static ItemStack createItem(Material mat, String displayName, List<String> lore) {
		return createItem(mat, 1, displayName, lore);
	}
}
