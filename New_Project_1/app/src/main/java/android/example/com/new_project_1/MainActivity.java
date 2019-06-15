package android.example.com.new_project_1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRelative;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent speechRecognizerIntent;

    private String speechInput;

    private TextView tSong;
    private ImageView iCover;
    private Button bPrevious;
    private Button bPlay;
    private Button bNext;
    private ImageView bMode;

    private MediaPlayer mMediaPlayer;

    private int order;
    private ArrayList<File> ArrayMusic;
    private String name;

    // use for music seekbar
    private SeekBar seekBar;
    private TextView musicText;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private int maximumDuration;
    private int IsLoop = 0;
    private int IsRnd = 0;
    private int whichMode = 0;
    ArrayList<Integer> array_image = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // music seekbar setting
        seekBar = findViewById(R.id.seekBar);
        musicText = findViewById(R.id.musicTimeText);


        //image
        array_image.add(R.drawable.forever);
        array_image.add(R.drawable.reset);
        array_image.add(R.drawable.love);
        array_image.add(R.drawable.ref_rain);
        array_image.add(R.drawable.kanon);
        //////////////////////////////////////////////////////////////


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

            ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled, getApplicationContext().getTheme()));
            } else {
                myImgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled));
            }
        }
        else {
            mMediaPlayer.start();
            bPlay.setText("pause");


            ImageView myImgView = (ImageView) findViewById(R.id.play_pause);

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
        ImageView myImgView = (ImageView) findViewById(R.id.albumCover);
        ImageView background = (ImageView) findViewById(R.id.fullbackground);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImgView.setImageDrawable(getResources().getDrawable(array_image.get(order%5), getApplicationContext().getTheme()));
            background.setImageDrawable(getResources().getDrawable(array_image.get(order%5), getApplicationContext().getTheme()));
        } else {
            myImgView.setImageDrawable(getResources().getDrawable(array_image.get(order%5)));
            background.setImageDrawable(getResources().getDrawable(array_image.get(order%5)));
        }
    }

    public void justfortest(View view) {
        Toast.makeText(MainActivity.this, "begin listening", Toast.LENGTH_LONG).show();
    }
}
