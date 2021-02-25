package net.fanghanhu.neverlose.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Device
{
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo
    public String mac;

    @ColumnInfo
    public String name;
}
