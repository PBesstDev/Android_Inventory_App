package com.example.bessmertnyy_3_2_assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> itemList;
    private final OnItemEditListener editListener;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemEditListener {
        void onEdit(InventoryItem item);
    }

    public interface OnItemDeleteListener {
        void onDelete(InventoryItem item);
    }

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
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dataview, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
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
    public int getItemCount() {
        return itemList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvLocation;
        View btnEditRow, btnDeleteRow;

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

