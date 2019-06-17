package android.example.com.new_project_1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
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


public class MainActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;


    private RelativeLayout mRelative;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent speechRecognizerIntent;

    private String speechInput;

    public static TextView tSong;
    public static TextView tSong_notify;
    private ImageView iCover;
    private Button bPrevious;
    public static Button bPlay;
    public static ImageView myImgView;
    public static ImageView myImgView_forchange;
    public static ImageView background_forchange;
    private Button bNext;
    private ImageView bMode;


    public static MediaPlayer mMediaPlayer;

    public static int order;
    public static ArrayList<File> ArrayMusic;
    public static String name;

    // use for music seekbar
    public static SeekBar seekBar;
    private TextView musicText;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    public static int maximumDuration;
    public static int IsLoop = 0;
    public static int IsRnd = 0;
    private int whichMode = 0;
    ArrayList<Integer> array_image = new ArrayList<Integer>();

    public static RemoteViews collapsedView;
    public static RemoteViews expandedView;
    public static Notification notification;
    public static String playPause_state;
    public static final String CHANNEL_ID = "exampleChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // music seekbar setting
        seekBar = findViewById(R.id.seekBar);
        musicText = findViewById(R.id.musicTimeText);


        //image
        array_image.add(R.drawable.a01);
        array_image.add(R.drawable.a02);
        array_image.add(R.drawable.a03);
        array_image.add(R.drawable.a04);
        array_image.add(R.drawable.a05);
        array_image.add(R.drawable.a06);
        array_image.add(R.drawable.a07);
        array_image.add(R.drawable.a08);
        array_image.add(R.drawable.a09);
        array_image.add(R.drawable.a11);
        array_image.add(R.drawable.a12);
        array_image.add(R.drawable.a13);
        array_image.add(R.drawable.a14);
        array_image.add(R.drawable.a15);
        array_image.add(R.drawable.a16);
        array_image.add(R.drawable.a17);
        array_image.add(R.drawable.a18);
        array_image.add(R.drawable.a19);
        array_image.add(R.drawable.a20);
        array_image.add(R.drawable.a21);
        array_image.add(R.drawable.a22);
        //////////////////////////////////////////////////////////////

        createNotificationChannel();

        notificationManager = NotificationManagerCompat.from(this);

        collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);
        expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);

        myImgView= (ImageView) findViewById(R.id.play_pause);
        background_forchange = (ImageView) findViewById(R.id.fullbackground);
        myImgView_forchange = (ImageView) findViewById(R.id.albumCover);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mMediaPlayer != null && b){
                    mMediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    bPlay.setText("play");
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    mMediaPlayer.start();
                    bPlay.setText("pause");
                }
            }
        });

        MainActivity.this.runOnUiThread(mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    int minutes = mCurrentPosition / 60;
                    int seconds = mCurrentPosition % 60;
                    int mmaxi = maximumDuration/60;
                    int smaxi = maximumDuration%60;
                    String time2;
                    String time;
                    if(seconds < 10)
                        time = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
                    else
                        time = String.valueOf(minutes) + ":" + String.valueOf(seconds);
                    if(smaxi < 10)
                        time2 = String.valueOf(mmaxi) + ":0" + String.valueOf(smaxi);
                    else
                        time2 = String.valueOf(mmaxi) + ":" + String.valueOf(smaxi);
                    musicText.setText(time + " / " + time2);
                }
                mHandler.postDelayed(this, 1000);
            }
        });



        mRelative = findViewById(R.id.parentLayout);

        tSong = findViewById(R.id.song);

        iCover = findViewById(R.id.albumCover);
        bPrevious = findViewById(R.id.btn_previous);
        bPlay = findViewById(R.id.btn_play);
        bNext = findViewById(R.id.btn_next);
        bMode = findViewById(R.id.btn_mode);

        bPrevious.setVisibility(View.GONE);
        bPlay.setVisibility(View.GONE);
        bNext.setVisibility(View.GONE); //  needless

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        ArrayMusic = (ArrayList) bundle.getParcelableArrayList("song_music");
        order = bundle.getInt("song_order");

        changebackground();


        readMusicAndSetMediaPlayer();
        tSong.setSelected(true);


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(MainActivity.this, "begin listening", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(whichMode == 1) {
                    if(result != null) {
                        speechInput = result.get(0);

                        Toast.makeText(MainActivity.this, speechInput, Toast.LENGTH_LONG).show();

                        if((speechInput.equals("暫停") || speechInput.equals("暫 停"))&& bPlay.getText().toString().equals("pause")) {
                            playAndPause();
                            //Toast.makeText(MainActivity.this, "暫停", Toast.LENGTH_LONG).show();
                        }
                        else if ((speechInput.equals("播放") || speechInput.equals("播 放"))&& bPlay.getText().toString().equals("play")) {
                            playAndPause();
                        }
                        else if (speechInput.equals("上一首") || speechInput.equals("上 一 首")) {
                            playThePrevious();
                        }
                        else if (speechInput.equals("下一首") || speechInput.equals("下 一 首")) {
                            playTheNext();
                        }
                        else if (speechInput.equals("隨機") || speechInput.equals("隨 機")) {
                            playTheRnd();
                        }
                        else if (speechInput.equals("循環") || speechInput.equals("循 環") || speechInput.equals("重複") || speechInput.equals("重 複")) {
                            playTheLoop();
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        mRelative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(speechRecognizerIntent);
                        speechInput = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        break;
                }

                return false;
            }
        });
    }


    public void modeChange(View view) {
        if(whichMode == 0) {
            whichMode = 1;
            bPrevious.setVisibility(View.GONE);
            bPlay.setVisibility(View.GONE);
            bNext.setVisibility(View.GONE);

            ImageView myImgView = (ImageView) findViewById(R.id.btn_mode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_black_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_black_40dp));
            }

        }
        else if(whichMode == 1) {
            whichMode = 0;
            /*bPrevious.setVisibility(View.VISIBLE);
            bPlay.setVisibility(View.VISIBLE);
            bNext.setVisibility(View.VISIBLE);*/
            bPrevious.setVisibility(View.GONE);
            bPlay.setVisibility(View.GONE);
            bNext.setVisibility(View.GONE);

            ImageView myImgView = (ImageView) findViewById(R.id.btn_mode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_off_black_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_off_black_40dp));
            }
        }
    }

    public void playPauseClick(View view) {
       playAndPause();
    }

    public void playPreviousClick(View view) {
       playThePrevious();
    }

    public void playNextClick(View view) {
        playTheNext();
    }

    public void playLoopClick(View view) {
        playTheLoop();
    }

    public void playRndclick(View view) {
        playTheRnd();
    }

    private void playAndPause() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            bPlay.setText("play");

            //ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled));
            }
        }
        else {
            mMediaPlayer.start();
            bPlay.setText("pause");


            //ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            }


            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playTheNext();
                }
            });
        }
    }

    private void playThePrevious() {
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

        ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp, getApplicationContext().getTheme()));
        } else {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        }

        changebackground();

        readMusicAndSetMediaPlayer();
    }

    private void playTheNext() {
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

        ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp, getApplicationContext().getTheme()));
        } else {
            myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        }

        changebackground();

        readMusicAndSetMediaPlayer();
    }

    private void playTheLoop() {
        IsLoop = (IsLoop+1)%2;
        if(IsLoop == 1) {
            Toast.makeText(MainActivity.this, "單曲重複", Toast.LENGTH_SHORT).show();

            ImageView myImgView = (ImageView) findViewById(R.id.id_repeat);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_green_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_green_40dp));
            }

        } else {
            Toast.makeText(MainActivity.this, "重複撥放關閉", Toast.LENGTH_SHORT).show();

            ImageView myImgView = (ImageView) findViewById(R.id.id_repeat);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_white_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_white_40dp));
            }

        }
    }

    private void playTheRnd() {
        IsRnd = (IsRnd+1)%2;
        if(IsRnd == 1) {
            Toast.makeText(MainActivity.this, "隨機撥放開起", Toast.LENGTH_SHORT).show();
            ImageView myImgView = (ImageView) findViewById(R.id.id_shuffle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_green_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_green_40dp));
            }
        } else {
            Toast.makeText(MainActivity.this, "隨機撥放關閉", Toast.LENGTH_SHORT).show();
            ImageView myImgView = (ImageView) findViewById(R.id.id_shuffle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_white_40dp, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_white_40dp));
            }
        }
    }

    private void readMusicAndSetMediaPlayer() {
        name = ArrayMusic.get(order).getName();
        Uri uri = Uri.parse(ArrayMusic.get(order).toString());
        tSong.setText(name);
        if(bPlay.getText().toString().equals("play")) {
            bPlay.setText("pause");
        }


        mMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        // music seekbar max time setting
        seekBar.setMax(mMediaPlayer.getDuration() / 1000);
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
                playTheNext();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mHandler.removeCallbacks(mRunnable);


        super.onDestroy();

    }

    public void changebackground() {
        //ImageView myImgView_forchange = (ImageView) findViewById(R.id.albumCover);
        //ImageView background_forchange = (ImageView) findViewById(R.id.fullbackground);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImgView_forchange.setImageDrawable(getResources().getDrawable(array_image.get(order%22), getApplicationContext().getTheme()));
            background_forchange.setImageDrawable(getResources().getDrawable(array_image.get(order%22), getApplicationContext().getTheme()));
        } else {
            myImgView_forchange.setImageDrawable(getResources().getDrawable(array_image.get(order%22)));
            background_forchange.setImageDrawable(getResources().getDrawable(array_image.get(order%22)));
        }
    }

    public void justfortest(View view) {
        Toast.makeText(MainActivity.this, "begin listening", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }*/
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Example Channel",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        showNotification();
    }

    public void showNotification() {


        Intent clickIntent = new Intent(this, NotificationReceiver.class);
        Intent skipPreviousIntent = new Intent(this, skipPreviousReceiver.class);
        Intent playPauseIntent = new Intent(this, playPauseReceiver.class);
        Intent skipNextIntent = new Intent(this, skipNextReceiver.class);

        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this, 0, clickIntent, 0);
        PendingIntent pendingSkipPreviousIntent = PendingIntent.getBroadcast(this, 0, skipPreviousIntent, 0);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, 0);
        PendingIntent pendingSkipNextIntent = PendingIntent.getBroadcast(this, 0, skipNextIntent, 0);

        //collapsedView
        collapsedView.setImageViewResource(R.id.image_cover_collapsed, R.drawable.ic_cover);
        collapsedView.setImageViewResource(R.id.image_previous_collapsed, R.drawable.ic_skip_previous_black_24dp);
        collapsedView.setImageViewResource(R.id.image_pause_collapsed, R.drawable.ic_pause_black_36dp);
        collapsedView.setImageViewResource(R.id.image_next_collapsed, R.drawable.ic_skip_next_black_24dp);
        collapsedView.setImageViewResource(R.id.image_close, R.drawable.ic_close_black_24dp);

        collapsedView.setOnClickPendingIntent(R.id.image_previous_collapsed, pendingSkipPreviousIntent);
        collapsedView.setOnClickPendingIntent(R.id.image_pause_collapsed, pendingPlayPauseIntent);
        collapsedView.setOnClickPendingIntent(R.id.image_next_collapsed, pendingSkipNextIntent);
        collapsedView.setOnClickPendingIntent(R.id.image_close, clickPendingIntent);

        //expandedView
        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.ic_cover);
        expandedView.setImageViewResource(R.id.image_previous_expanded, R.drawable.ic_skip_previous_black_24dp);
        expandedView.setImageViewResource(R.id.image_pause_expanded, R.drawable.ic_pause_black_36dp);
        expandedView.setImageViewResource(R.id.image_next_expanded, R.drawable.ic_skip_next_black_24dp);
        expandedView.setTextViewText(R.id.text_song_view, tSong.getText());
        //expandedView.setImageViewResource(R.id.image_close_expanded, R.drawable.ic_close_black_24dp);

        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);
        expandedView.setOnClickPendingIntent(R.id.image_previous_expanded, pendingSkipPreviousIntent);
        expandedView.setOnClickPendingIntent(R.id.image_pause_expanded, pendingPlayPauseIntent);
        expandedView.setOnClickPendingIntent(R.id.image_next_expanded, pendingSkipNextIntent);


        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(1, notification);
        //tSong_notify = (textview)
        //tSong_notify.setSelected(true);
        playPause_state = "play";
    }

}
