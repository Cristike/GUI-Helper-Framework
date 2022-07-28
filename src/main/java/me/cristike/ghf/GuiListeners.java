package me.cristike.ghf;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListeners implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        Gui gui = Gui.getGui(e.getView().getTopInventory());

        if (gui == null) return;
        e.setResult(gui.allowChanges() ? Event.Result.ALLOW : Event.Result.DENY);

        if (!gui.hasAction(e.getSlot())) return;
        gui.getAction(e.getSlot()).executeClickAction(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Gui gui = Gui.getGui(e.getInventory());

        if (gui == null) return;
        gui.closeAction().executeCloseAction(e);
    }
}
