package net.fanghanhu.neverlose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import net.fanghanhu.neverlose.data.AppDatabase;
import net.fanghanhu.neverlose.data.Device;
import net.fanghanhu.neverlose.data.History;

import java.text.SimpleDateFormat;

public class DeviceCardView extends ConstraintLayout
{
    public Device device;

    public DeviceCardView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        inflate(context, R.layout.view_device_card, this);
    }

    @SuppressLint("SimpleDateFormat")
    public void init(Device device)
    {
        if(device == null)
        {
            device = new Device();
            device.name = "error";
            device.mac = "error";
        }

        ((TextView)this.findViewById(R.id.deviceNameText)).setText(device.name);
        ((TextView)this.findViewById(R.id.macAddressText)).setText(device.mac);

        AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "NeverLose").allowMainThreadQueries().build();
        final History history = db.deviceDAO().getLastHistory(device.mac);
        if(history != null)
        {
            final Device target = device;
            ((TextView)this.findViewById(R.id.lastSeenText)).setText(new SimpleDateFormat("MM/dd/yyyy hh:mmaa").format(history.time));
            View button = this.findViewById(R.id.findButton);

            button.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MainActivity.showMap(DeviceCardView.this.getContext(), history.latitude, history.longitude, target.name);
                }
            });
        }
        else
        {
            ((TextView)this.findViewById(R.id.lastSeenText)).setText(getContext().getString(R.string.unknown));
            this.findViewById(R.id.findButton).setVisibility(INVISIBLE);
        }
        this.device = device;
    }
}
