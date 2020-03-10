package id.kido1611.mynoteapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.kido1611.mynoteapp.db.NoteDatabase;
import id.kido1611.mynoteapp.entity.Note;

public class NoteAddUpdateActivity extends AppCompatActivity
    implements View.OnClickListener {

    private EditText edTitle, edDescription;
    private Button btnSubmit;

    private boolean isEdit = false;
    private Note note;
    private int position;

    private NoteDatabase database;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";

    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        database = NoteDatabase.getDatabase(this);

        edTitle = findViewById(R.id.ed_title);
        edDescription = findViewById(R.id.ed_description);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if(note != null){
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }
        else{
            note = new Note();
        }

        String actionBarTitle;
        String btnTitle;

        if(isEdit){
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if(note != null){
                edTitle.setText(note.getTitle());
                edDescription.setText(note.getDescription());
            }
        }
        else{
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit.setText(btnTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isEdit){
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete :
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_submit){
            String title = edTitle.getText().toString();
            String description = edDescription.getText().toString();

            if(TextUtils.isEmpty(title)){
                edTitle.setError("Field cannot be blank");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, note);
            intent.putExtra(EXTRA_POSITION, position);

            if(isEdit){
                note.setTitle(title);
                note.setDescription(description);

                NoteDatabase.databaseWriteExecutor.execute(() -> {
                    database.noteDAO().insert(note);

                    setResult(RESULT_UPDATE, intent);
                    finish();
                });
            }
            else{
                note.setTitle(title);
                note.setDescription(description);
                note.setDate(getCurrentDate());

                NoteDatabase.databaseWriteExecutor.execute(() -> {
                    database.noteDAO().insert(note);

                    setResult(RESULT_ADD, intent);
                    finish();
                });
            }


        }
    }

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if(isDialogClose){
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        }
        else{
            dialogTitle = "Hapus note";
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> {
                    if(isDialogClose){
                        finish();
                    }
                    else{
                        NoteDatabase.databaseWriteExecutor.execute(() -> {
                            database.noteDAO().delete(note.getId());
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            setResult(RESULT_DELETE, intent);
                            finish();
                        });
                    }
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
