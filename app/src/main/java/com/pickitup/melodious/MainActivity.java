package com.pickitup.melodious;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends Activity
  {

    //variables
    ImageView record, stop_record, play, stop;
    LinearLayout view_new, view_best, view_exclusive, post;
    Button test_buttonx;
    String save_path = "";
    MediaRecorder recorder;
    MediaPlayer player;

    final int REQUEST_PERMISSION_CODE = 1000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request runtime permission
        if(!checkPermissionFromDevice())
        {
            requestPermissions();
        }

        //initialize views
        record = (ImageView) findViewById(R.id.start_record);
        stop_record = (ImageView) findViewById(R.id.stop_record);
        play = (ImageView) findViewById(R.id.play);
        stop = (ImageView) findViewById(R.id.stop);
        view_new = (LinearLayout) findViewById(R.id.view_new);
        view_best = (LinearLayout) findViewById(R.id.view_best);
        view_exclusive = (LinearLayout) findViewById(R.id.view_exclusive);
        post = (LinearLayout) findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MelodyView.class);
                startActivity(intent);
            }
        });

        view_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_new.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_category));
                view_best.setBackground(null);
                view_exclusive.setBackground(null);
            }
        });
        view_best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_best.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_category));
                view_new.setBackground(null);
                view_exclusive.setBackground(null);
            }
        });
        view_exclusive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_exclusive.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_category));
                view_new.setBackground(null);
                view_best.setBackground(null);
            }
        });
        record.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Toast.makeText(MainActivity.this, "Tyring to record...", Toast.LENGTH_SHORT).show();

                if(checkPermissionFromDevice())
                {
                    save_path = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_record.mp3";
                    setupMediaRecorder();
                    try
                    {
                        recorder.prepare();
                        recorder.start();
                    }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        requestPermissions();
                    }
                    return true;
            }
        });

        stop_record.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                    {
                        recorder.stop();
                        Toast.makeText(MainActivity.this, "Stopping recording...", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

        play.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                    {
                        try
                        {
                            player.setDataSource(save_path);
                            player.prepare();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        player.start();
                        Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        stop.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                    {
                        if(player != null)
                        {
                            player.stop();
                            player.release();
                            setupMediaRecorder();
                        }
                    }
                }
                return true;
            }
        });
    }

    private void setupMediaRecorder()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(save_path);
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private boolean checkPermissionFromDevice()
    {
        int write_external_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return  write_external_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
