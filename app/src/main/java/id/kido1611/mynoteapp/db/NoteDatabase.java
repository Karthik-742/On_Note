package id.kido1611.mynoteapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import id.kido1611.mynoteapp.entity.Note;

@Database(
        entities = {
                Note.class
        },
        version = 1,
        exportSchema = false
)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDAO noteDAO();

    private static volatile NoteDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NoteDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (NoteDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NoteDatabase.class,
                            "note_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
