package com.example.bessmertnyy_3_2_assignment;


public class InventoryItem {
    private int id;
    private String name;
    private String quantity;
    private String location;


    public InventoryItem(String name, String quantity, String location) {
        this(-1, name, quantity, location);
    }

    public InventoryItem(int id, String name, String quantity, String location) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getLocation() { return location; }
}
