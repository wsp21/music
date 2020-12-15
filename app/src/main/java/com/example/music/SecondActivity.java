package com.example.music;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaExtractor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.example.music.Song_make.formatTime;
import static com.example.music.Song_make.list;

public class SecondActivity  extends AppCompatActivity {
    private Boolean B_loop = true;
    private boolean isSeekBarChanging;
    private int currentPosition;
    private Timer timer;
    private MediaPlayer mediaPlayer;
    private int Num = -1;
    private String tol_time="";
    private String cur_time="";
    private static final String TAG = "SecondActivity";
    Random random = new Random();

    //查询、请求权限
    private  final int REQUEST_EXTERNAL_STORAGE = 1;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    public  void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        //查询、请求权限
        verifyStoragePermissions(this);

        mediaPlayer=new MediaPlayer();

        final ImageButton btn_pause = findViewById(R.id.btn_pause);
        final ImageButton btn_next = findViewById(R.id.btn_next);
        final ImageButton btn_last = findViewById(R.id.btn_last);
        final ImageButton btn_loop = findViewById(R.id.btn_loop);
        final ImageButton btn_random = findViewById(R.id.btn_random);
        final TextView textView = findViewById(R.id.text_name);
        final SeekBar seekBar = findViewById(R.id.seekbar_time);
        final TextView tol_time1=findViewById(R.id.tol_time);
        final TextView cur_time1=findViewById(R.id.cur_time);

        //进度条监听事件
        cur_time1.setText("00:00");
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    if (mediaPlayer != null) {
                        if (!isSeekBarChanging) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            //更新ui线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int cur=mediaPlayer.getCurrentPosition();
                                    int sec=cur/1000;
                                    int min=sec/60;
                                    sec-=min*60;
                                    String cur_time =String.format("%02d:%02d",min,sec);
                                    seekBar.setProgress(cur);
                                    cur_time1.setText(cur_time);
                                }});
                        }
                    } else {
                        seekBar.setProgress(0);
                    }
            }
        }, 0,250);

        //获取歌曲编号
        Intent intent=getIntent();
        Num=intent.getIntExtra("Number",0);
        Log.v(TAG,"number:  "+Num);
        tol_time=intent.getStringExtra("tol_time");
        tol_time1.setText(tol_time);

        textView.setText(list.get(Num).song);

        //播放暂停按钮
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    btn_pause.setImageResource(R.drawable.play);
                } else {
                    String p = list.get(Num).path;//获得歌曲的地址
                    Log.v(TAG,"string path:  "+p);
                    btn_pause.setImageResource(R.drawable.pause);
                    //重新获取进度
                    mediaPlayer.getCurrentPosition();
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(p);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        mediaPlayer.seekTo(currentPosition);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    textView.setText(list.get(Num).song);
                }
            }
        });

        //下一曲
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Num == list.size() - 1) {
                    Num = 0;
                } else {
                    Num++;
                    Log.v(TAG, "Num=" + Num);
                }
                String p = list.get(Num).path;
                play(p);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(list.get(Num).song);
                String s=formatTime(list.get(Num).duration);
                tol_time1.setText(s);
            }
        });


        //上一曲
        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Num == 0) {
                    Num = 0;
                } else {
                    Num--;
                    Log.v(TAG, "Num=" + Num);
                }
                String p = list.get(Num).path;
                play(p);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(list.get(Num).song);
                String s=formatTime(list.get(Num).duration);
                tol_time1.setText(s);
            }
        });

        //循环按钮
        btn_loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (B_loop) {
                    B_loop = false;
                    btn_loop.setImageResource(R.drawable.order);
                    mediaPlayer.setLooping(false);
                } else {
                    B_loop = true;
                    btn_loop.setImageResource(R.drawable.loop);
                    mediaPlayer.setLooping(true);
                }
            }
        });
        //随机按钮
        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x=random.nextInt(list.size());
                String p = list.get(x).path;
                play(p);
                seekBar.setMax(mediaPlayer.getDuration());
                textView.setText(list.get(x).song);
                String s=formatTime(list.get(x).duration);
                tol_time1.setText(s);
            }
        });



    }

    public void play(String path) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void toback(View view) {
        mediaPlayer.pause();
        mediaPlayer.reset();
        Intent intent= new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
    }

    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {
        final TextView cur_time1=findViewById(R.id.cur_time);
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            //当前播放位置
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }
}



