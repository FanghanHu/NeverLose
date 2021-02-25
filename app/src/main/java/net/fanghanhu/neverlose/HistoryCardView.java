package net.fanghanhu.neverlose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import net.fanghanhu.neverlose.data.History;

import java.text.SimpleDateFormat;

public class HistoryCardView extends FrameLayout
{
    public History history;

    public HistoryCardView(@NonNull Context context)
    {
        super(context);

        inflate(context, R.layout.view_history_card, this);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void init(History history)
    {
        ImageView icon = findViewById(R.id.historyConnectionIcon);
        TextView timeText = findViewById(R.id.historyTimeText);

        if(history == null)
        {
            timeText.setText("what?");
        }
        else
        {
            if(history.isConnect)
                icon.setImageTintList(this.getResources().getColorStateList(R.color.connect, null));
            else
                icon.setImageTintList(this.getResources().getColorStateList(R.color.disconnect, null));

            final String timeString = new SimpleDateFormat("MM/dd/yyyy hh:mmaa").format(history.time);
            timeText.setText(timeString);

            View button = this.findViewById(R.id.historyFindButton);
            final History target = history;

            button.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MainActivity.showMap(HistoryCardView.this.getContext(), target.latitude, target.longitude, timeString);
                }
            });
        }
    }
}
