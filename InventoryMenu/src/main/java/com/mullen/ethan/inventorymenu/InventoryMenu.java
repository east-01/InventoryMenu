package com.mullen.ethan.inventorymenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.mullen.ethan.inventorymenu.events.InventoryMenuClickEvent;

/**
 * The InventoryMenu will be a wrapper for a InventoryView, and is expected to persist
 *   until the InventoryView is closed.
 *
 * Each in the view will have SlotData. The SlotData can be configured to allow InventoryMenuActions.
 * 
 * @author Ethan Mullen
 */
public class InventoryMenu implements Listener {

	private InventoryView view;	
	private SlotData[] elements;
	
	private ItemStack backgroundItemStack;
	private boolean canClose;

	/** InventoryMenu constructor, parameter Player is going to be who we're opening
	 *    the inventory for. */
	public InventoryMenu(Player p, int size) {
		this.view = p.openInventory(Bukkit.createInventory(null, size));
		this.elements = new SlotData[view.getTopInventory().getSize() + view.getBottomInventory().getSize()];
		
		// Create empty elements
		for(int i = 0; i < elements.length; i++) {
			elements[i] = new SlotData(i);
		}
				
		this.backgroundItemStack = ItemUtils.createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
		this.canClose = true;
		
		Bukkit.getPluginManager().registerEvents(this, InventoryMenuPlugin.getInstance());
	}
	
	@EventHandler
	public void inventoryEvent(InventoryClickEvent event) {
		if(event.getSlot() == -999 || event.getSlot() == -1) return;
		if(view != null && event.getView() != view) return;
		InventoryMenuAction menuAction = InventoryMenuAction.convert(event.getAction());

		// If we can't convert the event's action into a menu action, we don't care.
		if(menuAction == null) return;
				
		SlotData data = elements[event.getRawSlot()];
		
		if(!data.isAllowed(menuAction)) {
			event.setCancelled(true);
		}
		
		InventoryMenuClickEvent menuEvent = new InventoryMenuClickEvent(this, event, menuAction);
		menuEvent.setCancelled(event.isCancelled());
		Bukkit.getPluginManager().callEvent(menuEvent);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if(!event.getView().equals(view)) return;
		
		if(!canClose) {
			new BukkitRunnable() {
				public void run() {
					event.getPlayer().openInventory(view);
				}
			}.runTaskLater(InventoryMenuPlugin.getInstance(), 1l);
		} else {
			closed();
		}
		
	}
	
	public void closed() {
		HandlerList.unregisterAll(this);
	}
			
	/** Fills the bottom row of the inventory */
	public void fillTaskbar() {
		Inventory inv = view.getTopInventory();
		for(int i = inv.getSize()-9; i < inv.getSize(); i++) {
			inv.setItem(i, getBackgroundItemStack());
		}
	}
		
	/** Configures the slots for the bottom inventory to allow everything except
	 *    for shift clicks. */
	public void allowBottomInventoryUse(boolean allowShiftClick) {
		for(int rawSlot = 0; rawSlot < view.getBottomInventory().getSize(); rawSlot++) {
			int slot = view.getTopInventory().getSize() + rawSlot;
			if(allowShiftClick) {
				elements[slot].allow(InventoryMenuAction.ALL_ACTIONS);
			} else {
				elements[slot].allow(
					InventoryMenuAction.PICKUP, 
					InventoryMenuAction.DROP, 
					InventoryMenuAction.PLACE, 
					InventoryMenuAction.COLLECT_TO_CURSOR, 
					InventoryMenuAction.CLONE_STACK, 
					InventoryMenuAction.HOTBAR_MOVE_AND_READ, 
					InventoryMenuAction.HOTBAR_SWAP,
					InventoryMenuAction.SWAP_WITH_CURSOR
				);				
			}
		}
		
	}
	
	public void setTitle(String title) {
		if(view != null) view.setTitle(title);
	}

	public InventoryView getView() { return view; }
	public SlotData getData(int slot) { return elements[slot]; }
	
	public ItemStack getBackgroundItemStack() { return backgroundItemStack; }
	public void setBackgroundItemStack(ItemStack newBackgroundItemStack) { this.backgroundItemStack = newBackgroundItemStack; }
	public boolean canClose() { return canClose; }
	public void setCanClose(boolean canClose) { this.canClose = canClose; }

	public class SlotData {
		private List<InventoryMenuAction> allowed;
		public SlotData(int slot) {
			this.allowed = new ArrayList<>();
		}	
		public boolean isAllowed(InventoryMenuAction action) {
			if(allowed.isEmpty()) return false;
			// Check if an action is allowed, if not, generalize then try again.
			while(action != null) {
				if(allowed.contains(action)) return true;
				action = action.generalize();
			}
			return false;
		}
		public void allow(InventoryMenuAction action) {
			if(isAllowed(action)) return;
			allowed.add(action);
		}
		public void allow(InventoryMenuAction... actions) {
			for(InventoryMenuAction action : actions) {
				allow(action);
			}
		}
		public void clear() {
			allowed.clear();
		}
	}
	
}