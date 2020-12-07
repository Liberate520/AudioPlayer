package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView progress, remained;
    SeekBar seekBar;
    ImageView prev, next, play, pause;
    MediaPlayer mediaPlayer;
    int currentTrack = 0;
    ArrayList<String> listTrack = new ArrayList<>();
    int idTrack = R.raw.music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        progress = findViewById(R.id.progress);
        remained = findViewById(R.id.remained);
        seekBar = findViewById(R.id.seekBar);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);

        currentTrack = (int)getIntent().getSerializableExtra("num");
        listTrack = (ArrayList<String>)getIntent().getSerializableExtra("listTrack");

        mediaPlayer = MediaPlayer.create(this, idTrack);
        seekBar.setMax(mediaPlayer.getDuration());

        MyThread myThread = new MyThread();
        myThread.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);

                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(listTrack.get(currentTrack)));
                mediaPlayer.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTrack++;
                if (currentTrack == listTrack.size()){
                    currentTrack = 0;
                }
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(listTrack.get(currentTrack)));
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTrack--;
                if (currentTrack < 0){
                    currentTrack = listTrack.size()-1;
                }
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(listTrack.get(currentTrack)));
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mediaPlayer.seekTo(i);
                progress.setText(format());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public String format(){
        int min = mediaPlayer.getCurrentPosition()/1000/60;
        int sec = mediaPlayer.getCurrentPosition()/1000 - min*60;
        String result = "";
        if (sec > 9) {
            result = "" + min + ":" + sec;
        }else {
            result = "" + min + ":0" + sec;
        }
        return result;
    }


    class MyThread extends Thread{
        @Override
        public void run() {
            while (true){
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                try {
                    sleep(500);
                }catch (InterruptedException e){
                    e.getStackTrace();
                }
            }
        }
    }
}