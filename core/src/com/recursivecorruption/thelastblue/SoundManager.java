package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.EnumMap;

public class SoundManager {
    private Music playMusic, beginMusic;
    private Music current, previous;
    private EnumMap<GameState, Music> stateMap = new EnumMap<GameState, Music>(GameState.class);

    public SoundManager() {
        previous = playMusic = Gdx.audio.newMusic(Gdx.files.internal("Song.mp3"));
        playMusic.setLooping(true);
        playMusic.setVolume(0f);
        current = beginMusic = Gdx.audio.newMusic(Gdx.files.internal("SadBg.mp3"));
        beginMusic.setLooping(true);
        beginMusic.setVolume(0f);
        stateMap.put(GameState.MENU, beginMusic);
        stateMap.put(GameState.PLAY, playMusic);
        stateMap.put(GameState.INSTRUCTIONS, beginMusic);
    }

    private static void increaseVolume(Music music) {
        if (music.getVolume() < 0.999f) {
            if (!music.isPlaying())
                music.play();
            music.setVolume(music.getVolume() + 0.01f);
        }
    }

    private static void decreaseVolume(Music music) {
        if (music.getVolume() > 0.001f)
            music.setVolume(music.getVolume() - 0.01f);
        else if (music.isPlaying())
            music.stop();
    }

    void onNewState(GameState state) {
        previous = current;
        current = stateMap.get(state);
    }

    void update() {
        decreaseVolume(previous);
        increaseVolume(current);
    }

    void dispose() {
        playMusic.dispose();
        beginMusic.dispose();
    }
}
