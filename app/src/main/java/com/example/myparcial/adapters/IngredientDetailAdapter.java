package com.example.myparcial.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientDetailAdapter extends RecyclerView.Adapter<IngredientDetailAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();
    private int baseServings = 1;
    private int currentServings = 1;

    public void setData(List<Ingredient> ingredients, int baseServings, int currentServings) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        this.baseServings = baseServings > 0 ? baseServings : 1;
        this.currentServings = currentServings > 0 ? currentServings : 1;
        notifyDataSetChanged();
    }

    public void updateServings(int currentServings) {
        this.currentServings = currentServings > 0 ? currentServings : 1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_detail, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ing = ingredients.get(position);
        holder.bind(ing, baseServings, currentServings);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtQtyUnit;
        private final TextView txtName;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQtyUnit = itemView.findViewById(R.id.txt_ing_quantity_unit);
            txtName = itemView.findViewById(R.id.txt_ing_name);
        }

        public void bind(Ingredient ingredient, int baseServings, int currentServings) {
            double recalculatedQty = ingredient.getRecalculatedQuantity(baseServings, currentServings);
            String formattedQty = Ingredient.formatQuantity(recalculatedQty);

            String qtyUnitStr = formattedQty + " " + ingredient.getUnit();
            txtQtyUnit.setText(qtyUnitStr);
            txtName.setText(ingredient.getName());
        }
    }
}