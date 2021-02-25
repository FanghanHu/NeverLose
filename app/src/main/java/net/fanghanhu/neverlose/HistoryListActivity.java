package net.fanghanhu.neverlose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import net.fanghanhu.neverlose.data.AppDatabase;
import net.fanghanhu.neverlose.data.Device;
import net.fanghanhu.neverlose.data.History;

import java.util.List;

public class HistoryListActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        Intent intent = getIntent();
        String mac = intent.getStringExtra(MainActivity.DEVICE_MAC);

        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "NeverLose").allowMainThreadQueries().build();
        Device device = db.deviceDAO().getDevice(mac);

        if(device != null)
            this.setTitle(device.name);

        final List<History> histories = db.deviceDAO().listHistory(mac);

        RecyclerView historyList = findViewById(R.id.historyList);

        historyList.setHasFixedSize(true);
        historyList.setLayoutManager(new LinearLayoutManager(this));


        historyList.setAdapter(new RecyclerView.Adapter()
        {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                HistoryCardView view = new HistoryCardView(HistoryListActivity.this);
                return new HistoryCardViewerHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
            {
                History target = histories.get(position);
                ((HistoryCardViewerHolder)holder).view.init(target);
            }

            @Override
            public int getItemCount()
            {
                return histories.size();
            }
        });
    }

    public static class HistoryCardViewerHolder extends RecyclerView.ViewHolder
    {
        public HistoryCardView view;
        public HistoryCardViewerHolder(HistoryCardView view)
        {
            super(view);
            this.view = view;

        }
    }
}
