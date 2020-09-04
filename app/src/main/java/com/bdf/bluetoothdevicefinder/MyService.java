package com.bdf.bluetoothdevicefinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyService extends Service {
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "MyService";
    private boolean isRunning  = false;
    private Looper looper;
    private MyServiceHandler myServiceHandler;
    int count = 0;
    public MyService() {
    }
    @Override
    public void onCreate() {
        addDiscoveryNoti("The app is searching for devices in the backgrond", 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getApplicationContext().registerReceiver(receiver, filter);
        mBluetoothAdapter.startDiscovery();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String date = DateFormat.getDateTimeInstance().format(new Date());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                if(rssi > -60) {
                    count++;
                    if (!ListDevice.isPresent(device.getName())) {
                        ListDevice.setContext(getApplicationContext());
                        ListDevice.load();
                        ListDevice.addDevice(device.getName());
                        ListDevice.addDataTime(date);
                        ListDevice.setDate(simpleDateFormat.format(new Date(c.getTimeInMillis())));
                        ListDevice.setTop(device.getName() + "\n" + date + "\n" + rssi + " dBm" + "\n" + device.getAddress());
                        Toast.makeText(context, simpleDateFormat.format(new Date(c.getTimeInMillis())), Toast.LENGTH_SHORT).show();
                        ListDevice.setSearchDate(simpleDateFormat.format(new Date(c.getTimeInMillis())));
                        ListDevice.setMac(device.getAddress());
                        ListDevice.setStrongestConnection(rssi + "");
                        ListDevice.save();
                    } else {
                        int i = ListDevice.isPresentAtPos(device.getName());
                        ListDevice.setContext(getApplicationContext());
                        ListDevice.load();
                        if (rssi > Integer.parseInt(ListDevice.getStrongestConnectionAtPos(i))) {
                            ListDevice.setStrongestConnectionAtPos(rssi + "", i);
                            ListDevice.setDateTimeAtPost(date, i);
                            ListDevice.setTopAtPos(device.getName() + "\n" + date + "\n" + rssi + " dBm" + "\n" + device.getAddress(), i);
                            ListDevice.setSearchDateAtPos(simpleDateFormat.format(new Date(c.getTimeInMillis())), i);
                            ListDevice.setTopAtPos(device.getAddress(), i);
                            Toast.makeText(context, "Swapping", Toast.LENGTH_SHORT).show();
                            ListDevice.save();
                        }
                    }
                    ListDevice.setSearchDate(simpleDateFormat.format(new Date(c.getTimeInMillis())));
                    ListDevice.setSearchName(device.getName());
                    ListDevice.addName(device.getName() + "\n" + date + "\n" + rssi + " dBm");
                    if (count > 4) {
                        Toast.makeText(getApplicationContext(), "Alert : " + "\n" + "You are currently in contact with 5 or more than 5 devices", Toast.LENGTH_SHORT).show();
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                    }
                    ListDevice.save();
                }else{}
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (!mBluetoothAdapter.isEnabled()){
                    addDiscoveryNoti("Turn On Bluetooth On your Device", 1);
                    getApplicationContext().unregisterReceiver(receiver);
                }
                else if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    //out.append("MAKING YOUR DEVICE DISCOVERABLE");
                    addDiscoveryNoti("Turn On Bluetooth Discovery to continue searching", 2);
                }
                else{
                    addDiscoveryNoti("Your device is looking for other devices nearby", 0);
                }
                count = 0;
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        getApplicationContext().unregisterReceiver(receiver);
        isRunning = false;
    }

    private final class MyServiceHandler extends Handler {
        public MyServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
        }
    }

    void addDiscoveryNoti(String message, int i){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.bdf_logo)
                        .setContentTitle("Bluetooth Alert")
                        .setContentText(message)
                        .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setOngoing(true);

        if(i != 0) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .addLine("Turn On Your App to make changes"));
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        final Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);

        if(i == 2) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            getApplicationContext().registerReceiver(receiver, filter);
        }else if(i == 1){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            getApplicationContext().registerReceiver(receiver, filter);
        }
    }
}
