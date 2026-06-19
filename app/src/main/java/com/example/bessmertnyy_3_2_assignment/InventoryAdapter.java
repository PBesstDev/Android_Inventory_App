package com.example.bessmertnyy_3_2_assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



//RecyclerView adapter that maps InventoryItem data into row views.
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    //Source list currently shown in RecyclerView.
    private List<InventoryItem> itemList;
    //Callback when edit button is clicked in a row.
    private final OnItemEditListener editListener;
    //Callback when delete button is clicked in a row.
    private final OnItemDeleteListener deleteListener;

    //Simple interface so activity can handle edit clicks.
    public interface OnItemEditListener {
        void onEdit(InventoryItem item);
    }

    //Simple interface so activity can handle delete clicks.
    public interface OnItemDeleteListener {
        void onDelete(InventoryItem item);
    }

    //Constructor gets item list + listeners from parent activity.
    public InventoryAdapter(
            List<InventoryItem> itemList,
            OnItemEditListener editListener,
            OnItemDeleteListener deleteListener
    ) {
        this.itemList = itemList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    //Inflates one row layout and wraps it in a view holder.
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dataview, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    //Binds one inventory item to one row.
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        InventoryItem currentItem = itemList.get(position);

        holder.tvName.setText(currentItem.getName());
        holder.tvQty.setText(currentItem.getQuantity());
        holder.tvLocation.setText(currentItem.getLocation());

        holder.btnEditRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onEdit(currentItem);
            }
        });

        holder.btnDeleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(currentItem);
            }
        });
    }

    @Override
    //Returns total number of rows in the list.
    public int getItemCount() {
        return itemList.size();
    }

    //Holds row view refs so findViewById isnt repeated over and over.
    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvLocation;
        View btnEditRow, btnDeleteRow;

        //Grab row view refs once when holder is created.
        public InventoryViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnEditRow = itemView.findViewById(R.id.btnEditRow);
            btnDeleteRow = itemView.findViewById(R.id.btnDeleteRow);
        }
    }
}

