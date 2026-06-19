package com.example.bessmertnyy_3_2_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

//SQLite helper that stores + reads inventory items.
public class InventoryDatabaseHelper extends SQLiteOpenHelper {

    //DB file name + schema version.
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 2;

    //Starter inventory so app has sample data on first run.
    public static final String EXAMPLE_INVENTORY_NAME = "Example Inventory";

    //Table/column names used in SQL statements.
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INVENTORY_NAME = "inventory_name";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_LOCATION = "location";

    //Basic constructor required by SQLiteOpenHelper.
    public InventoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //Creates inventory table + seeds starter rows.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_INVENTORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " TEXT NOT NULL, "
                + COLUMN_LOCATION + " TEXT NOT NULL)");

        seedExampleInventory(db);
    }

    @Override
    //Simple upgrade strategy: recreate table for this assignment app.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    //Insert one item row into selected inventory.
    public boolean addItem(String inventoryName, String name, String quantity, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, inventoryName);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_LOCATION, location);
        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }

    //Delete one row by its primary key ID.
    public boolean deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_INVENTORY, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    //Update one row using its ID + new values.
    public boolean updateItem(int id, String inventoryName, String name, String quantity, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, inventoryName);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_LOCATION, location);

        int rows = db.update(TABLE_INVENTORY, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    //Return all items for one inventory in newest-first order.
    public List<InventoryItem> getAllItems(String inventoryName) {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_QUANTITY + ", " + COLUMN_LOCATION
                        + " FROM " + TABLE_INVENTORY + " WHERE " + COLUMN_INVENTORY_NAME + "=? ORDER BY " + COLUMN_ID + " DESC",
                new String[]{inventoryName});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String quantity = cursor.getString(2);
                String location = cursor.getString(3);
                items.add(new InventoryItem(id, name, quantity, location));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }

    //Return distinct inventory names for list screen.
    public List<String> getInventoryNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COLUMN_INVENTORY_NAME + " FROM " + TABLE_INVENTORY + " ORDER BY " + COLUMN_INVENTORY_NAME,
                null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return names;
    }

    //Make sure starter inventory still exists after upgrades.
    public void ensureExampleInventoryExists() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_INVENTORY + " WHERE " + COLUMN_INVENTORY_NAME + "=?",
                new String[]{EXAMPLE_INVENTORY_NAME});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count == 0) {
            seedExampleInventory(db);
        }
    }

    //Add several starter rows for example inventory.
    private void seedExampleInventory(SQLiteDatabase db) {
        insertSeedItem(db, EXAMPLE_INVENTORY_NAME, 
            "Doodad", 
            "50", 
            "WHSE 1 12E25"
        );
        insertSeedItem(db, EXAMPLE_INVENTORY_NAME, 
            "Thiggamajig", 
            "30", 
            "WHSE 2 17A"
        );
        insertSeedItem(db, EXAMPLE_INVENTORY_NAME, 
            "Doohickey", 
            "110", 
            "WHSE 1 01Z91"
        );
        insertSeedItem(db, EXAMPLE_INVENTORY_NAME, 
            "Gizmo", 
            "40", 
            "WHSE 3 99X99"
        );
        insertSeedItem(db, EXAMPLE_INVENTORY_NAME, 
            "Thingummy", 
            "99", 
            "WHSE 1 12E22"
        );
    }

    //Helper that inserts one seed row.
    private void insertSeedItem(SQLiteDatabase db, String inventoryName, String name, String quantity, String location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INVENTORY_NAME, inventoryName);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_LOCATION, location);
        db.insert(TABLE_INVENTORY, null, values);
    }
}
