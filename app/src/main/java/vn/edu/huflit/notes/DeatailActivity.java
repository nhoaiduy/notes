package vn.edu.huflit.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DeatailActivity extends AppCompatActivity {

    TextView deadline, description;
    Toolbar toolbar;
    Model model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatail);
        deadline = findViewById(R.id.txtDeadline);
        description = findViewById(R.id.txtDescription);
        toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        Intent intent = getIntent();
        model = (Model) intent.getSerializableExtra("model");
        getSupportActionBar().setTitle(model.getTitle());
        description.setText(model.getDescription());
        deadline.setText(model.getDeadline());


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.edit){
            Intent intent = new Intent(this, UpdateNoteActivity.class);
            intent.putExtra("model", model);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
}