package com.mullen.ethan.inventorymenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The InventoryMenu will be a wrapper for a InventoryView, and is expected to persist
 *   until the InventoryView is closed.
 * @author Ethan Mullen
 */
public class InventoryMenu implements Listener {

	private Player p;
	private InventoryView view;	
	private IMElement[] elements;
	
	private boolean canClose;
	private boolean allowBottomInventoryUse;
	
	public InventoryMenu(Player p, int size) {
		this.p = p;
		this.view = p.openInventory(Bukkit.createInventory(null, size));
		this.elements = new IMElement[view.getTopInventory().getSize()];
		
		for(int i = 0; i < elements.length; i++) {
			createEmptyElement(i);
		}
		
		this.canClose = true;
		
		Bukkit.getPluginManager().registerEvents(this, InventoryMenuPlugin.getInstance());
	}
	
	@EventHandler
	public void inventoryEvent(InventoryClickEvent event) {
		if(event.getSlot() == -999 || event.getSlot() == -1) return;
		if(view != null && event.getView() != view) return;
		if(allowBottomInventoryUse && 
		   event.getClickedInventory() == view.getBottomInventory() && 
		   event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
		IMElement element = elements[event.getSlot()];
		if(!element.isAllowed(event.getAction())) {
			event.setCancelled(true);
		}
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
	
	public IMElement createEmptyElement(int slot) {
		elements[slot] = new IMElement(slot);
		return elements[slot];
	}
	
	public ItemStack getBackgroundItemStack() {
		ItemStack i = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(" ");
		i.setItemMeta(m);
		return i;
	}
	
	/** Fills the bottom row of the inventory */
	public void fillTaskbar() {
		Inventory inv = view.getTopInventory();
		for(int i = inv.getSize()-9; i < inv.getSize(); i++) {
			inv.setItem(i, getBackgroundItemStack());
		}
	}
		
	public void setTitle(String title) {
		if(view != null) view.setTitle(title);
	}

	public InventoryView getView() { return view; }
	public IMElement getElement(int slot) { return elements[slot]; }
	
	public boolean canClose() { return canClose; }
	public void setCanClose(boolean canClose) { this.canClose = canClose; }
	
	public boolean allowBottomInventoryUse() { return allowBottomInventoryUse; }
	public void setAllowBottomInventoryUse(boolean allow) { this.allowBottomInventoryUse = allow; }
	
}
