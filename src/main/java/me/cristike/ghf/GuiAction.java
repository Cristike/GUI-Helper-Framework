package me.cristike.ghf;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class GuiAction {

    public abstract void executeCloseAction(InventoryCloseEvent event);

    public abstract void executeClickAction(InventoryClickEvent event);
}
