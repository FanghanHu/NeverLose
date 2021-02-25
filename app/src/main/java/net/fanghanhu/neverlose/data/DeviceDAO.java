package net.fanghanhu.neverlose.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDAO
{
    @Query("SELECT * FROM device")
    List<Device> getAllDevices();

    @Query("SELECT * FROM history WHERE mac LIKE :mac ORDER BY uid DESC")
    List<History> listHistory(String mac);

    @Query("SELECT * FROM device WHERE mac LIKE :mac")
    Device getDevice(String mac);

    @Query("SELECT * FROM history WHERE mac LIKE :mac ORDER BY uid DESC LIMIT 1")
    History getLastHistory(String mac);

    //Keep only last 100 records
    @Query("DELETE FROM history WHERE uid IN (SELECT uid FROM history WHERE mac LIKE :mac ORDER BY uid DESC LIMIT -1 OFFSET 100)")
    void purge(String mac);

    @Insert
    void insertAll(History... histories);

    @Insert
    void insertAll(Device... devices);

    @Delete
    void delete(Device device);
}
