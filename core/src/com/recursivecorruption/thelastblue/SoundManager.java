package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SoundManager {
    private Music playMusic, beginMusic;

    public SoundManager() {
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("Song.mp3"));
        playMusic.setLooping(true);
        playMusic.setVolume(0f);
        beginMusic = Gdx.audio.newMusic(Gdx.files.internal("SadBg.mp3"));
        beginMusic.setLooping(true);
        beginMusic.setVolume(0f);
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

    void update(GameState state) {
        if (state == GameState.BEGIN) {
            decreaseVolume(playMusic);
            increaseVolume(beginMusic);
        } else {
            decreaseVolume(beginMusic);
            increaseVolume(playMusic);
        }
    }

    void dispose()
    {
        playMusic.dispose();
        beginMusic.dispose();
    }
}
