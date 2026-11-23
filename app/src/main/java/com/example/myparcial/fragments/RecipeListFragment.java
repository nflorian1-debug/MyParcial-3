package com.example.myparcial.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.adapters.RecipeAdapter;
import com.example.myparcial.db.RecipeDbHelper;
import com.example.myparcial.models.Recipe;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecipeListFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecipeDbHelper dbHelper;
    private RecipeAdapter adapter;

    private RecyclerView recyclerView;
    private LinearLayout emptyStateContainer;
    private TextView txtEmptyTitle;
    private TextView txtEmptySubtitle;
    private EditText edtSearch;
    private ChipGroup chipGroupCategories;

    private String currentSearchQuery = "";
    private String currentCategoryFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = RecipeDbHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recycler_recipes);
        emptyStateContainer = view.findViewById(R.id.empty_state_container);
        txtEmptyTitle = view.findViewById(R.id.txt_empty_title);
        txtEmptySubtitle = view.findViewById(R.id.txt_empty_subtitle);
        edtSearch = view.findViewById(R.id.edt_search);
        chipGroupCategories = view.findViewById(R.id.chip_group_categories);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_recipe);

        adapter = new RecipeAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Category chips filter setup
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_desayunos) {
                currentCategoryFilter = "DESAYUNO";
            } else if (id == R.id.chip_almuerzos) {
                currentCategoryFilter = "ALMUERZO";
            } else if (id == R.id.chip_postres) {
                currentCategoryFilter = "POSTRE";
            } else {
                currentCategoryFilter = "ALL";
            }
            loadRecipes();
        });

        // Search text watcher
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                loadRecipes();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add button FAB
        fabAdd.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("recipeId", -1L);
            Navigation.findNavController(v).navigate(R.id.action_RecipeList_to_RecipeForm, args);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        List<Recipe> recipes = dbHelper.getRecipes(currentSearchQuery, currentCategoryFilter, false);
        adapter.setRecipes(recipes);

        if (recipes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);

            // Determine friendly empty message
            List<Recipe> totalAll = dbHelper.getRecipes("", "ALL", false);
            if (totalAll.isEmpty()) {
                txtEmptyTitle.setText(R.string.empty_recipes_title);
                txtEmptySubtitle.setText(R.string.empty_recipes_subtitle);
            } else {
                txtEmptyTitle.setText("Sin resultados");
                txtEmptySubtitle.setText(R.string.empty_search_subtitle);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Bundle args = new Bundle();
        args.putLong("recipeId", recipe.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_RecipeList_to_RecipeDetail, args);
    }

    @Override
    public void onFavoriteToggle(Recipe recipe, int position) {
        boolean newFavStatus = !recipe.isFavorite();
        dbHelper.setFavorite(recipe.getId(), newFavStatus);
        recipe.setFavorite(newFavStatus);
        adapter.notifyItemChanged(position);
    }
}