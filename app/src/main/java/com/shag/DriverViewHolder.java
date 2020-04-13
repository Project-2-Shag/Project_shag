package com.shag;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class DriverViewHolder extends RecyclerView.ViewHolder {
    TextView name, id, way;
    public DriverViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.viewName);
        id = itemView.findViewById(R.id.viewId);
        name = itemView.findViewById(R.id.viewWay);
    }
}
