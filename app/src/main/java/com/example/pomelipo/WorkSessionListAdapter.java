package com.example.pomelipo;

import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
@SuppressLint("SetTextI18n")
public class WorkSessionListAdapter extends RecyclerView.Adapter<WorkSessionListAdapter.ViewHolder> {
    private ArrayList<WorkSession> workSessions;
    private OnItemSelectedListener itemSelectedListener;

    public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblSessionNameItem;
        private TextView lblWorkTimeItem;
        private TextView lblChillTimeItem;
        private TextView lblRepetitionsItem;
        private ImageButton btnStart;
        private ImageButton btnStop;

        ViewHolder(View view) {
            super(view);
            lblSessionNameItem = view.findViewById(R.id.lblSessionNameItem);
            lblWorkTimeItem = view.findViewById(R.id.lblWorkTimeItem);
            lblChillTimeItem = view.findViewById(R.id.lblChillTimeItem);
            lblRepetitionsItem = view.findViewById(R.id.lblRepetitionsItem);
            btnStart = view.findViewById(R.id.btnStart);
            btnStop = view.findViewById(R.id.btnStop);

            // Popup Menu
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.menu_contextual, popup.getMenu());

            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    popup.show();
                }
            });

            //ContextualMenu Listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener () {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (itemSelectedListener != null) {
                        itemSelectedListener.onWorkSessionContextualMenu(getAdapterPosition(), item);
                    }
                    return true;
                }
            });


            //Start Listener
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (itemSelectedListener != null){
                        itemSelectedListener.onStartPressed(position, btnStart, btnStop);
                    }
                }
            });

            //Stop Listener
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectedListener != null){
                        itemSelectedListener.onStopPressed(btnStart, btnStop);
                    }
                }
            });
        }
    }

    public WorkSessionListAdapter(ArrayList<WorkSession> workSessions) {
        this.workSessions = workSessions;
    }

    @Override
    public WorkSessionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WorkSessionListAdapter.ViewHolder holder , int position) {
        WorkSession ws = workSessions.get(position);
        holder.lblSessionNameItem.setText(workSessions.get(position).getSessionName());
        holder.lblWorkTimeItem.setText("WT: "+ Integer.toString(workSessions.get(position).getWorkTime()));
        holder.lblChillTimeItem.setText("CT: "+ Integer.toString(workSessions.get(position).getChillTime()));
        holder.lblRepetitionsItem.setText("R: "+ Integer.toString(workSessions.get(position).getRepetitions()));
    }

    @Override
    public int getItemCount() {
        return workSessions.size();
    }

}
