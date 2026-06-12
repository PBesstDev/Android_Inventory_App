package com.example.bessmertnyy_3_2_assignment;


//Model class for a single item in an inventory.
public class InventoryItem {
    private int id;             // Unique ID from the database
    private String name;        // Name of the item
    private String quantity;    // Current quantity (stored as String for flexibility)
    private String location;    // Physical location of the item


    //Constructor for creating a new item (ID is set to -1 by default).
    public InventoryItem(String name, String quantity, String location) {
        this(-1, name, quantity, location);
    }

    //Full constructor for items retrieved from the database.
    public InventoryItem(int id, String name, String quantity, String location) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
    }

    // Standard getters for item properties
    public int getId() { return id; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getLocation() { return location; }
}