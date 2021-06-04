package com.example.pomelipo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView lstWorkSessions;
    private ConectorDB conectorDB;
    private ArrayList<WorkSession> workSessions = new ArrayList<>();
    private WorkSessionListAdapter workSessionListAdapter;

    private Boolean onProgressSession = false;
    private Boolean sessionState = false; // true == worktime , false == chilltime
    private CountDownTimer workCountDownTimer;
    private CountDownTimer chillCountDownTimer;

    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder workBuilder;
    NotificationCompat.Builder chillBuilder;

    //private Boolean workTimefinsihed = false;
    //private Boolean chillTimeFinsihed = false;

    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewWorkSession.class));
            }
        });

        lstWorkSessions = findViewById(R.id.lstWorkSessions);

        conectorDB = new ConectorDB(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        lstWorkSessions.setLayoutManager(mLayoutManager);
        workSessionListAdapter = new WorkSessionListAdapter(workSessions);
        lstWorkSessions.setAdapter(workSessionListAdapter);

        //List all the WorkSessions
        getAllWorkSessions();

        //List Adapter
        workSessionListAdapter.setItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onWorkSessionContextualMenu(int position, MenuItem menu) {
                int id = menu.getItemId();

                if (id == R.id.Borrar) {
                    removeWorkSession(workSessions.get(position).getSessionName());
                    workSessions.remove(workSessions.get(position));
                    notifyListChanged();
                }
            }

            @Override
            public void onStartPressed(int position, ImageButton btnStart, ImageButton btnStop) {
                if (!isSessionInProgress()){
                    switchButtons("StartPressed", btnStart, btnStop);
                    startWorkSession(workSessions.get(position).getWorkTime() * 60000,
                            workSessions.get(position).getChillTime() * 60000,
                            workSessions.get(position).getRepetitions(),
                            btnStart,
                            btnStop);
                }
            }

            @Override
            public void onStopPressed(ImageButton btnStart, ImageButton btnStop) {
                if(isSessionInProgress()){
                    switchButtons("StopPressed", btnStart, btnStop);
                    stopWorkSession(btnStart, btnStop);
                }
            }
        });

        workBuilder = new NotificationCompat.Builder(this, "notificationChannel")
                .setSmallIcon(R.drawable.outline_notifications_black_18)
                .setContentTitle("Work Time!")
                .setContentText("Keep focused until you finish this round")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(Color.BLUE,500, 500)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND);

        chillBuilder = new NotificationCompat.Builder(this, "notificationChannel")
                .setSmallIcon(R.drawable.outline_notifications_black_18)
                .setContentTitle("Chill Time!")
                .setContentText("Rest in peace")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(Color.BLUE,500, 500)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND);

        notificationManagerCompat = NotificationManagerCompat.from(this);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            String description = "Notificacion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notificationChannel",name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startWorkSession(int workTime, int chillTime, int repetitions, ImageButton btnStart, ImageButton btnStop){
        setOnProgressSession(true);
        notificationManagerCompat.notify(100, workBuilder.build());
        counter = 1;
        sessionState = true;
        startWorkTimer(workTime, chillTime, repetitions, btnStart, btnStop);
    }

    private void stopWorkSession(ImageButton btnStart, ImageButton btnStop){
        setOnProgressSession(false);
        stopTimer();
    }

    private void startWorkTimer(long workTime, long chillTime, int repetitions, ImageButton btnStart, ImageButton btnStop){
        workCountDownTimer = new CountDownTimer(workTime,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                ;
            }

            @Override
            public void onFinish() {
                notificationManagerCompat.notify(100, chillBuilder.build());
                counter++;
                sessionState = false;
                startChillTimer(chillTime, workTime, repetitions, btnStart, btnStop);
            }
        }.start();
    }

    private void startChillTimer(long chillTime, long workTime, int repetitions, ImageButton btnStart, ImageButton btnStop){
        chillCountDownTimer = new CountDownTimer(chillTime,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                ;
            }

            @Override
            public void onFinish() {
                if (counter <= repetitions){
                    sessionState = true;
                    notificationManagerCompat.notify(100, workBuilder.build());
                    startWorkTimer(workTime, chillTime, repetitions, btnStart, btnStop);
                }else{
                    switchButtons("StopPressed", btnStart, btnStop);
                    setOnProgressSession(false);
                }
            }
        }.start();
    }

    private void stopTimer(){
        if(sessionState){
            workCountDownTimer.cancel();
        }else{
            chillCountDownTimer.cancel();
        }
    }

    private void removeWorkSession(String sessionName){
        conectorDB.open();
        conectorDB.deleteWorkSession(sessionName);
        conectorDB.close();
    }

    private void getAllWorkSessions (){
        workSessions.removeAll(workSessions);
        conectorDB.open();
        Cursor c = conectorDB.readAllWorkSessions();
        if (c.moveToFirst())
        {
            do {
                WorkSession ws = new WorkSession();
                ws.setSessionName(c.getString(0));
                ws.setWorkTime(c.getInt(1));
                ws.setChillTime(c.getInt(2));
                ws.setRepetitions(c.getInt(3));
                workSessions.add(ws);
            } while (c.moveToNext());
        }
        c.close();
        notifyListChanged();
    }

    public void notifyListChanged (){
        lstWorkSessions.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_acercaDe) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Developers");
            builder.setMessage("Josué Carlos Zenteno Yave\nSergio Silvestre Pavón");
            builder.setPositiveButton("OK",null);
            builder.create();
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isSessionInProgress(){
        return onProgressSession;
    }

    public void setOnProgressSession(Boolean onProgressSession) {
        this.onProgressSession = onProgressSession;
    }

    private void switchButtons(String mode, ImageButton btnStart, ImageButton btnStop){
        switch(mode){
            case "StartPressed":
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                break;
            case "StopPressed":
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                break;
        }
    }
}