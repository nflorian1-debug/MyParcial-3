package com.example.myparcial.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id;
    private String title;
    private RecipeCategory category;
    private String photoUri;
    private int servings;
    private int prepTime; // in minutes (for DESAYUNO)
    private String difficulty; // Fácil, Media, Difícil (for ALMUERZO)
    private int bakeOrRestTime; // in minutes (for POSTRE)
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private boolean isFavorite;

    public Recipe() {
        this.title = "";
        this.category = RecipeCategory.DESAYUNO;
        this.photoUri = "";
        this.servings = 2;
        this.prepTime = 15;
        this.difficulty = "Fácil";
        this.bakeOrRestTime = 20;
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.isFavorite = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public int getServings() {
        return servings <= 0 ? 1 : servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public String getDifficulty() {
        return difficulty == null || difficulty.trim().isEmpty() ? "Media" : difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getBakeOrRestTime() {
        return bakeOrRestTime;
    }

    public void setBakeOrRestTime(int bakeOrRestTime) {
        this.bakeOrRestTime = bakeOrRestTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /**
     * Helper to get formatted modality info string.
     */
    public String getModalityInfoText() {
        if (category == null) return "";
        switch (category) {
            case DESAYUNO:
                return prepTime + " min de prep.";
            case ALMUERZO:
                return "Dificultad: " + getDifficulty();
            case POSTRE:
                return bakeOrRestTime + " min horneado/reposo";
            default:
                return "";
        }
    }
}