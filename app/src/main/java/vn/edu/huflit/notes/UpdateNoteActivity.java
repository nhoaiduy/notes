package vn.edu.huflit.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class UpdateNoteActivity extends AppCompatActivity {

    EditText title, description, deadline;
    DatePicker datePicker;
    Button updateNote;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

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
        updateNote = findViewById(R.id.updateNote);
        datePicker = findViewById(R.id.datePicker);

        Intent intent = getIntent();
        Model model = (Model) intent.getSerializableExtra("model");
        title.setText(model.getTitle());
        description.setText(model.getDescription());
        String[] dates = model.getDeadline().split("-");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final String[] deadline = {""};
        int day = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int year = Integer.parseInt(dates[2]);
        if (day<10 && (month+1)<9){
            deadline[0] = "0"+day+"-0"+(month)+"-"+year;
        }else if (day<10){
            deadline[0] = "0"+day+"-"+(month)+"-"+year;
        }else if (month+1<9) {
            deadline[0] = day + "-0" + (month) + "-" + year;
        }
        datePicker.init(year, month-1,day, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month+=1;
                if (dayOfMonth<10 && month<10){
                    deadline[0] = "0"+dayOfMonth+"-0"+(month)+"-"+year;
                }else if (dayOfMonth<10){
                    deadline[0] = "0"+dayOfMonth+"-"+(month)+"-"+year;
                }else if (month<10) {
                    deadline[0] = dayOfMonth + "-0" + (month) + "-" + year;
                }
                Log.d("ABC", month+"");
            }
        });

        id = model.getId();

        updateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(title.getText().toString()) && !TextUtils.isEmpty(description.getText().toString())){

                    Database database = new Database(UpdateNoteActivity.this);
                    database.updateNote(title.getText().toString(), description.getText().toString(), id, deadline[0]);
                    Model model = new Model(id,title.getText().toString(), description.getText().toString(),  deadline[0]);
                    Intent intent = new Intent(UpdateNoteActivity.this, MainActivity.class);
                    HomeFragment.countNoti = 2;
                    intent.putExtra("model",model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(UpdateNoteActivity.this, "Yêu cầu nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}