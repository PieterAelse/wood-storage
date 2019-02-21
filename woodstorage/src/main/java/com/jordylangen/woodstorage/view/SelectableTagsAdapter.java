package com.jordylangen.woodstorage.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jordylangen.woodstorage.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectableTagsAdapter extends RecyclerView.Adapter<SelectableTagsAdapter.SelectableTagViewHolder> {

    private Callback callback;
    private List<SelectableTag> selectableTags;

    SelectableTagsAdapter(Callback callback) {
        this.callback = callback;
        selectableTags = new ArrayList<>();
    }

    @Override
    public SelectableTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_selectable_tag, parent, false);
        return new SelectableTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableTagViewHolder holder, int position) {
        final SelectableTag selectableTag = selectableTags.get(position);
        holder.tagTextView.setText(selectableTag.getTag());
        holder.isSelectedCheckbox.setChecked(selectableTag.isSelected());

        holder.isSelectedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectableTag.setIsSelected(isChecked);
                callback.tagSelectedChanged(selectableTag, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectableTags.size();
    }

    public void add(SelectableTag selectableTag) {
        selectableTags.add(selectableTag);
        notifyItemInserted(selectableTags.size());
    }

    public void set(List<SelectableTag> selectedTags) {
        this.selectableTags = selectedTags;
        notifyDataSetChanged();
    }

    class SelectableTagViewHolder extends RecyclerView.ViewHolder {

        private TextView tagTextView;
        private CheckBox isSelectedCheckbox;

        SelectableTagViewHolder(View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.selectable_tag);
            isSelectedCheckbox = itemView.findViewById(R.id.selectable_tag_is_selected);
        }
    }

    public interface Callback {
        void tagSelectedChanged(SelectableTag selectableTag, boolean isChecked);
    }
}
