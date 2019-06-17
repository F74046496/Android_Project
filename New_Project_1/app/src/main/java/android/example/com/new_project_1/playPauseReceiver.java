package android.example.com.new_project_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


import android.example.com.new_project_1.*;

public class playPauseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ////play
        if(MainActivity.mMediaPlayer.isPlaying()) {
            MainActivity.mMediaPlayer.pause();
            MainActivity.bPlay.setText("play");
            MainActivity.myImgView.setImageResource(R.drawable.ic_play_circle_filled);
        }
        else {
            MainActivity.mMediaPlayer.start();
            MainActivity.bPlay.setText("pause");
            MainActivity.myImgView.setImageResource(R.drawable.ic_pause_black_24dp);
        }
        ////

        if (MainActivity.playPause_state.equals("play")) {
            MainActivity.collapsedView.setImageViewResource(R.id.image_pause_collapsed, R.drawable.ic_play_arrow_black_24dp);
            MainActivity.expandedView.setImageViewResource(R.id.image_pause_expanded, R.drawable.ic_play_arrow_black_24dp);
            MainActivity.playPause_state = "pause";
        }
        else {
            MainActivity.collapsedView.setImageViewResource(R.id.image_pause_collapsed, R.drawable.ic_pause_black_36dp);
            MainActivity.expandedView.setImageViewResource(R.id.image_pause_expanded, R.drawable.ic_pause_black_36dp);
            MainActivity.playPause_state = "play";
        }

        MainActivity.expandedView.setTextViewText(R.id.text_song_view, MainActivity.tSong.getText());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, MainActivity.notification);



        Log.i("test btn", "play!!");
        Toast.makeText(context, "Play-Pause Song", Toast.LENGTH_SHORT).show();

    }

}