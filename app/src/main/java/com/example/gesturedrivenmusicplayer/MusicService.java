package com.example.gesturedrivenmusicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<song> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void playSong(){
        player.reset();
        //get song
        song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void onCreate(){
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        songPosn++;
        if(songPosn>=songs.size()) songPosn=0;
        playSong();
    }


}
