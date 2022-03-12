package vn.edu.huflit.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class AddNotesActivity extends AppCompatActivity {

    EditText title, description;
    Button addNote;
    DatePicker datePicker;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        addNote = findViewById(R.id.addNote);
        datePicker = findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final String[] deadline = {""};
        if (calendar.get(Calendar.DAY_OF_MONTH)<10 && (calendar.get(Calendar.MONTH)+1)<10){
            deadline[0] = "0"+calendar.get(Calendar.DAY_OF_MONTH)+"-0"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
        }else if (calendar.get(Calendar.DAY_OF_MONTH)<10){
            deadline[0] = "0"+calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
        }else if (calendar.get(Calendar.MONTH+1)<10) {
            deadline[0] = calendar.get(Calendar.DAY_OF_MONTH) + "-0" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if (month<12){
                    month++;
                }
                if (dayOfMonth<10 && month<9){
                    deadline[0] = "0"+dayOfMonth+"-0"+(month)+"-"+year;
                }else if (dayOfMonth<10){
                    deadline[0] = "0"+dayOfMonth+"-"+(month)+"-"+year;
                }else if (month<9) {
                    deadline[0] = dayOfMonth + "-0" + (month) + "-" + year;
                }
                Log.d("ABC", deadline[0]);
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(title.getText().toString()) && !TextUtils.isEmpty(description.getText().toString())){

                    Database database= new Database(AddNotesActivity.this);
                    database.addNote(title.getText().toString(), description.getText().toString(), deadline[0]);
                    HomeFragment.countNoti = 2;
                    Model model = new Model("?",title.getText().toString(), description.getText().toString(),  deadline[0]);
                    Intent intent = new Intent(AddNotesActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("model",model);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(AddNotesActivity.this, "Yêu cầu nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}