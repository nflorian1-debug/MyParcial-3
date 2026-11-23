package com.example.myparcial.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import java.util.List;

public class FavoritesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecipeDbHelper dbHelper;
    private RecipeAdapter adapter;

    private RecyclerView recyclerView;
    private LinearLayout emptyFavContainer;
    private EditText edtSearchFav;
    private ChipGroup chipGroupFavCategories;

    private String currentSearchQuery = "";
    private String currentCategoryFilter = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = RecipeDbHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recycler_favorites);
        emptyFavContainer = view.findViewById(R.id.empty_fav_container);
        edtSearchFav = view.findViewById(R.id.edt_search_fav);
        chipGroupFavCategories = view.findViewById(R.id.chip_group_fav_categories);

        adapter = new RecipeAdapter(requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        chipGroupFavCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_fav_desayunos) {
                currentCategoryFilter = "DESAYUNO";
            } else if (id == R.id.chip_fav_almuerzos) {
                currentCategoryFilter = "ALMUERZO";
            } else if (id == R.id.chip_fav_postres) {
                currentCategoryFilter = "POSTRE";
            } else {
                currentCategoryFilter = "ALL";
            }
            loadFavorites();
        });

        edtSearchFav.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                loadFavorites();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Recipe> favorites = dbHelper.getRecipes(currentSearchQuery, currentCategoryFilter, true);
        adapter.setRecipes(favorites);

        if (favorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyFavContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyFavContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Bundle args = new Bundle();
        args.putLong("recipeId", recipe.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_Favorites_to_RecipeDetail, args);
    }

    @Override
    public void onFavoriteToggle(Recipe recipe, int position) {
        dbHelper.setFavorite(recipe.getId(), false);
        loadFavorites();
    }
}