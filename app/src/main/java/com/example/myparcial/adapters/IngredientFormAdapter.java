package com.example.myparcial.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.models.Ingredient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class IngredientFormAdapter extends RecyclerView.Adapter<IngredientFormAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();

    public IngredientFormAdapter(List<Ingredient> ingredients) {
        if (ingredients != null) {
            this.ingredients = ingredients;
        }
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient() {
        ingredients.add(new Ingredient());
        notifyItemInserted(ingredients.size() - 1);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_form, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText edtName;
        private final TextInputEditText edtQuantity;
        private final TextInputEditText edtUnit;
        private final ImageButton btnRemove;

        private TextWatcher nameWatcher;
        private TextWatcher qtyWatcher;
        private TextWatcher unitWatcher;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            edtName = itemView.findViewById(R.id.edt_ing_name);
            edtQuantity = itemView.findViewById(R.id.edt_ing_quantity);
            edtUnit = itemView.findViewById(R.id.edt_ing_unit);
            btnRemove = itemView.findViewById(R.id.btn_remove_ing);
        }

        public void bind(Ingredient ingredient, int position) {
            // Remove previous watchers to avoid duplicate updates during re-binding
            if (nameWatcher != null) edtName.removeTextChangedListener(nameWatcher);
            if (qtyWatcher != null) edtQuantity.removeTextChangedListener(qtyWatcher);
            if (unitWatcher != null) edtUnit.removeTextChangedListener(unitWatcher);

            edtName.setText(ingredient.getName());
            edtQuantity.setText(ingredient.getQuantity() > 0 ? Ingredient.formatQuantity(ingredient.getQuantity()) : "");
            edtUnit.setText(ingredient.getUnit());

            nameWatcher = new SimpleTextWatcher() {
                @Override
                public void onTextChanged(String s) {
                    ingredient.setName(s);
                }
            };

            qtyWatcher = new SimpleTextWatcher() {
                @Override
                public void onTextChanged(String s) {
                    try {
                        double qty = Double.parseDouble(s.trim());
                        ingredient.setQuantity(qty);
                    } catch (Exception e) {
                        ingredient.setQuantity(0.0);
                    }
                }
            };

            unitWatcher = new SimpleTextWatcher() {
                @Override
                public void onTextChanged(String s) {
                    ingredient.setUnit(s);
                }
            };

            edtName.addTextChangedListener(nameWatcher);
            edtQuantity.addTextChangedListener(qtyWatcher);
            edtUnit.addTextChangedListener(unitWatcher);

            btnRemove.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < ingredients.size()) {
                    ingredients.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, ingredients.size());
                }
            });
        }
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextChanged(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}

        public abstract void onTextChanged(String s);
    }
}