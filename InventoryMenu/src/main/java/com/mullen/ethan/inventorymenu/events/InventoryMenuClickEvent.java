package com.mullen.ethan.inventorymenu.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryMenuClickEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	@SuppressWarnings("unused")
	private boolean cancelled;
	
	private InventoryClickEvent event;
	
	
	
	public InventoryMenuClickEvent(InventoryClickEvent event) {
		this.event = event;
		
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return false;
	}

	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
		this.cancelled = cancel;
	}

}
