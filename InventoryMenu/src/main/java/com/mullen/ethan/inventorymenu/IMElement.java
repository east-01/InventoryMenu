package com.mullen.ethan.inventorymenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

/**
 * An IMElement acts as a slot in the InventoryMenu, an IMElement
 *   class should only be responsible for ONE slot.
 */
public class IMElement {
	
	private int slot;
	private List<InventoryAction> allowed;
	private HashMap<InventoryAction, Material> allowedByMaterial; // Allows you to accept specific materials in slots
	private HashMap<InventoryAction, ItemStack> allowedByItemStack; // Allows you to accept specific ItemStacks in slots
	
	public IMElement(int slot) {
		this.slot = slot;
		
		this.allowed = new ArrayList<>();
		this.allowedByMaterial = new HashMap<>();
		this.allowedByItemStack = new HashMap<>();
		
	}
	
	public int getSlot() {
		return slot;
	}
	
	public IMElement allow(InventoryAction action) {
		if(!allowed.contains(action)) allowed.add(action);
		return this;
	}
	
	public boolean isAllowed(InventoryAction action) {
		return allowed.contains(action);
	}
	
	public IMElement allowByMaterial(InventoryAction action, Material material) {
		allowedByMaterial.put(action, material);
		return this;
	}
	
	public IMElement allowByItemStack(InventoryAction action, ItemStack itemStack) {
		allowedByItemStack.put(action, itemStack);
		return this;
	}

	public void allowAll() {
		Arrays.asList(InventoryAction.values()).forEach(action -> allow(action));
	}
	
	public void allowPickups() {
		Arrays.asList(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME)
		.forEach(action -> allow(action));
	}
	
	public void clearAllow() {
		allowed.clear();
	}
	
}
