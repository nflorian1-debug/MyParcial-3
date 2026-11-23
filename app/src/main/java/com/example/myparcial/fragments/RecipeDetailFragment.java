package com.example.myparcial.fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.adapters.IngredientDetailAdapter;
import com.example.myparcial.adapters.StepDetailAdapter;
import com.example.myparcial.db.RecipeDbHelper;
import com.example.myparcial.models.Recipe;
import com.example.myparcial.models.RecipeCategory;
import com.google.android.material.card.MaterialCardView;

public class RecipeDetailFragment extends Fragment {

    private RecipeDbHelper dbHelper;
    private Recipe currentRecipe;
    private long recipeId = -1L;
    private int currentServings = 2;

    private ImageView imgPhoto;
    private ImageButton btnFavorite;
    private LinearLayout badgeCategory;
    private TextView txtCategory;
    private TextView txtTitle;

    private MaterialCardView cardModalityHighlight;
    private ImageView imgModalityIcon;
    private TextView txtModalityLabel;
    private TextView txtModalityValue;

    private TextView txtServingsCount;
    private ImageButton btnServingsMinus;
    private ImageButton btnServingsPlus;

    private RecyclerView recyclerIngredients;
    private RecyclerView recyclerSteps;

    private IngredientDetailAdapter ingredientAdapter;
    private StepDetailAdapter stepAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = RecipeDbHelper.getInstance(requireContext());

        if (getArguments() != null) {
            recipeId = getArguments().getLong("recipeId", -1L);
        }

        imgPhoto = view.findViewById(R.id.img_detail_photo);
        btnFavorite = view.findViewById(R.id.btn_detail_favorite);
        badgeCategory = view.findViewById(R.id.badge_detail_category);
        txtCategory = view.findViewById(R.id.txt_detail_category);
        txtTitle = view.findViewById(R.id.txt_detail_title);

        cardModalityHighlight = view.findViewById(R.id.card_modality_highlight);
        imgModalityIcon = view.findViewById(R.id.img_detail_modality_icon);
        txtModalityLabel = view.findViewById(R.id.txt_detail_modality_label);
        txtModalityValue = view.findViewById(R.id.txt_detail_modality_value);

        txtServingsCount = view.findViewById(R.id.txt_servings_count);
        btnServingsMinus = view.findViewById(R.id.btn_servings_minus);
        btnServingsPlus = view.findViewById(R.id.btn_servings_plus);

        recyclerIngredients = view.findViewById(R.id.recycler_detail_ingredients);
        recyclerSteps = view.findViewById(R.id.recycler_detail_steps);

        ImageButton btnEdit = view.findViewById(R.id.btn_detail_edit);
        ImageButton btnDelete = view.findViewById(R.id.btn_detail_delete);

        ingredientAdapter = new IngredientDetailAdapter();
        recyclerIngredients.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerIngredients.setAdapter(ingredientAdapter);

        stepAdapter = new StepDetailAdapter();
        recyclerSteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSteps.setAdapter(stepAdapter);

        // Servings counter actions (- / +)
        btnServingsMinus.setOnClickListener(v -> {
            if (currentServings > 1) {
                currentServings--;
                updateServingsUI();
            }
        });

        btnServingsPlus.setOnClickListener(v -> {
            if (currentServings < 50) {
                currentServings++;
                updateServingsUI();
            }
        });

        // Edit button
        btnEdit.setOnClickListener(v -> {
            if (currentRecipe != null) {
                Bundle args = new Bundle();
                args.putLong("recipeId", currentRecipe.getId());
                Navigation.findNavController(v).navigate(R.id.action_RecipeDetail_to_RecipeForm, args);
            }
        });

        // Delete button with dialog
        btnDelete.setOnClickListener(v -> confirmDeleteRecipe());

        // Favorite button
        btnFavorite.setOnClickListener(v -> {
            if (currentRecipe != null) {
                boolean newFav = !currentRecipe.isFavorite();
                currentRecipe.setFavorite(newFav);
                dbHelper.setFavorite(currentRecipe.getId(), newFav);
                updateFavoriteButtonUI();
            }
        });

        loadRecipeData();
    }

    private void loadRecipeData() {
        if (recipeId <= 0) return;
        currentRecipe = dbHelper.getRecipeById(recipeId);
        if (currentRecipe == null) return;

        currentServings = currentRecipe.getServings();
        txtTitle.setText(currentRecipe.getTitle());

        // Photo loading
        String uriStr = currentRecipe.getPhotoUri();
        if (uriStr != null && uriStr.startsWith("drawable://")) {
            String drawableName = uriStr.replace("drawable://", "");
            int resId = requireContext().getResources().getIdentifier(drawableName, "drawable", requireContext().getPackageName());
            if (resId != 0) {
                imgPhoto.setImageResource(resId);
            } else {
                imgPhoto.setImageResource(R.drawable.placeholder_recipe);
            }
        } else if (uriStr != null && !uriStr.trim().isEmpty()) {
            try {
                imgPhoto.setImageURI(Uri.parse(uriStr));
            } catch (Exception e) {
                imgPhoto.setImageResource(R.drawable.placeholder_recipe);
            }
        } else {
            imgPhoto.setImageResource(R.drawable.placeholder_recipe);
        }

        // Modality styling & highlight details
        RecipeCategory cat = currentRecipe.getCategory();
        if (cat == null) cat = RecipeCategory.DESAYUNO;

        txtCategory.setText(cat.getSingleName().toUpperCase());

        int bgResColor, textResColor, iconRes;
        String modLabel, modValue;

        switch (cat) {
            case DESAYUNO:
                bgResColor = R.color.desayuno_container;
                textResColor = R.color.desayuno_text;
                iconRes = R.drawable.ic_timer;
                modLabel = "Tiempo de preparación";
                modValue = currentRecipe.getPrepTime() + " minutos";
                break;
            case ALMUERZO:
                bgResColor = R.color.almuerzo_container;
                textResColor = R.color.almuerzo_text;
                iconRes = R.drawable.ic_bar_chart;
                modLabel = "Nivel de dificultad";
                modValue = currentRecipe.getDifficulty();
                break;
            case POSTRE:
            default:
                bgResColor = R.color.postre_container;
                textResColor = R.color.postre_text;
                iconRes = R.drawable.ic_oven;
                modLabel = "Tiempo de reposo / horneado";
                modValue = currentRecipe.getBakeOrRestTime() + " minutos";
                break;
        }

        badgeCategory.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bgResColor)));
        txtCategory.setTextColor(ContextCompat.getColor(requireContext(), textResColor));

        cardModalityHighlight.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bgResColor)));
        imgModalityIcon.setImageResource(iconRes);
        imgModalityIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), textResColor)));
        txtModalityLabel.setText(modLabel);
        txtModalityValue.setText(modValue);

        updateFavoriteButtonUI();
        updateServingsUI();

        // Populate lists
        ingredientAdapter.setData(currentRecipe.getIngredients(), currentRecipe.getServings(), currentServings);
        stepAdapter.setSteps(currentRecipe.getSteps());
    }

    private void updateServingsUI() {
        txtServingsCount.setText(String.valueOf(currentServings));
        if (currentRecipe != null) {
            ingredientAdapter.updateServings(currentServings);
        }
    }

    private void updateFavoriteButtonUI() {
        if (currentRecipe != null && currentRecipe.isFavorite()) {
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void confirmDeleteRecipe() {
        if (currentRecipe == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar receta")
                .setMessage(getString(R.string.msg_confirm_delete, currentRecipe.getTitle()))
                .setPositiveButton(R.string.btn_eliminar, (dialog, which) -> {
                    dbHelper.deleteRecipe(currentRecipe.getId());
                    Toast.makeText(requireContext(), R.string.msg_receta_eliminada, Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}