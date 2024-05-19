package com.example.bai_3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button buttonPrev, buttonPause, buttonNext;
    private SeekBar seekBar;
    private TextView textViewStart, textViewEnd;
    private Handler handler = new Handler();
    // Runnable to update the SeekBar and the start time TextView

    private int currentSongIndex;
    private TextView textViewSongName;

    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) ((currentPosition / (double) mediaPlayer.getDuration()) * 100));

                int seconds = (currentPosition / 1000) % 60;
                int minutes = (currentPosition / (1000 * 60)) % 60;
                textViewStart.setText(String.format("%d:%02d", minutes, seconds));

                // Post the Runnable again in one second
                handler.postDelayed(this, 1000);
            }
        }
    };



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Get references to the TextViews
        textViewStart = findViewById(R.id.textViewStart);
        textViewEnd = findViewById(R.id.textViewEnd);
        // Set the start time to 0
        textViewStart.setText("0:00");



        // Get the file paths from the Intent
        ArrayList<String> songFilePaths = getIntent().getStringArrayListExtra("songFilePaths");
        String filePath = getIntent().getStringExtra("filePath");

        // Find the index of the clicked song in the list
        currentSongIndex = songFilePaths.indexOf(filePath);
        // get song name
        textViewSongName = findViewById(R.id.textViewSongName);
        String songName = new File(filePath).getName();
        songName = songName.substring(0, songName.lastIndexOf('.'));
        textViewSongName.setText(songName);


        // Create and start the MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // MediaPlayer is prepared, now you can get the duration
                    int duration = mp.getDuration(); // Duration in milliseconds
                    int seconds = (duration / 1000) % 60;
                    int minutes = (duration / (1000 * 60)) % 60;
                    textViewEnd.setText(String.format("%d:%02d", minutes, seconds));
                    mp.start();
                    // Start updating the SeekBar and the start time TextView
                    handler.post(updateSeekBarRunnable);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Get references to the buttons and SeekBar
        buttonPrev = findViewById(R.id.buttonPrev);
        buttonPause = findViewById(R.id.buttonPause);
        buttonNext = findViewById(R.id.buttonNext);
        seekBar = findViewById(R.id.seekBar);

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "Previous button clicked", Toast.LENGTH_SHORT).show();
                // Implement your logic to play the previous song here
                // Decrement the current song index
                currentSongIndex--;

                // If the current song index is less than 0, set it to the last song
                if (currentSongIndex < 0) {
                    currentSongIndex = songFilePaths.size() - 1;
                }

                // Stop and release the current MediaPlayer
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    // Remove any pending posts of updateSeekBarRunnable that are in the message queue
                    handler.removeCallbacks(updateSeekBarRunnable);
                }

                // Create and start a new MediaPlayer with the previous song
                mediaPlayer = new MediaPlayer();
                try {
                    String prevSongPath = songFilePaths.get(currentSongIndex);
                    mediaPlayer.setDataSource(prevSongPath);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // MediaPlayer is prepared, now you can get the duration
                            int duration = mp.getDuration(); // Duration in milliseconds
                            int seconds = (duration / 1000) % 60;
                            int minutes = (duration / (1000 * 60)) % 60;
                            textViewEnd.setText(String.format("%d:%02d", minutes, seconds));
                            mp.start();
                            // Start updating the SeekBar and the start time TextView
                            handler.post(updateSeekBarRunnable);
                            // Get the song name
                            String songName = new File(prevSongPath).getName();
                            songName = songName.substring(0, songName.lastIndexOf('.'));
                            textViewSongName.setText(songName);
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "Pause button clicked", Toast.LENGTH_SHORT).show();
                // Implement your logic to pause/resume the song here
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    buttonPause.setText("Resume");
                    // Remove any pending posts of updateSeekBarRunnable that are in the message queue
                    handler.removeCallbacks(updateSeekBarRunnable);
                } else {
                    mediaPlayer.start();
                    buttonPause.setText("Pause");
                    // Post the Runnable again
                    handler.post(updateSeekBarRunnable);
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity2.this, "Next button clicked", Toast.LENGTH_SHORT).show();
                // Implement your logic to play the next song here
                // Increment the current song index
                currentSongIndex++;

                // If the current song index is greater than the last index, set it to the first song
                if (currentSongIndex >= songFilePaths.size()) {
                    currentSongIndex = 0;
                }

                // Stop and release the current MediaPlayer
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
//                    mediaPlayer.release();
                }

                // Create and start a new MediaPlayer with the next song
                mediaPlayer = new MediaPlayer();
                try {
                    String nextSongPath = songFilePaths.get(currentSongIndex);
                    mediaPlayer.setDataSource(nextSongPath);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // ... existing code ...

                            // Get the song name
                            String songName = new File(nextSongPath).getName();
                            songName = songName.substring(0, songName.lastIndexOf('.'));
                            textViewSongName.setText(songName);
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Set an OnSeekBarChangeListener for the SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int duration = mediaPlayer.getDuration();
                    int seekPosition = (int) ((progress / 100.0) * duration);
                    mediaPlayer.seekTo(seekPosition);

                    // Update the start time TextView
                    int seconds = (seekPosition / 1000) % 60;
                    int minutes = (seekPosition / (1000 * 60)) % 60;
                    textViewStart.setText(String.format("%d:%02d", minutes, seconds));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can implement special handling for when the user starts moving the SeekBar thumb here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can implement special handling for when the user stops moving the SeekBar thumb here
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        // Remove any pending posts of updateSeekBarRunnable that are in the message queue
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}