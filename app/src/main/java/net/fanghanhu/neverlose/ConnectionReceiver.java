package net.fanghanhu.neverlose;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import androidx.room.Room;

import net.fanghanhu.neverlose.data.AppDatabase;
import net.fanghanhu.neverlose.data.Device;
import net.fanghanhu.neverlose.data.History;

import java.util.Date;

public class ConnectionReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        //System.out.println("Address:" + device.getAddress());
        //System.out.println("Name:" + device.getName());

        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "NeverLose").allowMainThreadQueries().build();
        Device target = db.deviceDAO().getDevice(device.getAddress());
        if (target == null || target.mac == null)
        {
            target = new Device();
            target.mac = device.getAddress();
            target.name = device.getName();

            db.deviceDAO().insertAll(target);
        }

        History history = new History();
        history.mac = target.mac;
        history.time = new Date(System.currentTimeMillis());

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        if (provider == null)
        {
            history.latitude = 0;
            history.longitude = 0;
        } else
        {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }

            Location location = locationManager.getLastKnownLocation(provider);
            history.latitude = location.getLatitude();
            history.longitude = location.getLongitude();


        }

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
            //Device is now connected
            history.isConnect = true;
            db.deviceDAO().insertAll(history);
            db.deviceDAO().purge(target.mac);

        }else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
            //Device has disconnected
            history.isConnect = false;
            db.deviceDAO().insertAll(history);
            db.deviceDAO().purge(target.mac);
        }
    }
}
