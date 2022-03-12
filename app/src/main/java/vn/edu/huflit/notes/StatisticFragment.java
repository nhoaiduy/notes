package vn.edu.huflit.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment implements NotesAdapter.Listener {

    RecyclerView recyclerView;
    ArrayList<Model> notesList;
    NotesAdapter notesAdapter;
    Database database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        notesList = new ArrayList<>();
        database = new Database(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fetchAllNotesFromDatabase();
        final AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.edit_ip);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.myarray));
        textView.setAdapter(adapter);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(spinner.getSelectedItem().toString());
                textView.dismissDropDown();
                ArrayList<Model> arrayList = filterModel(id);
                notesAdapter = new NotesAdapter(arrayList, StatisticFragment.this);
                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView.setText(spinner.getSelectedItem().toString());
                textView.dismissDropDown();
            }
        });
    }

    void fetchAllNotesFromDatabase(){
        Cursor cursor = database.readAllData();
        if (cursor.getCount()==0){
            Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                notesList.add(new Model(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Model> filterModel(long id){
        ArrayList<Model> arrayList =new ArrayList<>();
        int length = notesList.size();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (id == 0){
            Date date = cal.getTime();
            String today = sdf.format(date).toString();
            for (int i = 0; i<length;i++){
                if(today.equals(notesList.get(i).getDeadline())){
                    arrayList.add(notesList.get(i));
                }

            }
        } else if (id == 1){
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();
            String tomorrow = sdf.format(date).toString();
            Log.d("ABC", tomorrow);
            for (int i = 0; i<length;i++){
                if(tomorrow.equals(notesList.get(i).getDeadline())){
                    arrayList.add(notesList.get(i));
                    Log.d("ABC", notesList.get(i).getDeadline());
                }

            }
        }
        return arrayList;
    }

    @Override
    public void onClick(Model model) {
        Intent intent= new Intent(getContext(), DeatailActivity.class);
        intent.putExtra("model", model);
        startActivity(intent);
    }

    @Override
    public void onLongClick(Model model) {
        buttonOpenDialogClicked(model.getId());
    }

    private void buttonOpenDialogClicked(String id)  {
        DeleteDialog.Listener listener = new DeleteDialog.Listener() {
            @Override
            public void deleteNote() {
                database.deleteNote(id);
                notesList.clear();
                fetchAllNotesFromDatabase();
                notesAdapter = new NotesAdapter(notesList, StatisticFragment.this);
                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            }
        };

        final DeleteDialog dialog = new DeleteDialog(getContext(), listener);

        dialog.show();
    }
}