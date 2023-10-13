package com.mullen.ethan.inventorymenu;

import org.bukkit.event.inventory.InventoryAction;

public enum InventoryMenuAction {
	
	// T1 actions
	ALL_ACTIONS,
	NO_ACTIONS,
	
	// T2 actions
	DROP,
	PICKUP,
	PLACE,
	
	// T3 actions
	CLONE_STACK, 
	COLLECT_TO_CURSOR, 
	DROP_ALL_CURSOR, DROP_ALL_SLOT, DROP_ONE_CURSOR, DROP_ONE_SLOT,
	HOTBAR_MOVE_AND_READ, HOTBAR_SWAP, 
	MOVE_TO_OTHER_INVENTORY, 
	PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME, 
	PLACE_ALL, PLACE_ONE, PLACE_SOME, 
	SWAP_WITH_CURSOR;
	
	public InventoryMenuAction generalize() {
		switch(this) {					
		case DROP_ALL_CURSOR:
		case DROP_ALL_SLOT:
		case DROP_ONE_CURSOR:
		case DROP_ONE_SLOT:
			return DROP;
		
		case PICKUP_ALL:
		case PICKUP_HALF:
		case PICKUP_ONE:
		case PICKUP_SOME:
			return PICKUP;

		case PLACE_ALL:
		case PLACE_ONE:
		case PLACE_SOME:
			return PLACE;

		default:
			return null;
		}
	}
	
	public static InventoryMenuAction convert(InventoryAction action) {
		try {
			return InventoryMenuAction.valueOf(action.toString());
		} catch(Exception e) {
			return null;
		}
	}
	
}
