package com.mullen.ethan.inventorymenu;

import org.bukkit.plugin.java.JavaPlugin;

public class InventoryMenuPlugin extends JavaPlugin {

	private static InventoryMenuPlugin instance;
	public static InventoryMenuPlugin getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		
		if(instance != null) throw new IllegalStateException("Tried to call onEnable() with another instance existing, this shouldn't happen I think");
		instance = this;
				
	}
	
	@Override
	public void onDisable() {

	}
	
}