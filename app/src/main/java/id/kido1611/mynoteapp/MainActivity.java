package id.kido1611.mynoteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import id.kido1611.mynoteapp.adapter.NoteAdapter;
import id.kido1611.mynoteapp.db.NoteDatabase;
import id.kido1611.mynoteapp.entity.Note;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;

    private ProgressBar progressBar;
    private RecyclerView rvNotes;
    private NoteAdapter adapter;

    private NoteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = NoteDatabase.getDatabase(this);

        progressBar = findViewById(R.id.progressBar);
        rvNotes = findViewById(R.id.rv_notes);
        fabAdd = findViewById(R.id.fab_add);

        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
            startActivityForResult(i, NoteAddUpdateActivity.REQUEST_ADD);
        });

        NoteDatabase.databaseWriteExecutor.execute(() -> {
            progressBar.setVisibility(View.VISIBLE);
            List<Note> notes = database.noteDAO().getNotes();
            adapter.setListNotes(notes);
            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(requestCode == NoteAddUpdateActivity.REQUEST_ADD){
                if(resultCode == NoteAddUpdateActivity.RESULT_ADD){
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    adapter.additem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount()-1);

                    showSnackbarMessage("Satu item berhasil ditambahkan");
                }
            }
            else if(requestCode == NoteAddUpdateActivity.REQUEST_UPDATE){
                if(resultCode == NoteAddUpdateActivity.RESULT_UPDATE){
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.updateItem(position, note);
                    rvNotes.smoothScrollToPosition(position);

                    showSnackbarMessage("Satu item berhasil diubah");
                }
                else if(resultCode == NoteAddUpdateActivity.RESULT_DELETE){
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.removeItem(position);

                    showSnackbarMessage("Satu item berhasil dihapus");
                }
            }
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_LONG).show();
    }
}
