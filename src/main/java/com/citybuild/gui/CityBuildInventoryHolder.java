package com.citybuild.gui;

import org.bukkit.inventory.InventoryHolder;

/**
 * CityBuildInventoryHolder - Custom inventory holder to identify CityBuild GUIs
 */
public class CityBuildInventoryHolder implements InventoryHolder {
    private final String guiType;
    
    public CityBuildInventoryHolder(String guiType) {
        this.guiType = guiType;
    }
    
    public String getGuiType() {
        return guiType;
    }
    
    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        return null;
    }
}
