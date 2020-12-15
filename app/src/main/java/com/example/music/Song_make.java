package com.example.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Song_make {
    public static List<Song> list;
    public static Song song;
    //从多媒体数据库中获取歌曲
    public static List<Song> getmusic(Context context){

        list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null, "", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Log.v("TAG","getmusic:  ");

        if(cursor != null){
            while (cursor.moveToNext()){
                song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                Log.v("TAG","song name:  "+song.song);
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Log.v("TAG","song path:  "+song.path);
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Log.v("TAG","song duration:  "+song.duration);
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                Log.v("TAG","song size:  "+song.size);
                list.add(song);
            }
        }
        Log.v("TAG","------------");
        cursor.close();
        return list;
    }

    //    转换歌曲时间的格式
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return tt;
        } else {
            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
            return tt;
        }
    }
}
