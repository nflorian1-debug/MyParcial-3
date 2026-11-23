package com.example.myparcial.models;

public enum RecipeCategory {
    DESAYUNO("Desayuno", "Desayunos"),
    ALMUERZO("Almuerzo", "Almuerzos"),
    POSTRE("Postre", "Postres");

    private final String singleName;
    private final String pluralName;

    RecipeCategory(String singleName, String pluralName) {
        this.singleName = singleName;
        this.pluralName = pluralName;
    }

    public String getSingleName() {
        return singleName;
    }

    public String getPluralName() {
        return pluralName;
    }

    public static RecipeCategory fromString(String name) {
        if (name == null) return DESAYUNO;
        for (RecipeCategory cat : RecipeCategory.values()) {
            if (cat.name().equalsIgnoreCase(name) ||
                cat.getSingleName().equalsIgnoreCase(name) ||
                cat.getPluralName().equalsIgnoreCase(name)) {
                return cat;
            }
        }
        return DESAYUNO;
    }
}