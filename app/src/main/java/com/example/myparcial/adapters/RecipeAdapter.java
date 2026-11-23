package com.example.myparcial.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.models.Recipe;
import com.example.myparcial.models.RecipeCategory;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
        void onFavoriteToggle(Recipe recipe, int position);
    }

    private final Context context;
    private List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(Context context, OnRecipeClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes != null ? recipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe, listener, context);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgRecipe;
        private final LinearLayout badgeContainer;
        private final TextView txtCategory;
        private final ImageButton btnFavorite;
        private final TextView txtTitle;
        private final ImageView imgModalityIcon;
        private final TextView txtModalityInfo;
        private final TextView txtServingsInfo;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.img_recipe);
            badgeContainer = itemView.findViewById(R.id.badge_category_container);
            txtCategory = itemView.findViewById(R.id.txt_recipe_category);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            txtTitle = itemView.findViewById(R.id.txt_recipe_title);
            imgModalityIcon = itemView.findViewById(R.id.img_modality_icon);
            txtModalityInfo = itemView.findViewById(R.id.txt_modality_info);
            txtServingsInfo = itemView.findViewById(R.id.txt_servings_info);
        }

        public void bind(Recipe recipe, OnRecipeClickListener listener, Context context) {
            txtTitle.setText(recipe.getTitle());

            String servingsText = recipe.getServings() + (recipe.getServings() == 1 ? " porción" : " porciones");
            txtServingsInfo.setText(servingsText);
            txtModalityInfo.setText(recipe.getModalityInfoText());

            // Image handling
            String uriStr = recipe.getPhotoUri();
            if (uriStr != null && uriStr.startsWith("drawable://")) {
                String drawableName = uriStr.replace("drawable://", "");
                int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                if (resId != 0) {
                    imgRecipe.setImageResource(resId);
                } else {
                    imgRecipe.setImageResource(R.drawable.placeholder_recipe);
                }
            } else if (uriStr != null && !uriStr.trim().isEmpty()) {
                try {
                    imgRecipe.setImageURI(Uri.parse(uriStr));
                } catch (Exception e) {
                    imgRecipe.setImageResource(R.drawable.placeholder_recipe);
                }
            } else {
                imgRecipe.setImageResource(R.drawable.placeholder_recipe);
            }

            // Category badge & modality styling
            RecipeCategory cat = recipe.getCategory();
            if (cat == null) cat = RecipeCategory.DESAYUNO;

            txtCategory.setText(cat.getPluralName().toUpperCase());

            int bgResColor, textResColor, iconRes;
            switch (cat) {
                case DESAYUNO:
                    bgResColor = R.color.desayuno_container;
                    textResColor = R.color.desayuno_text;
                    iconRes = R.drawable.ic_timer;
                    break;
                case ALMUERZO:
                    bgResColor = R.color.almuerzo_container;
                    textResColor = R.color.almuerzo_text;
                    iconRes = R.drawable.ic_bar_chart;
                    break;
                case POSTRE:
                default:
                    bgResColor = R.color.postre_container;
                    textResColor = R.color.postre_text;
                    iconRes = R.drawable.ic_oven;
                    break;
            }

            badgeContainer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, bgResColor)));
            txtCategory.setTextColor(ContextCompat.getColor(context, textResColor));
            imgModalityIcon.setImageResource(iconRes);

            // Favorite button
            if (recipe.isFavorite()) {
                btnFavorite.setImageResource(R.drawable.ic_favorite);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            }

            btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteToggle(recipe, getAdapterPosition());
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}