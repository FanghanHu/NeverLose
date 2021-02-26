package net.fanghanhu.neverlose;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String notice1 = "This app uses your location data in the background when you connect or disconnect to a Bluetooth device, the data will be retained on your device and is not collected by us in any way. ";

            String notice2 = "<span>By downloading or using the app, you agree to the NeverLose <a href='https://github.com/FanghanHu/NeverLose/blob/master/TermsAndConditions.md'>Terms and Conditions</a> " +
                    "and <a href='https://github.com/FanghanHu/NeverLose/blob/master/PrivacyPolicy.md'>Privacy Policy</a>.</span>";


            final LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(40, 40, 40, 40);

            final TextView message = new TextView(MainActivity.this);
            message.setText(notice1);

            final TextView message2 = new TextView(MainActivity.this);
            message2.setText(Html.fromHtml(notice2));
            message2.setMovementMethod(LinkMovementMethod.getInstance());

            layout.addView(message, layoutParams);
            layout.addView(message2, layoutParams);

            //No background location permission
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Accept Terms")
                    .setView(layout)
                    //yes button
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            {
                                //ask for background location usage
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                }, 1);
                            } else {
                                //Ask for permissions to use locations
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                }, 0);
                            }
                        }
                    })

                    //no button
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Exit program
                            System.exit(0);
                        }
                    })
                    .show();
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
