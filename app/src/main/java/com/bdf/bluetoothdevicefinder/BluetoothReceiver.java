package com.bdf.bluetoothdevicefinder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BluetoothReceiver extends BroadcastReceiver {
    private static final int REQUEST_ENABLE_BT = 0;
    Button search;
    Button stop;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> names = new ArrayList<>();
    boolean searchCheck = false;
    int count = 0;
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    List<String> local = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String date = DateFormat.getDateTimeInstance().format(new Date());
            Calendar c = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            if(rssi > -60 && !isPresentInLocal(device.getName())) {
                count++;
                if (!ListDevice.isPresent(device.getName())) {
                    ListDevice.addDevice(device.getName());
                    ListDevice.addDataTime(date);
                    ListDevice.setDate(simpleDateFormat.format(new Date(c.getTimeInMillis())));
                    Toast.makeText(context, simpleDateFormat.format(new Date(c.getTimeInMillis())), Toast.LENGTH_SHORT).show();
                    ListDevice.setStrongestConnection(rssi + "");
                    ListDevice.setMac(device.getAddress());
                    ListDevice.setTop(device.getName() + "\n" + date + "\n" + rssi + " dBm" + "\n" + device.getAddress());
                    ListDevice.setSearchDate(simpleDateFormat.format(new Date(c.getTimeInMillis())));
                    ListDevice.save();
                } else {
                    int i = ListDevice.isPresentAtPos(device.getName());
                    ListDevice.setContext(context);
                    ListDevice.load();
                    if (rssi > Integer.parseInt(ListDevice.getStrongestConnectionAtPos(i))) {
                        ListDevice.setStrongestConnectionAtPos(rssi + "", i);
                        ListDevice.setDateTimeAtPost(date, i);
                        Toast.makeText(context, "Swapping", Toast.LENGTH_SHORT).show();
                        ListDevice.setTopAtPos(device.getName() + "\n" + date + "\n" + rssi + " dBm" + "\n" + device.getAddress(), i);
                        ListDevice.setSearchDateAtPos(simpleDateFormat.format(new Date(c.getTimeInMillis())), i);
                        ListDevice.setMacAtPos(device.getAddress(), i);
                        ListDevice.save();
                    }
                }
                ListDevice.setContext(context);
                ListDevice.load();
                ListDevice.setSearchName(device.getName());
                local.add(device.getName());
                ListDevice.addName(device.getName() + "\n" + date + "\n" + rssi + " dBm");
                names.add(device.getName() + "\n" + date + "\n" + rssi + " dBm" + "\n" + device.getAddress());
                ListDevice.save();
                arrayAdapter.notifyDataSetChanged();
                if (count > 4) {
                    Toast.makeText(context, "Alert : " + "\n" + "You are currently in contact with 5 or more than 5 devices", Toast.LENGTH_SHORT).show();
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                }
            }else{}
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                //out.append("MAKING YOUR DEVICE DISCOVERABLE");
                Toast.makeText(context, "Make your device visible to continue searching", Toast.LENGTH_SHORT).show();
                stop.setEnabled(false);
                stop.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                search.setEnabled(true);
            }else{
                mBluetoothAdapter.startDiscovery();
            }
            count = 0;
            names.clear();
            local.clear();
        }
    }

    boolean isPresentInLocal(String s){
        for(int i = 0; i<local.size();i++){
            if(s.equals(local.get(i)))
                return true;
        }
        return false;
    }
}
