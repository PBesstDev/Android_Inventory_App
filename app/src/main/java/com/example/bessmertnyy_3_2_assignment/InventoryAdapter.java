package com.example.bessmertnyy_3_2_assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



// Managing and displaying inventory items in a RecyclerView.
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> itemList;
    private final OnItemDeleteListener deleteListener;


    //Interface to handle delete button clicks.
    public interface OnItemDeleteListener {
        void onDelete(InventoryItem item);
    }

    public InventoryAdapter(List<InventoryItem> itemList, OnItemDeleteListener deleteListener) {
        this.itemList = itemList;
        this.deleteListener = deleteListener;
    }

    @Override
    //Inflates row layout (item_dataview.xml) and creates ViewHolder.
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dataview, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    //Binds specific data from inventory list to UI elements in row.
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        InventoryItem currentItem = itemList.get(position);
        
        // Populate text fields
        holder.tvName.setText(currentItem.getName());
        holder.tvQty.setText(currentItem.getQuantity());
        holder.tvLocation.setText(currentItem.getLocation());
        
        // Setup listener for the delete button in this row
        holder.btnDeleteRow.setOnClickListener(v -> deleteListener.onDelete(currentItem));
    }

    @Override
    //Returns total num of items to be displayed.
    public int getItemCount() {
        return itemList.size();
    }

    //Grouping all UI components for each row
    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvLocation;
        View btnDeleteRow;

        public InventoryViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnDeleteRow = itemView.findViewById(R.id.btnDeleteRow);
        }
    }
}
