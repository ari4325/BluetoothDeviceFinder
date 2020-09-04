package com.bdf.bluetoothdevicefinder;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;


/**
 * A simple {@link Fragment} subclass.
 */
public class Nearby extends Fragment {
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



    public Nearby() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearby, container, false);
        Intent intent = new Intent(getActivity().getApplicationContext(), MyService.class);
        getActivity().stopService(intent);
        ListDevice.setContext(getActivity().getApplicationContext());
        ListDevice.load();
        if (mBluetoothAdapter == null) {
            out.append("device not supported");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        stop = v.findViewById(R.id.stop);
        stop.setEnabled(false);
        stop.setVisibility(View.GONE);
        search = v.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBTpermissions();
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    //out.append("MAKING YOUR DEVICE DISCOVERABLE");
                    Toast.makeText(v.getContext(), "MAKING YOUR DEVICE DISCOVERABLE",
                            Toast.LENGTH_LONG).show();

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(enableBtIntent);
                }else {
                    Toast.makeText(v.getContext(), "Device is already discoverable", Toast.LENGTH_SHORT).show();
                }
                if(!mBluetoothAdapter.isDiscovering()) {
                    /*IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    getActivity().registerReceiver(receiver, filter);*/
                    Intent intent1 = new Intent();
                    intent1.setAction(BluetoothDevice.ACTION_FOUND);
                    intent1.setAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    getActivity().sendBroadcast(intent1);
                    mBluetoothAdapter.startDiscovery();
                    Toast.makeText(v.getContext(), "Started Searching for nearby devices", Toast.LENGTH_SHORT).show();
                    search.setVisibility(View.GONE);
                    search.setEnabled(false);
                    stop.setEnabled(true);
                    stop.setVisibility(View.VISIBLE);
                    searchCheck = true;
                }else{
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isDiscovering()){
                    getActivity().unregisterReceiver(receiver);
                    Toast.makeText(v.getContext(), "Stopped Searching for nearby devices", Toast.LENGTH_SHORT).show();
                    stop.setEnabled(false);
                    stop.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    search.setEnabled(true);
                    searchCheck = false;
                }
            }
        });

        listView = v.findViewById(R.id.listDeviceNames);
        ListDevice.setContext(getActivity().getApplicationContext());
        ListDevice.load();
        arrayAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);

        return v;
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    public void onDestroy(){
        if(searchCheck) {
            Intent intent = new Intent(getActivity().getApplicationContext(), MyService.class);
            getActivity().startService(intent);
        }
        super.onDestroy();
    }

    void checkBTpermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck+=getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            permissionCheck+=getActivity().checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if(permissionCheck!=0){
                getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        }
    }


}
