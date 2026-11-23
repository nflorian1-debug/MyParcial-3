package com.example.myparcial.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.adapters.IngredientFormAdapter;
import com.example.myparcial.db.RecipeDbHelper;
import com.example.myparcial.models.Ingredient;
import com.example.myparcial.models.Recipe;
import com.example.myparcial.models.RecipeCategory;
import com.example.myparcial.models.Step;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecipeFormFragment extends Fragment {

    private RecipeDbHelper dbHelper;
    private long recipeId = -1L;
    private Recipe existingRecipe;
    private String selectedPhotoUriStr = "";

    private TextView txtHeader;
    private ChipGroup chipGroupCategory;

    private MaterialCardView cardSelectPhoto;
    private ImageView imgPreview;

    private EditText edtTitle;
    private EditText edtServings;

    // Dynamic modality views
    private TextInputLayout inputLayoutPrepTime;
    private EditText edtPrepTime;

    private LinearLayout containerDifficulty;
    private ChipGroup chipGroupDifficulty;

    private TextInputLayout inputLayoutBakeTime;
    private EditText edtBakeTime;

    // Ingredients
    private IngredientFormAdapter ingredientAdapter;

    // Steps
    private EditText edtSteps;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register photo picker launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String savedUri = saveImageLocally(requireContext(), uri);
                if (savedUri != null) {
                    selectedPhotoUriStr = savedUri;
                    imgPreview.setImageURI(Uri.parse(savedUri));
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = RecipeDbHelper.getInstance(requireContext());

        if (getArguments() != null) {
            recipeId = getArguments().getLong("recipeId", -1L);
        }

        txtHeader = view.findViewById(R.id.txt_form_header);
        chipGroupCategory = view.findViewById(R.id.chip_group_form_category);

        cardSelectPhoto = view.findViewById(R.id.card_select_photo);
        imgPreview = view.findViewById(R.id.img_form_preview);

        edtTitle = view.findViewById(R.id.edt_form_title);
        edtServings = view.findViewById(R.id.edt_form_servings);

        inputLayoutPrepTime = view.findViewById(R.id.input_layout_prep_time);
        edtPrepTime = view.findViewById(R.id.edt_form_prep_time);

        containerDifficulty = view.findViewById(R.id.container_difficulty);
        chipGroupDifficulty = view.findViewById(R.id.chip_group_difficulty);

        inputLayoutBakeTime = view.findViewById(R.id.input_layout_bake_time);
        edtBakeTime = view.findViewById(R.id.edt_form_bake_time);

        RecyclerView recyclerIngredients = view.findViewById(R.id.recycler_form_ingredients);
        MaterialButton btnAddIngRow = view.findViewById(R.id.btn_add_ingredient_row);

        edtSteps = view.findViewById(R.id.edt_form_steps);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_recipe);

        // Ingredients RecyclerView
        ingredientAdapter = new IngredientFormAdapter(new ArrayList<>());
        recyclerIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerIngredients.setAdapter(ingredientAdapter);

        btnAddIngRow.setOnClickListener(v -> ingredientAdapter.addIngredient());

        // Photo selector click
        cardSelectPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Category Selection Listener for dynamic modality fields
        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            updateModalityFieldsVisibility(id);
        });

        if (recipeId > 0) {
            txtHeader.setText(R.string.title_editar_receta);
            loadExistingRecipe();
        } else {
            txtHeader.setText(R.string.title_nueva_receta);
            // Default: 2 initial empty ingredients
            List<Ingredient> initialIngs = new ArrayList<>();
            initialIngs.add(new Ingredient("", 1.0, "unidad"));
            initialIngs.add(new Ingredient("", 1.0, "unidad"));
            ingredientAdapter.setIngredients(initialIngs);

            updateModalityFieldsVisibility(R.id.chip_form_desayuno);
        }

        btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void updateModalityFieldsVisibility(int checkedChipId) {
        if (checkedChipId == R.id.chip_form_desayuno) {
            inputLayoutPrepTime.setVisibility(View.VISIBLE);
            containerDifficulty.setVisibility(View.GONE);
            inputLayoutBakeTime.setVisibility(View.GONE);
        } else if (checkedChipId == R.id.chip_form_almuerzo) {
            inputLayoutPrepTime.setVisibility(View.GONE);
            containerDifficulty.setVisibility(View.VISIBLE);
            inputLayoutBakeTime.setVisibility(View.GONE);
        } else if (checkedChipId == R.id.chip_form_postre) {
            inputLayoutPrepTime.setVisibility(View.GONE);
            containerDifficulty.setVisibility(View.GONE);
            inputLayoutBakeTime.setVisibility(View.VISIBLE);
        }
    }

    private void loadExistingRecipe() {
        existingRecipe = dbHelper.getRecipeById(recipeId);
        if (existingRecipe == null) return;

        edtTitle.setText(existingRecipe.getTitle());
        edtServings.setText(String.valueOf(existingRecipe.getServings()));
        selectedPhotoUriStr = existingRecipe.getPhotoUri();

        // Image display
        if (selectedPhotoUriStr != null && selectedPhotoUriStr.startsWith("drawable://")) {
            String drawableName = selectedPhotoUriStr.replace("drawable://", "");
            int resId = requireContext().getResources().getIdentifier(drawableName, "drawable", requireContext().getPackageName());
            if (resId != 0) {
                imgPreview.setImageResource(resId);
            }
        } else if (selectedPhotoUriStr != null && !selectedPhotoUriStr.isEmpty()) {
            try {
                imgPreview.setImageURI(Uri.parse(selectedPhotoUriStr));
            } catch (Exception e) {
                imgPreview.setImageResource(R.drawable.placeholder_recipe);
            }
        }

        // Category selection
        RecipeCategory cat = existingRecipe.getCategory();
        if (cat == RecipeCategory.DESAYUNO) {
            chipGroupCategory.check(R.id.chip_form_desayuno);
            updateModalityFieldsVisibility(R.id.chip_form_desayuno);
        } else if (cat == RecipeCategory.ALMUERZO) {
            chipGroupCategory.check(R.id.chip_form_almuerzo);
            updateModalityFieldsVisibility(R.id.chip_form_almuerzo);
        } else if (cat == RecipeCategory.POSTRE) {
            chipGroupCategory.check(R.id.chip_form_postre);
            updateModalityFieldsVisibility(R.id.chip_form_postre);
        }

        edtPrepTime.setText(String.valueOf(existingRecipe.getPrepTime()));
        edtBakeTime.setText(String.valueOf(existingRecipe.getBakeOrRestTime()));

        String dif = existingRecipe.getDifficulty();
        if ("Fácil".equalsIgnoreCase(dif)) {
            chipGroupDifficulty.check(R.id.chip_dif_facil);
        } else if ("Difícil".equalsIgnoreCase(dif)) {
            chipGroupDifficulty.check(R.id.chip_dif_dificil);
        } else {
            chipGroupDifficulty.check(R.id.chip_dif_media);
        }

        // Ingredients
        if (existingRecipe.getIngredients() != null && !existingRecipe.getIngredients().isEmpty()) {
            ingredientAdapter.setIngredients(new ArrayList<>(existingRecipe.getIngredients()));
        }

        // Steps
        if (existingRecipe.getSteps() != null && !existingRecipe.getSteps().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < existingRecipe.getSteps().size(); i++) {
                sb.append(existingRecipe.getSteps().get(i).getInstruction());
                if (i < existingRecipe.getSteps().size() - 1) {
                    sb.append("\n");
                }
            }
            edtSteps.setText(sb.toString());
        }
    }

    private void saveRecipe() {
        String title = edtTitle.getText() != null ? edtTitle.getText().toString().trim() : "";
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_error_titulo, Toast.LENGTH_SHORT).show();
            return;
        }

        int servings = 2;
        try {
            servings = Integer.parseInt(edtServings.getText().toString().trim());
        } catch (Exception ignored) {}

        // Selected category
        RecipeCategory category = RecipeCategory.DESAYUNO;
        int checkedChip = chipGroupCategory.getCheckedChipId();
        if (checkedChip == R.id.chip_form_almuerzo) {
            category = RecipeCategory.ALMUERZO;
        } else if (checkedChip == R.id.chip_form_postre) {
            category = RecipeCategory.POSTRE;
        }

        int prepTime = 15;
        try {
            prepTime = Integer.parseInt(edtPrepTime.getText().toString().trim());
        } catch (Exception ignored) {}

        int bakeTime = 30;
        try {
            bakeTime = Integer.parseInt(edtBakeTime.getText().toString().trim());
        } catch (Exception ignored) {}

        String difficulty = "Media";
        int checkedDifChip = chipGroupDifficulty.getCheckedChipId();
        if (checkedDifChip == R.id.chip_dif_facil) {
            difficulty = "Fácil";
        } else if (checkedDifChip == R.id.chip_dif_dificil) {
            difficulty = "Difícil";
        }

        // Filter valid non-empty ingredients
        List<Ingredient> rawIngs = ingredientAdapter.getIngredients();
        List<Ingredient> validIngs = new ArrayList<>();
        for (Ingredient ing : rawIngs) {
            if (ing.getName() != null && !ing.getName().trim().isEmpty()) {
                validIngs.add(ing);
            }
        }

        if (validIngs.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_error_ingredientes, Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse steps
        String rawStepsStr = edtSteps.getText() != null ? edtSteps.getText().toString().trim() : "";
        List<Step> stepList = new ArrayList<>();
        if (!rawStepsStr.isEmpty()) {
            String[] lines = rawStepsStr.split("\n");
            int stepNum = 1;
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    stepList.add(new Step(stepNum++, line.trim()));
                }
            }
        }

        Recipe recipe = existingRecipe != null ? existingRecipe : new Recipe();
        recipe.setTitle(title);
        recipe.setCategory(category);
        recipe.setPhotoUri(selectedPhotoUriStr);
        recipe.setServings(servings);
        recipe.setPrepTime(prepTime);
        recipe.setBakeOrRestTime(bakeTime);
        recipe.setDifficulty(difficulty);
        recipe.setIngredients(validIngs);
        recipe.setSteps(stepList);

        dbHelper.saveRecipe(recipe);

        Toast.makeText(requireContext(), R.string.msg_receta_guardada, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).popBackStack();
    }

    private String saveImageLocally(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File file = new File(context.getFilesDir(), "recipe_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 4];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return Uri.fromFile(file).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return uri.toString();
        }
    }
}