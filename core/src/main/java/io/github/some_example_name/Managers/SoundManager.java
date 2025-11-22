package io.github.some_example_name.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import io.github.some_example_name.Static.GameResources;

public class SoundManager {
    private Sound jumpSound;
    private Sound deathSound;

    private boolean soundsEnabled = true;
    private float volume = 0.7f;

    public SoundManager() {
        loadSounds(); /*
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(GameResources.BACKGROUND_MUSIC_PATH));
        shootSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.SHOOT_SOUND_PATH));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DESTROY_SOUND_PATH));
        */

        //backgroundMusic.setVolume(0.2f);
        //backgroundMusic.setLooping(true);

        //updateSoundFlag();
        //updateMusicFlag();
    }
    /*
    public Music backgroundMusic;
    public Sound shootSound;
    public Sound explosionSound;

    public boolean isSoundOn;
    public boolean isMusicOn;

    public void updateMusicFlag() {
        isMusicOn = ScoreManager.loadIsMusicOn();
        if (isMusicOn) backgroundMusic.play();
        else backgroundMusic.stop();
    }
    public void updateSoundFlag() {
        isSoundOn = ScoreManager.loadIsSoundOn();
    }
    */

    private void loadSounds() {
        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.JUMP_SOUND_PATH));
            deathSound = Gdx.audio.newSound(Gdx.files.internal(GameResources.DEATH_SOUND_PATH));
            System.out.println("✅ Sounds loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading sounds: " + e.getMessage());
            soundsEnabled = false;
        }
    }

    public void playJumpSound() {
        if (soundsEnabled && jumpSound != null) {
            jumpSound.play(volume);
        }
    }

    public void playDeathSound() {
        if (soundsEnabled && deathSound != null) {
            deathSound.play(volume);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }

    public void setSoundsEnabled(boolean enabled) {
        this.soundsEnabled = enabled;
    }

    public void dispose() {
        if (jumpSound != null) {
            jumpSound.dispose();
        }
        if (deathSound != null) {
            deathSound.dispose();
        }
    }
}
