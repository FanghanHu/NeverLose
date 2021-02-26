package net.fanghanhu.neverlose;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import net.fanghanhu.neverlose.data.AppDatabase;
import net.fanghanhu.neverlose.data.Device;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String DEVICE_MAC = "net.fanghanhu.neverlose.DEVICE_MAC";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup deviceList = findViewById(R.id.deviceList);

        //TODO: request location permissions

        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "NeverLose").allowMainThreadQueries().build();
        List<Device> devices = db.deviceDAO().getAllDevices();

        for(final Device device : devices)
        {
            DeviceCardView deviceCardView = new DeviceCardView(this, null);
            deviceCardView.init(device);

            deviceCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    viewHistories(device.mac);
                }
            });

            deviceList.addView(deviceCardView);
        }
    }

    private void viewHistories(String mac)
    {
        Intent intent = new Intent(this, HistoryListActivity.class);
        intent.putExtra(DEVICE_MAC, mac);
        startActivity(intent);
    }

    public static void showMap(Context context, double latitude, double longitude, String label)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + "0,0?q=" + latitude + "," + longitude + " (" + Uri.encode(label) + ")"));
        context.startActivity(intent);
    }
}
