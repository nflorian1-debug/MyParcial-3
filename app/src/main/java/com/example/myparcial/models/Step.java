package com.example.myparcial.models;

public class Step {
    private long id;
    private long recipeId;
    private int stepNumber;
    private String instruction;

    public Step() {
        this.instruction = "";
    }

    public Step(int stepNumber, String instruction) {
        this.stepNumber = stepNumber;
        this.instruction = instruction;
    }

    public Step(long id, long recipeId, int stepNumber, String instruction) {
        this.id = id;
        this.recipeId = recipeId;
        this.stepNumber = stepNumber;
        this.instruction = instruction;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}