package android.example.com.new_project_1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import static android.example.com.new_project_1.MainActivity.*;

public class skipPreviousReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(IsRnd == 1) {
            Random ran = new Random();
            order = (order + ran.nextInt(ArrayMusic.size()))%ArrayMusic.size();
        }
        order = order - 1;
        if(order < 0) {
            order = order + ArrayMusic.size();
        }
        //Toast.makeText(MainActivity.this, order, Toast.LENGTH_LONG).show();
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.release();

        name = ArrayMusic.get(order).getName();
        Uri uri = Uri.parse(ArrayMusic.get(order).toString());
        tSong.setText(name);
        if(bPlay.getText().toString().equals("play")) {
            bPlay.setText("pause");
        }


        mMediaPlayer = MediaPlayer.create(context, uri);

        // music seekbar max time setting
        MainActivity.seekBar.setMax(mMediaPlayer.getDuration() / 1000);
        maximumDuration = mMediaPlayer.getDuration() / 1000;

        mMediaPlayer.start();



        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(IsLoop == 1) {
                    order--;
                } else if(IsRnd == 1) {
                    Random ran = new Random();
                    order = (order + ran.nextInt(ArrayMusic.size()))%ArrayMusic.size();
                }
                if(IsRnd == 1) {
                    Random ran = new Random();
                    order = (order + ran.nextInt(ArrayMusic.size()))%ArrayMusic.size();
                }
                order = order + 1;
                if(order == ArrayMusic.size()) {
                    order = order - ArrayMusic.size();
                }

                mMediaPlayer.pause();
                mMediaPlayer.stop();
                mMediaPlayer.release();

                name = ArrayMusic.get(order).getName();
                Uri uri = Uri.parse(ArrayMusic.get(order).toString());
                tSong.setText(name);
                if(bPlay.getText().toString().equals("play")) {
                    bPlay.setText("pause");
                }


                mMediaPlayer = MediaPlayer.create(context, uri);

                // music seekbar max time setting
                seekBar.setMax(mMediaPlayer.getDuration() / 1000);
                maximumDuration = mMediaPlayer.getDuration() / 1000;

                mMediaPlayer.start();
            }
        });

        MainActivity.expandedView.setTextViewText(R.id.text_song_view, MainActivity.tSong.getText());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, MainActivity.notification);
        
        Log.i("test btn", "previous in seperate java file");
        Toast.makeText(context, "skip previous", Toast.LENGTH_SHORT).show();
    }

}