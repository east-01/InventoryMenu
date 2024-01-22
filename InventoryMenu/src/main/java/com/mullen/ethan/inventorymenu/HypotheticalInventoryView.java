package com.mullen.ethan.inventorymenu;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/** This class will take in an InventoryClickEvent and
 *    attempt to predict what the inventory looks like
 *    after the event has run.
 *  This is useful because we don't want to have to delay
 *    our inventory events by one tick to read the result.
 *  Finally, we'll still be able to cancel the event since
 *    we didn't delay to the next tick. Cool.
 *    
 *  @author Ethan Mullen
 */
public class HypotheticalInventoryView {

	private ItemStack[] upperContents;
	private ItemStack[] lowerContents;
	private ItemStack cursor;
	
	public HypotheticalInventoryView(InventoryClickEvent event) {
		
		InventoryView view = event.getWhoClicked().getOpenInventory();
		boolean clickedTop = event.getClickedInventory() == view.getTopInventory();
		
		// Load current contents
		this.upperContents = new ItemStack[view.getTopInventory().getContents().length];
		for(int i = 0; i < upperContents.length; i++) {
			if(view.getTopInventory().getContents()[i] == null) continue;
			upperContents[i] = view.getTopInventory().getContents()[i].clone();
		}
		this.lowerContents = new ItemStack[view.getBottomInventory().getContents().length];
		for(int i = 0; i < lowerContents.length; i++) {
			if(view.getBottomInventory().getContents()[i] == null) continue;
			lowerContents[i] = view.getBottomInventory().getContents()[i].clone();
		}
		this.cursor = view.getCursor().clone();

		// Predict new contents
		ItemStack[] currentContents = clickedTop ? upperContents : lowerContents;
		ItemStack currentItem = currentContents[event.getSlot()];
		
		switch(event.getAction()) {
		// A max-size stack of the clicked item is put on the cursor.
		case CLONE_STACK:
			if(currentItem == null) break;
			cursor = currentItem.clone();
			cursor.setAmount(cursor.getMaxStackSize());
			break;
		// The inventory is searched for the same material, and they are put on the cursor up to Material.getMaxStackSize().
		// Notes: The search always starts from the top inventory. It says it searches for the same material, but the items have to be exactly equal.
		case COLLECT_TO_CURSOR:
			if(cursor == null) 
				throw new IllegalStateException("Cursor shouldn't be null for COLLECT_TO_CURSOR");
			
			int amountOnCursor = cursor.getAmount();
			for(int i = 0; i < upperContents.length + lowerContents.length; i++) {
				if(amountOnCursor >= cursor.getMaxStackSize()) break;
				
				boolean inUpper = i < upperContents.length;
				ItemStack curr = inUpper ? upperContents[i] : lowerContents[i-upperContents.length];
				if(curr == null)
					continue;
				if(!cursor.isSimilar(curr))
					continue;
				
				int amountToAdd = Math.min(curr.getAmount(), cursor.getMaxStackSize()-amountOnCursor);
				amountOnCursor += amountToAdd;
				curr.setAmount(curr.getAmount()-amountToAdd);
			}
			cursor.setAmount(amountOnCursor);
			break;
		// The entire cursor item is dropped.
		case DROP_ALL_CURSOR:
			cursor = null;
			break;
		// One item is dropped from the cursor.
		case DROP_ONE_CURSOR:
			cursor.setAmount(cursor.getAmount()-1);
			break;
		// The entire clicked slot is dropped.
		case DROP_ALL_SLOT:
			currentContents[event.getSlot()] = null;
			break;
		// One item is dropped from the clicked slot.
		case DROP_ONE_SLOT:
			if(currentItem.getAmount() == 1) {
				currentContents[event.getSlot()] = null;
			} else {
				currentContents[event.getSlot()].setAmount(currentItem.getAmount()-1);
			}
			break;
		// The clicked item is moved to the hotbar, and the item currently there is re-added to the player's inventory.
		case HOTBAR_MOVE_AND_READD:
			Bukkit.broadcastMessage("TODO: HOTBAR_MOVE_AND_READD doesn't have implementation.");
			break;
		// The clicked slot and the picked hotbar slot are swapped.
		case HOTBAR_SWAP:
			ItemStack temp = lowerContents[event.getHotbarButton()];
			lowerContents[event.getHotbarButton()] = currentContents[event.getSlot()];
			currentContents[event.getSlot()] = temp;
			break;
		// The item is moved to the opposite inventory if a space is found.
		case MOVE_TO_OTHER_INVENTORY:
			int amountRemaining = currentItem.getAmount();
			for(int i = 0; i < upperContents.length + lowerContents.length; i++) {
				if(amountRemaining <= 0) break;
				
				boolean inUpper = i < upperContents.length;
				
				ItemStack curr = inUpper ? upperContents[i] : lowerContents[i-upperContents.length];
				if(curr == null)
					continue;
				if(!cursor.isSimilar(curr))
					continue;
				
				int amountWeCanAdd = curr.getMaxStackSize()-curr.getAmount();
				curr.setAmount(amountWeCanAdd);
				amountRemaining -= amountWeCanAdd;				
			}
			cursor.setAmount(amountRemaining);
			break;
		// All of the items on the clicked slot are moved to the cursor.
		case PICKUP_ALL:
			cursor = currentContents[event.getSlot()].clone();
			currentContents[event.getSlot()] = null;
			break;
		// Half of the items on the clicked slot are moved to the cursor.
		case PICKUP_HALF:
			int amount = currentItem.getAmount();
			cursor = currentItem.clone();
			cursor.setAmount((int)Math.ceil(amount/2d));
			currentContents[event.getSlot()].setAmount((int)Math.floor(amount/2d));
			break;
		// One of the items on the clicked slot are moved to the cursor.
		case PICKUP_ONE:
			cursor = currentItem.clone();
			cursor.setAmount(1);
			if(currentItem.getAmount() == 1) {
				currentContents[event.getSlot()] = null;
			} else {
				currentContents[event.getSlot()].setAmount(currentItem.getAmount()-1);
			}
			break;
		// Some of the items on the clicked slot are moved to the cursor.
		// Notes: This event describes "if there were 128 cobblestone in a slot, but your cursor can only hold 64"
		case PICKUP_SOME:
			int amountToAdd = cursor.getMaxStackSize()-cursor.getAmount();
			cursor.setAmount(cursor.getMaxStackSize());
			currentContents[event.getSlot()].setAmount(currentItem.getAmount()-amountToAdd);
			break;
		// All of the items on the cursor are moved to the clicked slot.
		case PLACE_ALL:
			if(currentContents[event.getSlot()] != null) {
				currentContents[event.getSlot()].setAmount(currentContents[event.getSlot()].getAmount() + cursor.getAmount());
			} else {
				currentContents[event.getSlot()] = cursor.clone();
			}
			cursor = null;
			break;
		// A single item from the cursor is moved to the clicked slot.
		case PLACE_ONE:
			if(currentContents[event.getSlot()] != null) {
				currentContents[event.getSlot()].setAmount(currentItem.getAmount() + 1);
			} else {
				currentContents[event.getSlot()] = cursor.clone();
				currentContents[event.getSlot()].setAmount(1);
			}
			cursor.setAmount(cursor.getAmount() - 1);
			break;
		// Some of the items from the cursor are moved to the clicked slot (usually up to the max stack size).
		case PLACE_SOME:
			int amountToPlace = currentItem.getMaxStackSize()-currentItem.getAmount();
			currentContents[event.getSlot()].setAmount(currentItem.getMaxStackSize());
			cursor.setAmount(cursor.getAmount()-amountToPlace);
			break;
		// The clicked item and the cursor are exchanged.
		case SWAP_WITH_CURSOR:
			ItemStack temp2 = currentContents[event.getSlot()].clone();
			currentContents[event.getSlot()] = cursor.clone();
			cursor = temp2;
			break;
		default:
			break;
		
		}
		
	}
	
	public ItemStack getItemFromTop(int slot) {
		if(slot < 0 || slot >= upperContents.length) 
			throw new IllegalArgumentException("Slot " + slot + " is out of bounds for the upper inventory.");
		return upperContents[slot];
	}

	public ItemStack getItemFromBottom(int slot) {
		if(slot < 0 || slot >= lowerContents.length) 
			throw new IllegalArgumentException("Slot " + slot + " is out of bounds for the lower inventory.");
		return lowerContents[slot];
	}

	public ItemStack[] getUpperContents() { return upperContents; }
	public ItemStack[] getLowerContents() { return lowerContents; }
	public ItemStack getCursor() { return cursor; }
	
}
