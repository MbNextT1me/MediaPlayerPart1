package com.example.mediaplayerbroadcastreceiver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressBarActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView elapsedTimeTextView;
    private Button playPauseButton;
    private Handler handler;
    private Runnable runnable;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressbar);

        progressBar = findViewById(R.id.progress_bar);
        elapsedTimeTextView = findViewById(R.id.elapsed_time_text_view);
        TextView totalTimeTextView = findViewById(R.id.total_time_text_view);
        playPauseButton = findViewById(R.id.play_pause_button);
        Button backButton = findViewById(R.id.back_button);

        Intent intent = getIntent();
        int mediaPosition = intent.getIntExtra("mediaPosition", 0);
        isPlaying = intent.getBooleanExtra("isPlaying", false);
        int mediaDuration = intent.getIntExtra("mediaDuration", 0);

        totalTimeTextView.setText(formatTime(mediaDuration));
        progressBar.setMax(mediaDuration);

        if (isPlaying) {
            MainActivity.mediaPlayer.start();
            MainActivity.mediaPlayer.seekTo(mediaPosition);
            playPauseButton.setText(R.string.pause);
        } else {
            playPauseButton.setText(R.string.play);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (MainActivity.mediaPlayer != null && MainActivity.mediaPlayer.isPlaying()) {
                    int currentPosition = MainActivity.mediaPlayer.getCurrentPosition();
                    progressBar.setProgress(currentPosition);
                    elapsedTimeTextView.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                MainActivity.mediaPlayer.pause();
                playPauseButton.setText(R.string.play);
            } else {
                MainActivity.mediaPlayer.start();
                playPauseButton.setText(R.string.pause);
            }

            isPlaying = !isPlaying;
        });


        backButton.setOnClickListener(v -> {
            Intent intentBack = new Intent(ProgressBarActivity.this, MainActivity.class);
            intentBack.putExtra("mediaPosition", MainActivity.mediaPlayer.getCurrentPosition());
            intentBack.putExtra("isPlaying", isPlaying);
            startActivity(intentBack);
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
