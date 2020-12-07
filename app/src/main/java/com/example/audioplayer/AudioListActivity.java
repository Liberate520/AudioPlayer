package com.example.audioplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class AudioListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayToView;
    ArrayAdapter adapter;
    ArrayList<String> path = new ArrayList<>();

    final int MY_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_list);

        listView = findViewById(R.id.listView);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
        }
        else {
            fillList();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
                    fillList();
                }
                else {
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songPath);
                arrayToView.add(currentTitle + "\n" + currentArtist);
                path.add(currentPath);
//                listTrack.add(new AudioModel(currentTitle, currentArtist, currentPath));
            }while (songCursor.moveToNext());
        }
    }

    public void fillList(){
        arrayToView = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayToView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("num", i);
                intent.putExtra("listTrack", path);
                intent.setClass(AudioListActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });
    }
}