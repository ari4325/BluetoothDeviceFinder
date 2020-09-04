package com.bdf.bluetoothdevicefinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    Context context;

    HistoryAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public HistoryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.history_tab_layout, viewGroup , false);
        HistoryAdapter.Holder holder = new HistoryAdapter.Holder(v);
        return holder ;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.Holder holder, final int i) {
                 ListDevice.setContext(context);
                 ListDevice.load();
                 holder.deviceName.setText(ListDevice.getDeviceNameAtPosition(i));
                 holder.dateTime.setText(ListDevice.getDateTimeAtPosition(i));
                 holder.conn.setText(ListDevice.getStrongestConnectionAtPos(i)+" dBm");
                 holder.delete.setOnClickListener(new View.OnClickListener() {
                 @Override
                  public void onClick(View v) {
                     ListDevice.deviceName.remove(i);
                     ListDevice.dateTime.remove(i);
                     ListDevice.names.remove(i);
                     ListDevice.date.remove(i);
                     ListDevice.strongestConnection.remove(i);
                     ListDevice.save();
                     notifyItemRemoved(i);
                     notifyItemRangeChanged(i, ListDevice.deviceName.size());
                     Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
             }
         });
    }

    @Override
    public int getItemCount() {
        return ListDevice.deviceName.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView deviceName;
        TextView dateTime;
        ImageView delete;
        TextView conn;
        public Holder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            dateTime = itemView.findViewById(R.id.dateTime);
            delete = itemView.findViewById(R.id.delete);
            conn = itemView.findViewById(R.id.conn);
        }
    }
}
