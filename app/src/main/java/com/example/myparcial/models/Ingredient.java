package com.example.myparcial.models;

public class Ingredient {
    private long id;
    private long recipeId;
    private String name;
    private double quantity;
    private String unit;

    public Ingredient() {
        this.name = "";
        this.quantity = 1.0;
        this.unit = "unidad";
    }

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Ingredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Calculates quantity according to servings factor.
     */
    public double getRecalculatedQuantity(int baseServings, int targetServings) {
        if (baseServings <= 0) return quantity;
        return (quantity * targetServings) / (double) baseServings;
    }

    /**
     * Formats the quantity value nicely (e.g., 1.5 instead of 1.5000, 2 instead of 2.0).
     */
    public static String formatQuantity(double qty) {
        if (qty == (long) qty) {
            return String.format(java.util.Locale.getDefault(), "%d", (long) qty);
        } else {
            return String.format(java.util.Locale.getDefault(), "%.2f", qty).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
    }
}