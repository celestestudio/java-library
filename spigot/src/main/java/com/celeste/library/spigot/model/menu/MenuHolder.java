package com.celeste.library.spigot.model.menu;

import com.celeste.library.spigot.exception.InvalidPropertyException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Properties;

@Getter
public final class MenuHolder implements InventoryHolder {

    private final Menu menu;
    private final Properties properties;

    @Setter
    private MenuInventory menuInventory;

    /**
     * Menu holder constructor.
     *
     * @param menu Menu
     * @param properties ImmutableMap of the properties.
     */
    public MenuHolder(final Menu menu, final Properties properties) {
        this.menu = menu;
        this.properties = properties;
        this.menuInventory = MenuInventory.builder()
            .items(new ArrayList<>(menu.getSize()))
            .menu(Bukkit.createInventory(this, menu.getSize(), menu.getTitle()))
            .build();
    }

    /**
     * Puts the item on the specific slot.
     *
     * @param item ItemStack
     * @param slot Slot
     *
     * @return MenuItem
     */
    public MenuItem slot(final int slot, final ItemStack item) {
        final MenuItem menuItem = new MenuItem(slot).item(item);

        menuInventory.getItems().set(slot, menuItem);
        return menuItem;
    }

    /**
     * Creates the inventory and sets all items.
     *
     * @param player Player that will open the inventory
     */
    public void show(final Player player) {
        if (player.getOpenInventory() != player.getInventory()) {
            // TODO: Update title and all things from the menu instead of just opening another Menu
        }

        for (MenuItem item : menuInventory.getItems()) {
          if (item == null) continue;
          menuInventory.getMenu().setItem(item.getSlot(), item.getItem());
        }

        menu.onRender(player, this);
        player.openInventory(menuInventory.getMenu());
    }

//    public void updateTitle(final String title, final Player player) {
//        try {
//            PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
//            packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));
//            Method getHandle = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle");
//            Object entityPlayer = getHandle.invoke(player);
//            Field activeContainerField = entityPlayer.getClass().getField("activeContainer");
//            Object activeContainer = activeContainerField.get(entityPlayer);
//            Field windowIdField = activeContainer.getClass().getField("windowId");
//            int id = windowIdField.getInt(activeContainer);
//            packet.getStrings().write(0, "minecraft:chest");
//            packet.getIntegers().write(0, id);
//            packet.getIntegers().write(1, rows * 9);
//            ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
//
//            int i = 0;
//            for (ItemStack item : player.getInventory().getContents()) {
//                player.getInventory().setItem(i, item);
//                i += 1;
//            }
//
//            player.updateInventory();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }

    public void handleClick(final InventoryClickEvent event) {
        event.setCancelled(true);

        final int slot = event.getSlot();
        if (slot < 0) return;

        final MenuItem item = menuInventory.getItems().get(slot);
        if (item == null || item.getAction() == null) return;

        item.getAction().run(this, event);
    }

    public void handleOpen(final InventoryOpenEvent event) {
        menu.onOpen(event, this);
    }

    public void handleClose(final InventoryCloseEvent event) {
        menu.onClose(event, this);
    }

    /**
     * Gets the properties with that Key on the ImmutableMap.
     *
     * @param key Key to get the value
     * @param <T> Property class
     * @return Class of the property
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(final String key) {
        if (properties.get(key) == null) throw new InvalidPropertyException("Get property returned null");
        return (T) properties.get(key);
    }

    /**
     * Sets the properties with that Key on the ImmutableMap.
     *
     * @param key Key for the value
     * @param value Property object
     */
    public void setProperty(final String key, final Object value) {
        properties.put(key, value);
    }

    /**
     * Checks it the property exists.
     *
     * @param key Key to get the value
     * @return boolean If exists
     */
    public boolean hasProperty(final String key) {
        return properties.containsKey(key);
    }

    @Override @NotNull
    public Inventory getInventory() {
      return menuInventory.getMenu();
    }

}
