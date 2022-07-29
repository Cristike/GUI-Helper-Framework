package me.cristike.ghf;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Gui {
    /**
     * Equals true if the listeners were registered, false if not.
     */
    private static boolean listenersRegistered;

    /**
     * Contains the inventories in relation to the GUI they're part of.
     */
    @NotNull
    private static final HashMap<Inventory, Gui> INVENTORIES = new HashMap<>();

    /**
     * The id of the GUI. It is unique.
     */
    @NotNull
    private final String ID;

    /**
     * The inventory of the GUI.
     */
    @Nullable
    private Inventory inventory;

    /**
     * The title of the GUI.
     */
    @NotNull
    private String title;

    /**
     * The size of the GUI.
     * A multiple of 9.
     */
    private int size;

    /**
     * The contents of the GUI.
     */
    @NotNull
    private ItemStack[] contents;

    /**
     * The actions assigned for InventoryClickEvent.
     */
    @NotNull
    private final HashMap<Integer, GuiAction> ACTIONS = new HashMap<>();

    /**
     * The action assigned for InventoryCloseEvent.
     */
    @NotNull
    private GuiAction closeAction = new GuiAction() {
        @Override
        public void executeCloseAction(InventoryCloseEvent event) {

        }

        @Override
        public void executeClickAction(InventoryClickEvent event) {

        }
    };

    /**
     * Whether the user can modify the inventory or not.
     */
    private boolean allowChanges;

    /**
     * Constructs a new GUI, with a specific id, title and size.
     */
    public Gui(@NotNull JavaPlugin plugin, @NotNull String id, @NotNull String title, int size) {
        this.ID = id;
        this.title = title;
        this.size = size;
        this.contents = new ItemStack[size];

        if (listenersRegistered) return;
        plugin.getServer().getPluginManager().registerEvents(new GuiListeners(), plugin);

        listenersRegistered = true;
    }

    /**
     * Constructs a new GUI, with a specific id, title, size and contents.
     */
    public Gui(@NotNull JavaPlugin plugin, @NotNull String id, @NotNull String title, int size, @NotNull ItemStack[] contents) {
        this.ID = id;
        this.title = title;
        this.size = size;
        this.contents = contents;

        if (listenersRegistered) return;
        plugin.getServer().getPluginManager().registerEvents(new GuiListeners(), plugin);

        listenersRegistered = true;
    }

    /**
     * Builds the inventory if not built already.
     */
    public void build() {
        if (inventory != null) return;
        if (size % 9 != 0) size(54);

        inventory = Bukkit.createInventory(null, size, title);
        inventory.setContents(contents);

        INVENTORIES.put(inventory, this);
    }

    /**
     * Updates the inventory.
     */
    public void update() {
        if (inventory == null) {
            build();
            return;
        }

        List<HumanEntity> viewers = inventory.getViewers();
        Inventory i = Bukkit.getServer().createInventory(null, size, title);
        i.setContents(contents);

        viewers.forEach(HumanEntity::closeInventory);

        INVENTORIES.remove(inventory);
        inventory = i;
        INVENTORIES.put(inventory, this);

        viewers.forEach(v -> v.openInventory(inventory));
    }

    /**
     * Opens the GUI for the given player
     */
    public void open(@NotNull Player player) {
        if (inventory == null) return;
        player.openInventory(inventory);
    }

    /**
     * Checks if a GUI with the given id exists.
     */
    public static boolean exists(@NotNull String id) {
        return INVENTORIES.keySet().stream().anyMatch(key -> INVENTORIES.get(key).id().equals(id));
    }

    /**
     * Returns the GUI with the given id.
     */
    @Nullable
    public static Gui getGui(@NotNull String id) {
        for (Inventory key : INVENTORIES.keySet())
            if (INVENTORIES.get(key).id().equals(id))
                return INVENTORIES.get(key);

        return null;
    }

    /**
     * Returns the GUI with the given inventory.
     */
    @Nullable
    public static Gui getGui(@NotNull Inventory inventory) {
        return INVENTORIES.getOrDefault(inventory, null);
    }

    /**
     * Returns the inventory of the GUI with the given id.
     */
    @Nullable
    public static Inventory getGuiInventory(@NotNull String id) {
        for (Inventory key : INVENTORIES.keySet())
            if (INVENTORIES.get(key).id().equals(id))
                return key;

        return null;
    }

    /**
     * Removes registered GUI with given id.
     */
    public static void removeGui(@NotNull String id) {
        Inventory i = null;

        for (Inventory key : INVENTORIES.keySet())
            if (INVENTORIES.get(key).id().equals(id)) {
                i = key;
                break;
            }

        if (i != null)
            INVENTORIES.remove(i);
    }

    /**
     * Removes registered GUI given its inventory.
     */
    public static void removeGui(@NotNull Inventory inventory) {
        INVENTORIES.remove(inventory);
    }

    /**
     * Clears the GUI's contents.
     */
    public void clear() {
        contents = new ItemStack[size];
    }

    /**
     * Sets the given slot to the given item.
     */
    public void set(@NotNull ItemStack item, int slot) {
        if (slot < 0 || slot >= size) return;
        contents[slot] = item;
    }

    /**
     * Sets the given slots to the given item.
     */
    public void set(@NotNull ItemStack item, int @NotNull ... slots) {
        for (int slot : slots) {
            if (slot < 0 || slot >= size) continue;
            contents[slot] = item;
        }
    }

    /**
     * Fills the entire GUI with the given item.
     */
    public void fill(@NotNull ItemStack item) {
        for (int slot = 0; slot < size; slot++)
            contents[slot] = item;
    }

    /**
     * Fill the entire GUI with the given item, except the specified slots.
     */
    public void fillExcept(@NotNull ItemStack item, int @NotNull ... exceptions) {
        for (int slot = 0; slot < size; slot++) {
            final int k = slot;
            if (Arrays.stream(exceptions).anyMatch(n -> n == k)) continue;

            contents[slot] = item;
        }
    }

    /**
     * Fills the GUI in a given range with the given item.
     * The range start with 'from' and ends with 'to'.
     */
    public void fillInRange(@NotNull ItemStack item, int from, int to) {
        if (from < 0 || from >= size) return;
        if (to < 0 || to >= size) return;

        for (int slot = from; slot <= to; slot++)
            contents[slot] = item;
    }

    /**
     * Fills the given GUI's line with the given item.
     */
    public void fillLine(@NotNull ItemStack item, int line) {
        if (line < 0 || line >= size) return;
        int from = line * 9, to = (line + 1) * 9 - 1;

        for (int slot = from; slot <= to; slot++)
            contents[slot] = item;
    }

    /**
     * Fills the given GUI's lines with the given item.
     */
    public void fillLines(@NotNull ItemStack item, int @NotNull ... lines) {
        for (int line : lines)
            fillLine(item, line);
    }

    /**
     * Fills the given GUI's line in a given range with the given item.
     * The range starts with 'from' and ends with 'to'.
     * 'from' and 'to' are values from 0-8
     */
    public void fillLineInRange(@NotNull ItemStack item, int line, int from, int to) {
        if (line < 0 || line >= size) return;
        if (from < 0 || from >= 9) return;
        if (to < 0 || to >= 9) return;
        int fFrom = line * 9 + from, fTo = line * 9 + to;

        for (int slot = fFrom; slot <= fTo; slot++)
            contents[slot] = item;
    }

    /**
     * Fills the given GUI's column with the given item.
     */
    public void fillColumn(@NotNull ItemStack item, int column) {
        if (column < 0 || column >= 9) return;
        int to = ((size / 9) - 1) * 9 + column;

        for (int slot = column; slot <= to; slot += 9)
            contents[slot] = item;
    }

    /**
     * Fills the given GUI's columns with the given item.
     */
    public void fillColumns(@NotNull ItemStack item, int @NotNull ... columns) {
        for (int column : columns)
            fillColumn(item, column);
    }

    /**
     * Fills the given GUI's column in a given range with the given item.
     * The range starts with 'from' and ends with 'to'.
     * 'from' and 'to' are values from 0-8.
     */
    public void fillColumnInRange(@NotNull ItemStack item, int column, int from, int to) {
        if (column < 0 || column >= 9) return;
        if (from < 0 || from >= size / 9) return;
        if (to < 0 || to >= size / 9) return;
        int fFrom = from * 9 + column, fTo = to * 9 + column;

        for (int slot = fFrom; slot <= fTo; slot += 9)
            contents[slot] = item;
    }

    /**
     * Register an action for the given slot.
     */
    public void registerAction(@NotNull GuiAction action, int slot) {
        if (slot < 0 || slot >= size) return;
        ACTIONS.put(slot, action);
    }

    /**
     * Register a new action for the given slots.
     */
    public void registerAction(@NotNull GuiAction action, int @NotNull ... slots) {
        for (int slot : slots)
            registerAction(action, slot);
    }

    /**
     * Unregister the action assigned to the given slot.
     */
    public void unregisterAction(int slot) {
        ACTIONS.remove(slot);
    }

    /**
     * Unregister the action assigned to the given slots.
     */
    public void unregisterAction(int @NotNull ... slots) {
        for (int slot : slots)
            unregisterAction(slot);
    }

    /**
     * Checks if the given slot has an assigned action or not.
     */
    public boolean hasAction(int slot) {
        return ACTIONS.containsKey(slot);
    }

    /**
     * Returns the action for the given slot or null.
     */
    public GuiAction getAction(int slot) {
        return ACTIONS.getOrDefault(slot, new GuiAction() {
            @Override
            public void executeCloseAction(InventoryCloseEvent event) {

            }

            @Override
            public void executeClickAction(InventoryClickEvent event) {

            }
        });
    }

    /**
     * Getter for ID.
     */
    @NotNull
    public String id() {
        return ID;
    }

    /**
     * Getter for inventory.
     */
    @Nullable
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Getter for title.
     */
    @NotNull
    public String title() {
        return title;
    }

    /**
     * Setter for title.
     */
    public void title(@NotNull String t) {
        title = t;
    }

    /**
     * Getter for size.
     */
    public int size() {
        return size;
    }

    /**
     * Setter for size.
     */
    public void size(int s) {
        if (s < size)
            contents = Arrays.copyOfRange(contents.clone(), 0, s - 1);
        size = s;
    }

    /*
     * Getter for a clone of contents.
     */
    @NotNull
    public ItemStack[] contents() {
        return contents.clone();
    }

    /**
     * Setter for contents.
     * Returns if the contents were set or not.
     */
    public boolean contents(@NotNull ItemStack[] c) {
        if (c.length != size) return false;
        contents = c;

        return true;
    }

    /**
     * Getter for closeAction.
     */
    public GuiAction closeAction() {
        return closeAction;
    }

    /**
     * Setter for closeAction.
     */
    public void closeAction(GuiAction ga) {
        closeAction = ga;
    }

    /**
     * Getter for allowChanges
     */
    public boolean allowChanges() {
        return allowChanges;
    }

    /**
     * Setter for allowChanges
     */
    public void allowChanges(boolean ac) {
        allowChanges = ac;
    }
}
