package vn.edu.huflit.notes;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NotesAdapter.Listener {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    NotesAdapter notesAdapter;
    ArrayList<Model> notesList;
    Database database;
    String [] s;
    public int day,month,year, d, m, y, count1, count2;
    Calendar c = Calendar.getInstance();
    public static int countNoti = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        notesList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        database = new Database(getContext());
        fetchAllNotesFromDatabase();
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddNotesActivity.class);
                startActivity(intent);
            }
        });

        final AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.edit_ip);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.myarray));
        textView.setAdapter(adapter);
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(spinner.getSelectedItem().toString());
                textView.dismissDropDown();
                ArrayList<Model> arrayList = sortNotes(id);
                notesAdapter = new NotesAdapter(arrayList, HomeFragment.this);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.search){
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        d = c.get(Calendar.DATE);
        m = c.get(Calendar.MONTH)+1;
        y = c.get(Calendar.YEAR);
        count2 = calDay(m,y);
        count2+=d;
        if (countNoti==0){
            ArrayList<Model> noti = new ArrayList<>();
            for (int i = 0; i< notesList.size();i++){
                s = notesList.get(i).getDeadline().split("-");
                day = Integer.parseInt(s[0]);
                month = Integer.parseInt(s[1]);
                year = Integer.parseInt(s[2]);
                count1 = calDay(month, year);
                count1+=day;
                count1+=(year-y)*(calDay(12,y)+31-count2);
                if(count1-count2<=3 && count1-count2>=0){
                    SendNotification(notesList.get(i), count1);
                }
            }
            countNoti=1;
        }else if(countNoti==2){
            Intent intent = getActivity().getIntent();
            Model model = (Model) intent.getSerializableExtra("model");
            s = model.getDeadline().split("-");
            day = Integer.parseInt(s[0]);
            month = Integer.parseInt(s[1]);
            year = Integer.parseInt(s[2]);
            count1 = calDay(month, year);
            count1+=day;
            count1+=(year-y)*(calDay(12,y)+31-count2);
            if(count1-count2<=3 && count1-count2>=0){
                SendNotification(model, count1);
            }
            countNoti=1;
        }
    }

    public static int calDay(int month, int year){
        int count = 0;
        switch (month-1){
            case 11:
                count +=30;
            case 10:
                count +=31;
            case 9:
                count +=30;
            case 8:
                count +=31;
            case 7:
                count +=31;
            case 6:
                count +=30;
            case 5:
                count +=31;
            case 4:
                count +=30;
            case 3:
                count +=31;
            case 2:
                if(year%100==0 && year%4==0 && year%400==0){
                    count+=29;
                }else count+=28;
            case 1:
                count +=31;
        }
        return count;
    }

    //Send Notification
    public void SendNotification(Model model, int count){
        String s;
        if(count==count2) s= "Đã đến hạn!";
        else s = "Chỉ còn "+ (count-count2) +" ngày!";
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.notification);

        Notification notification = new NotificationCompat.Builder(getContext(), CreateChannel.CHANNEL_ID)
                .setContentTitle(model.getTitle()+"!")
                .setContentText(s)
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(b)
                .build();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getNotificationId(), notification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    ArrayList<Model> sortNotes(long id){
        ArrayList<Model> arrayList = notesList;
        if(id==1){
            Collections.sort(arrayList, Comparator.comparing(Model::getTitle));
        }else if(id == 2){
            Collections.sort(arrayList, Comparator.comparing(Model::getTitle));
            Collections.reverse(arrayList);
        }else{
            notesList.clear();
            fetchAllNotesFromDatabase();
            arrayList=notesList;
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
                notesAdapter = new NotesAdapter(notesList, HomeFragment.this);
                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            }
        };

        final DeleteDialog dialog = new DeleteDialog(getContext(), listener);

        dialog.show();
    }

}