package com.example.bessmertnyy_3_2_assignment;


//Simple model object that represents one inventory row.
public class InventoryItem {
    //Unique row ID from DB.
    private int id;
    //Item name shown in UI.
    private String name;
    //Quantity kept as text for simple form handling.
    private String quantity;
    //Storage location text.
    private String location;


    //Convenience constructor used when id isnt known yet.
    public InventoryItem(String name, String quantity, String location) {
        this(-1, name, quantity, location);
    }

    //Full constructor used when loading existing rows.
    public InventoryItem(int id, String name, String quantity, String location) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.location = location;
    }

    //Getter methods used by adapter + activities.
    public int getId() { return id; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getLocation() { return location; }
}
