package com.example.pomelipo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewWorkSession extends AppCompatActivity {

    private EditText txtWorkSessionName;
    private EditText txtWorkTime;
    private EditText txtChillTime;
    private EditText txtRepetitions;
    private Button btnAdd;

    private ConectorDB conectorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_session);

        txtWorkSessionName = findViewById(R.id.txtWorkSessionName);
        txtWorkTime = findViewById(R.id.txtWorkTime);
        txtChillTime = findViewById(R.id.txtChillTime);
        txtRepetitions = findViewById(R.id.txtRepetitions);
        btnAdd = findViewById(R.id.btnAdd);
        conectorDB = new ConectorDB(this);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (there_are_info_gaps()){
                    Toast.makeText(getBaseContext(), "There are empty fields", Toast.LENGTH_SHORT).show();
                }else if (already_exists(txtWorkSessionName.getText().toString())){
                    Toast.makeText(getBaseContext(), "This Work Session already exists", Toast.LENGTH_SHORT).show();
                }else{
                    addNewWorkSession();
                    Toast.makeText(getBaseContext(), "Work Session created correctly", Toast.LENGTH_SHORT).show();
                    go_back_to_main();
                }
            }
        });
    }

    private boolean already_exists(String sessionName){
        conectorDB.open();
        Cursor cursor = conectorDB.readWorkSession(sessionName);
        if (cursor.getCount() != 0){
            return true;
        }
        conectorDB.close();
        return false;
    }

    private boolean there_are_info_gaps(){
        return txtWorkSessionName.getText().toString().equals("") || txtWorkTime.getText().toString().equals("") || txtChillTime.getText().toString().equals("") || txtRepetitions.getText().toString().equals("");
    }

    private void go_back_to_main(){
        startActivity(new Intent(NewWorkSession.this, MainActivity.class));
    }

    private void addNewWorkSession(){
        conectorDB.open();
        conectorDB.createWorkSession(txtWorkSessionName.getText().toString(), Integer.parseInt(txtWorkTime.getText().toString()), Integer.parseInt(txtChillTime.getText().toString()), Integer.parseInt(txtRepetitions.getText().toString()));
        conectorDB.close();
    }

}