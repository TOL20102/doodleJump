package io.github.some_example_name.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class ScoreManager {
    private static final String SCORE_FILE = "scores.json";
    private ScoreData scoreData;
    private Json json;

    public ScoreManager() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
        loadScores();
    }

    public static class ScoreData {
        public int highScore = 0;
        public int lastScore = 0;
        public int minScore = 0;
        public int totalGames = 0;
    }

    public void saveScore(int score) {
        scoreData.lastScore = score;

        if (score > scoreData.highScore) {
            scoreData.highScore = score;
        }

        if (scoreData.totalGames == 0 || score < scoreData.minScore) {
            scoreData.minScore = score;
        }

        scoreData.totalGames++;

        try {
            FileHandle file = Gdx.files.local(SCORE_FILE);
            String data = json.toJson(scoreData);
            file.writeString(data, false);
            System.out.println("Score saved: " + score);
        } catch (Exception e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public void loadScores() {
        try {
            FileHandle file = Gdx.files.local(SCORE_FILE);
            if (file.exists()) {
                String data = file.readString();
                scoreData = json.fromJson(ScoreData.class, data);
                System.out.println("Scores loaded: High=" + scoreData.highScore + ", Last=" + scoreData.lastScore);
            } else {
                scoreData = new ScoreData();
                saveScore(0); // Create initial file
            }
        } catch (Exception e) {
            System.err.println("Error loading scores: " + e.getMessage());
            scoreData = new ScoreData();
        }
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public int getHighScore() {
        return scoreData.highScore;
    }

    public int getLastScore() {
        return scoreData.lastScore;
    }

    public int getMinScore() {
        return scoreData.minScore;
    }

    public int getTotalGames() {
        return scoreData.totalGames;
    }
}
