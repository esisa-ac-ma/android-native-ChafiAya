package esisa.ac.ma.projet_natif.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import esisa.ac.ma.projet_natif.entities.Favorite;
@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM T_favorite")
    List<Favorite> getAll();

    @Insert
    void add(Favorite favorite);
    // @Transaction
    @Query("SELECT * FROM T_favorite WHERE phone = :phone")
    Favorite getFavoriteByPhone(String phone);
    @Update
    int update(Favorite favorite);

    @Delete
    void delete(Favorite favorite);
}