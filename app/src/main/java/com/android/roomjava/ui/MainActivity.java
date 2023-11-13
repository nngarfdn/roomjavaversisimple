package com.android.roomjava.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.roomjava.R;
import com.android.roomjava.database.Note;
import com.android.roomjava.database.NoteDao;
import com.android.roomjava.database.NoteRoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private NoteDao mNotesDao;
    private ExecutorService executorService;
    private ListView listView;
    private EditText edtTitle, edtDesc, edtDate;
    private Note selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSeeData = findViewById(R.id.btnnn);
        Button btnUpdate = findViewById(R.id.btn_update);
        listView = findViewById(R.id.listView);
        edtTitle = findViewById(R.id.txt_title);
        edtDesc = findViewById(R.id.txt_desc);
        edtDate = findViewById(R.id.txt_date);

        executorService = Executors.newSingleThreadExecutor();
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNotesDao = db.noteDao();

        getAllNotes();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedNote = (Note) adapterView.getAdapter().getItem(i);
                edtTitle.setText(selectedNote.getTitle());
                edtDesc.setText(selectedNote.getDescription());
                edtDate.setText(selectedNote.getDate());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View arg1, int i, long id) {
                Note item = (Note) adapterView.getAdapter().getItem(i);
                delete(item);
                return true;
            }
        });

        btnSeeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert(new Note(edtTitle.getText().toString(),
                        edtDesc.getText().toString(),
                        edtDate.getText().toString()));
                setEmptyField();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNote.setTitle(edtTitle.getText().toString());
                selectedNote.setDescription(edtDesc.getText().toString());
                selectedNote.setDate(edtDate.getText().toString());
                update(selectedNote);
                setEmptyField();
            }
        });
    }

    private void setEmptyField() {
        edtTitle.setText("");
        edtDesc.setText("");
        edtDate.setText("");
    }


    private void getAllNotes() {
        mNotesDao.getAllNotes().observe(this, notes -> {
            ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this,
                    android.R.layout.simple_list_item_1, notes);
            listView.setAdapter(adapter);
        });
    }

    private void insert(final Note note) {
        executorService.execute(() -> mNotesDao.insert(note));
    }

    private void delete(final Note note) {
        executorService.execute(() -> mNotesDao.delete(note));
    }

    private void update(final Note note) {
        executorService.execute(() -> mNotesDao.update(note));
    }
}