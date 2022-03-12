package vn.edu.huflit.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements NotesAdapter.Listener {

    Toolbar toolbar;
    SearchView searchView;
    NotesAdapter notesAdapter;
    ArrayList<Model> notesList;
    RecyclerView recyclerView;
    LinearLayout linearEmpty;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar= findViewById(R.id.tb_search);
        searchView = toolbar.findViewById(R.id.searchView);
        linearEmpty = findViewById(R.id.linearEmpty);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        notesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        database = new Database(this);
        fetchAllNotesFromDatabase();
        setVisible(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.getFilter().filter(newText);
                if(notesAdapter.notesFilterList.size()==0 || newText.isEmpty()){
                    setVisible(false);
                }else {
                    setVisible(true);
                }
                return false;
            }
        });
    }
    ArrayList<Model> fetchAllNotesFromDatabase(){
        Cursor cursor = database.readAllData();
        if (cursor.getCount()==0){
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                notesList.add(new Model(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
        }
        return notesList;
    }

    public void setVisible(boolean flag){
        if(!flag){
            linearEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            linearEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Model model) {
        Intent intent= new Intent(this, DeatailActivity.class);
        intent.putExtra("model", model);
        startActivity(intent);
    }

    @Override
    public void onLongClick(Model model) {

    }
}