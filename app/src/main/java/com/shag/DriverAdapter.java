package com.shag;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder>
{
    private List<Driver> list;

    public DriverAdapter(List<Driver> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DriverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DriverViewHolder holder, int position) {
        Driver driver = list.get(position);
        holder.name.setText(driver.getSecondName() + " " + driver.getName() + " " + driver.getThirdName());
        holder.id.setText(driver.getId());
        holder.way.setText(driver.getWay());

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 0, 0, "Удалить");
                //menu.add(holder.getAdapterPosition(), 1, 0, "Изменить");
                menu.add(holder.getAdapterPosition(), 2, 0, "Копировать");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DriverViewHolder extends RecyclerView.ViewHolder
    {

        TextView name, id, way;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.viewName);
            id = itemView.findViewById(R.id.viewId);
            way = itemView.findViewById(R.id.viewWay);
        }
    }
}
