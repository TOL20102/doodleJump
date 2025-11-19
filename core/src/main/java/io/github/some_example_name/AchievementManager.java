package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.HashMap;
import java.util.Map;

public class AchievementManager {
    private HashMap<String, Achievement> achievements;
    private Json json;
    private FileHandle saveFile;

    public AchievementManager() {
        this.achievements = new HashMap<>();
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);

        if (Gdx.files.isLocalStorageAvailable()) {
            saveFile = Gdx.files.local("achievements.json");
        } else {
            saveFile = Gdx.files.internal("achievements.json");
        }

        initializeAchievements();
        loadAchievements();
    }

    private void initializeAchievements() {
        addAchievement(new Achievement("welcome", "Welcome!", "First time playing"));
        addAchievement(new Achievement("first_jump", "First Jump", "Make first jump"));
        addAchievement(new Achievement("height_100", "Height 100", "Reach height 100"));
        addAchievement(new Achievement("height_500", "Height 500", "Reach height 500"));
    }

    public void addAchievement(Achievement achievement) {
        achievements.put(achievement.id, achievement);
    }

    public void unlockAchievement(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null) {
            if (!achievement.unlocked) {
                achievement.unlocked = true;
                achievement.show();

                Gdx.app.log("ACHIEVEMENT", "Разблокировано: " + achievement.title);

                saveAchievements();

                try {
                    Gdx.input.vibrate(200);
                } catch (Exception e) {

                }
            }
        }
    }

    public void showAchievement(String id) {
        Achievement achievement = achievements.get(id);
        if (achievement != null && achievement.unlocked) {
            achievement.show();
        }
    }

    public void update(float delta) {
        for (Achievement achievement : achievements.values()) {
            achievement.update(delta);
        }
    }

    public void draw(Batch batch, BitmapFont font) {
        if (font == null) return;

        for (Achievement achievement : achievements.values()) {
            if (achievement.isShowing) {
                achievement.draw(batch, font);
            }
        }
    }

    public void saveAchievements() {
        try {
            AchievementSaveData saveData = new AchievementSaveData();

            for (Achievement achievement : achievements.values()) {
                saveData.unlockedAchievements.put(achievement.id, achievement.unlocked);
            }

            String jsonData = json.toJson(saveData);
            saveFile.writeString(jsonData, false);

        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "ERROR saving achievements: " + e.getMessage());
        }
    }

    public void loadAchievements() {
        try {
            if (saveFile.exists()) {
                String data = saveFile.readString();
                AchievementSaveData saveData = json.fromJson(AchievementSaveData.class, data);

                for (Map.Entry<String, Boolean> entry : saveData.unlockedAchievements.entrySet()) {
                    String achievementId = entry.getKey();
                    Boolean unlocked = entry.getValue();

                    Achievement achievement = achievements.get(achievementId);
                    if (achievement != null) {
                        achievement.unlocked = unlocked;
                    }
                }
            } else {
                saveAchievements();
            }

        } catch (Exception e) {
            Gdx.app.error("AchievementManager", "ERROR loading achievements: " + e.getMessage());
        }
    }

    public boolean isAchievementUnlocked(String id) {
        Achievement achievement = achievements.get(id);
        return achievement != null && achievement.unlocked;
    }

    public static class AchievementSaveData {
        public HashMap<String, Boolean> unlockedAchievements = new HashMap<>();
    }
}
