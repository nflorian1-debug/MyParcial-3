package com.example.myparcial.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myparcial.R;
import com.example.myparcial.models.Step;

import java.util.ArrayList;
import java.util.List;

public class StepDetailAdapter extends RecyclerView.Adapter<StepDetailAdapter.StepViewHolder> {

    private List<Step> steps = new ArrayList<>();

    public void setSteps(List<Step> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_detail, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.bind(step, position + 1);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNumber;
        private final TextView txtInstruction;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = itemView.findViewById(R.id.txt_step_number);
            txtInstruction = itemView.findViewById(R.id.txt_step_instruction);
        }

        public void bind(Step step, int displayIndex) {
            txtNumber.setText(String.valueOf(displayIndex));
            txtInstruction.setText(step.getInstruction());
        }
    }
}