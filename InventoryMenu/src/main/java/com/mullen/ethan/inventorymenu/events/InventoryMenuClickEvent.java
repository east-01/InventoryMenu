package com.mullen.ethan.inventorymenu.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mullen.ethan.inventorymenu.InventoryMenu;
import com.mullen.ethan.inventorymenu.InventoryMenuAction;

public class InventoryMenuClickEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	@SuppressWarnings("unused")
	private boolean cancelled;
	
	private InventoryMenu menu;
	private InventoryClickEvent event;
	private InventoryMenuAction action;
	private boolean clickedTop;
	private boolean clickedBottom;
	
	public InventoryMenuClickEvent(InventoryMenu menu, InventoryClickEvent event, InventoryMenuAction action) {
		
		this.menu = menu;
		this.event = event;
		this.action = action;
		
		if(event.getClickedInventory() == null) return;
		this.clickedTop = event.getClickedInventory() == menu.getView().getTopInventory();
		this.clickedBottom = event.getClickedInventory() == menu.getView().getBottomInventory();
		
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() { return false; }
	public void setCancelled(boolean cancel) {
		event.setCancelled(cancel);
		this.cancelled = cancel;
	}

	public InventoryMenu getMenu() { return menu; }
	public InventoryClickEvent getOriginalEvent() { return event; }
	public InventoryMenuAction getAction() { return action; }
	public boolean clickedTopInventory() { return clickedTop; }
	public boolean clickedBottomInventory() { return clickedBottom; }
	
	/** Wrapper for InventoryClickEvent#getSlot() */
	public int getSlot() { return event.getSlot(); }
	/** Wrapper for InventoryClickEvent#getRawSlot() */
	public int getRawSlot() { return event.getRawSlot(); }
	
}
