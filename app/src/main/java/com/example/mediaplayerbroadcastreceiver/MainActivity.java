package com.example.mediaplayerbroadcastreceiver;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button playPauseButton;
    protected static MediaPlayer mediaPlayer;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.play_pause_button);
        Button goToProgressBarActivityButton = findViewById(R.id.second_activity_button);

        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean("isPlaying");
        } else {
            isPlaying = false;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            AssetManager assetManager = getAssets();
            AssetFileDescriptor fileDescriptor;
            try {
                String trackName = getString(R.string.track);
                fileDescriptor = assetManager.openFd(trackName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        updatePlayPauseButton();

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            isPlaying = !isPlaying;
            updatePlayPauseButton();
        });

        goToProgressBarActivityButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProgressBarActivity.class);
            intent.putExtra("mediaPosition", mediaPlayer.getCurrentPosition());
            intent.putExtra("isPlaying", mediaPlayer.isPlaying());
            intent.putExtra("mediaDuration", mediaPlayer.getDuration());
            startActivity(intent);
        });

    }

    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setText(R.string.pause);
        } else {
            playPauseButton.setText(R.string.play);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlaying", isPlaying);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayPauseButton();
        if (mediaPlayer.isPlaying()) {
            isPlaying = true;
            updatePlayPauseButton();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updatePlayPauseButton();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}

