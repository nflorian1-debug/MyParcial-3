package com.example.myparcial.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myparcial.models.Ingredient;
import com.example.myparcial.models.Recipe;
import com.example.myparcial.models.RecipeCategory;
import com.example.myparcial.models.Step;

import java.util.ArrayList;
import java.util.List;

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recetario.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_INGREDIENTS = "ingredients";
    private static final String TABLE_STEPS = "steps";

    // Recipes columns
    private static final String COL_RECIPE_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_CATEGORY = "category";
    private static final String COL_PHOTO_URI = "photo_uri";
    private static final String COL_SERVINGS = "servings";
    private static final String COL_PREP_TIME = "prep_time";
    private static final String COL_DIFFICULTY = "difficulty";
    private static final String COL_BAKE_REST_TIME = "bake_rest_time";
    private static final String COL_IS_FAVORITE = "is_favorite";

    // Ingredients columns
    private static final String COL_ING_ID = "id";
    private static final String COL_ING_RECIPE_ID = "recipe_id";
    private static final String COL_ING_NAME = "name";
    private static final String COL_ING_QUANTITY = "quantity";
    private static final String COL_ING_UNIT = "unit";

    // Steps columns
    private static final String COL_STEP_ID = "id";
    private static final String COL_STEP_RECIPE_ID = "recipe_id";
    private static final String COL_STEP_NUMBER = "step_number";
    private static final String COL_STEP_INSTRUCTION = "instruction";

    private static RecipeDbHelper instance;

    public static synchronized RecipeDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RecipeDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_PHOTO_URI + " TEXT, " +
                COL_SERVINGS + " INTEGER DEFAULT 2, " +
                COL_PREP_TIME + " INTEGER DEFAULT 0, " +
                COL_DIFFICULTY + " TEXT, " +
                COL_BAKE_REST_TIME + " INTEGER DEFAULT 0, " +
                COL_IS_FAVORITE + " INTEGER DEFAULT 0" +
                ");";

        String createIngredientsTable = "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                COL_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ING_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_ING_NAME + " TEXT NOT NULL, " +
                COL_ING_QUANTITY + " REAL NOT NULL, " +
                COL_ING_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_ING_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE" +
                ");";

        String createStepsTable = "CREATE TABLE " + TABLE_STEPS + " (" +
                COL_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STEP_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_STEP_NUMBER + " INTEGER NOT NULL, " +
                COL_STEP_INSTRUCTION + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_STEP_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(createRecipesTable);
        db.execSQL(createIngredientsTable);
        db.execSQL(createStepsTable);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Recipe 1: Desayuno - Pancakes de Avena y Banano
        ContentValues r1 = new ContentValues();
        r1.put(COL_TITLE, "Pancakes de Avena y Banano");
        r1.put(COL_CATEGORY, RecipeCategory.DESAYUNO.name());
        r1.put(COL_PHOTO_URI, "drawable://sample_desayuno");
        r1.put(COL_SERVINGS, 2);
        r1.put(COL_PREP_TIME, 15);
        r1.put(COL_DIFFICULTY, "Fácil");
        r1.put(COL_BAKE_REST_TIME, 0);
        r1.put(COL_IS_FAVORITE, 1);
        long id1 = db.insert(TABLE_RECIPES, null, r1);

        insertIngredient(db, id1, "Banano maduro", 1, "unidad");
        insertIngredient(db, id1, "Hojuelas de avena", 1, "taza");
        insertIngredient(db, id1, "Huevos", 2, "unidades");
        insertIngredient(db, id1, "Leche de almendras", 0.5, "taza");
        insertIngredient(db, id1, "Polvo de hornear", 1, "cucharadita");
        insertIngredient(db, id1, "Canela en polvo", 0.5, "cucharadita");

        insertStep(db, id1, 1, "Tritura el banano en un tazón hasta hacer un puré suave.");
        insertStep(db, id1, 2, "Agrega los huevos, avena, leche, polvo de hornear y canela. Mezcla bien.");
        insertStep(db, id1, 3, "Calienta un sartén antiadherente a fuego medio y vierte porciones de la mezcla.");
        insertStep(db, id1, 4, "Cocina por 2-3 minutos hasta que salgan burbujas, voltea y dora el otro lado.");

        // Recipe 2: Almuerzo - Pasta Primavera con Vegetales
        ContentValues r2 = new ContentValues();
        r2.put(COL_TITLE, "Pasta Primavera con Vegetales");
        r2.put(COL_CATEGORY, RecipeCategory.ALMUERZO.name());
        r2.put(COL_PHOTO_URI, "drawable://sample_almuerzo");
        r2.put(COL_SERVINGS, 3);
        r2.put(COL_PREP_TIME, 25);
        r2.put(COL_DIFFICULTY, "Fácil");
        r2.put(COL_BAKE_REST_TIME, 0);
        r2.put(COL_IS_FAVORITE, 0);
        long id2 = db.insert(TABLE_RECIPES, null, r2);

        insertIngredient(db, id2, "Pasta (Penne o Fusilli)", 300, "g");
        insertIngredient(db, id2, "Tomates cherry", 150, "g");
        insertIngredient(db, id2, "Brócoli en arbolitos", 1, "taza");
        insertIngredient(db, id2, "Aceite de oliva", 2, "cucharadas");
        insertIngredient(db, id2, "Ajo picado", 2, "dientes");
        insertIngredient(db, id2, "Queso Parmesano", 50, "g");

        insertStep(db, id2, 1, "Cocina la pasta en abundante agua salada según las instrucciones del paquete.");
        insertStep(db, id2, 2, "En una sartén grande, saltea el ajo en aceite de oliva a fuego medio.");
        insertStep(db, id2, 3, "Agrega el brócoli y los tomates cherry salteando por 5-7 minutos.");
        insertStep(db, id2, 4, "Mezcla la pasta escurrida con los vegetales y sirve con queso parmesano rallado.");

        // Recipe 3: Postre - Brownies Fudge de Chocolate
        ContentValues r3 = new ContentValues();
        r3.put(COL_TITLE, "Brownies Fudge de Chocolate");
        r3.put(COL_CATEGORY, RecipeCategory.POSTRE.name());
        r3.put(COL_PHOTO_URI, "drawable://sample_postre");
        r3.put(COL_SERVINGS, 4);
        r3.put(COL_PREP_TIME, 20);
        r3.put(COL_DIFFICULTY, "Media");
        r3.put(COL_BAKE_REST_TIME, 30);
        r3.put(COL_IS_FAVORITE, 1);
        long id3 = db.insert(TABLE_RECIPES, null, r3);

        insertIngredient(db, id3, "Chocolate negro 70%", 150, "g");
        insertIngredient(db, id3, "Mantequilla sin sal", 100, "g");
        insertIngredient(db, id3, "Azúcar morena", 150, "g");
        insertIngredient(db, id3, "Huevos", 2, "unidades");
        insertIngredient(db, id3, "Harina de trigo", 80, "g");
        insertIngredient(db, id3, "Cacao en polvo", 20, "g");

        insertStep(db, id3, 1, "Precalienta el horno a 180°C y enharina un molde cuadrado.");
        insertStep(db, id3, 2, "Derrite el chocolate con la mantequilla a baño María o en microondas.");
        insertStep(db, id3, 3, "Integra el azúcar y los huevos batiendo enérgicamente.");
        insertStep(db, id3, 4, "Añade la harina y el cacao tamizados mezclando suavemente con espátula.");
        insertStep(db, id3, 5, "Hornea durante 25-30 minutos y deja reposar 15 minutos antes de cortar.");
    }

    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ING_RECIPE_ID, recipeId);
        cv.put(COL_ING_NAME, name);
        cv.put(COL_ING_QUANTITY, qty);
        cv.put(COL_ING_UNIT, unit);
        db.insert(TABLE_INGREDIENTS, null, cv);
    }

    private void insertStep(SQLiteDatabase db, long recipeId, int number, String instruction) {
        ContentValues cv = new ContentValues();
        cv.put(COL_STEP_RECIPE_ID, recipeId);
        cv.put(COL_STEP_NUMBER, number);
        cv.put(COL_STEP_INSTRUCTION, instruction);
        db.insert(TABLE_STEPS, null, cv);
    }

    public List<Recipe> getRecipes(String searchQuery, String categoryFilter, boolean favoritesOnly) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder selection = new StringBuilder("1=1");
        List<String> selectionArgs = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            selection.append(" AND ").append(COL_TITLE).append(" LIKE ?");
            selectionArgs.add("%" + searchQuery.trim() + "%");
        }

        if (categoryFilter != null && !categoryFilter.equals("ALL")) {
            selection.append(" AND ").append(COL_CATEGORY).append(" = ?");
            selectionArgs.add(categoryFilter);
        }

        if (favoritesOnly) {
            selection.append(" AND ").append(COL_IS_FAVORITE).append(" = 1");
        }

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null,
                null,
                COL_RECIPE_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Recipe recipe = cursorToRecipe(cursor);
                list.add(recipe);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (cursor != null && cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
            cursor.close();
        }
        if (recipe != null) {
            recipe.setIngredients(getIngredientsForRecipe(id));
            recipe.setSteps(getStepsForRecipe(id));
        }
        return recipe;
    }

    public long saveRecipe(Recipe recipe) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id = recipe.getId();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_TITLE, recipe.getTitle());
            cv.put(COL_CATEGORY, recipe.getCategory().name());
            cv.put(COL_PHOTO_URI, recipe.getPhotoUri());
            cv.put(COL_SERVINGS, recipe.getServings());
            cv.put(COL_PREP_TIME, recipe.getPrepTime());
            cv.put(COL_DIFFICULTY, recipe.getDifficulty());
            cv.put(COL_BAKE_REST_TIME, recipe.getBakeOrRestTime());
            cv.put(COL_IS_FAVORITE, recipe.isFavorite() ? 1 : 0);

            if (id > 0) {
                db.update(TABLE_RECIPES, cv, COL_RECIPE_ID + "=?", new String[]{String.valueOf(id)});
                // delete old ingredients & steps
                db.delete(TABLE_INGREDIENTS, COL_ING_RECIPE_ID + "=?", new String[]{String.valueOf(id)});
                db.delete(TABLE_STEPS, COL_STEP_RECIPE_ID + "=?", new String[]{String.valueOf(id)});
            } else {
                id = db.insert(TABLE_RECIPES, null, cv);
                recipe.setId(id);
            }

            if (recipe.getIngredients() != null) {
                for (Ingredient ing : recipe.getIngredients()) {
                    insertIngredient(db, id, ing.getName(), ing.getQuantity(), ing.getUnit());
                }
            }

            if (recipe.getSteps() != null) {
                int stepNum = 1;
                for (Step step : recipe.getSteps()) {
                    insertStep(db, id, stepNum++, step.getInstruction());
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public void setFavorite(long recipeId, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_RECIPES, cv, COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
    }

    public boolean deleteRecipe(long recipeId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_RECIPES, COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        return rows > 0;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
        recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        recipe.setCategory(RecipeCategory.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))));
        recipe.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO_URI)));
        recipe.setServings(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SERVINGS)));
        recipe.setPrepTime(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PREP_TIME)));
        recipe.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIFFICULTY)));
        recipe.setBakeOrRestTime(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BAKE_REST_TIME)));
        recipe.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1);
        return recipe;
    }

    private List<Ingredient> getIngredientsForRecipe(long recipeId) {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_INGREDIENTS, null, COL_ING_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)}, null, null, COL_ING_ID + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Ingredient ing = new Ingredient();
                ing.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ING_ID)));
                ing.setRecipeId(recipeId);
                ing.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_NAME)));
                ing.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ING_QUANTITY)));
                ing.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_ING_UNIT)));
                list.add(ing);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    private List<Step> getStepsForRecipe(long recipeId) {
        List<Step> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STEPS, null, COL_STEP_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)}, null, null, COL_STEP_NUMBER + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Step step = new Step();
                step.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_STEP_ID)));
                step.setRecipeId(recipeId);
                step.setStepNumber(cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEP_NUMBER)));
                step.setInstruction(cursor.getString(cursor.getColumnIndexOrThrow(COL_STEP_INSTRUCTION)));
                list.add(step);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}