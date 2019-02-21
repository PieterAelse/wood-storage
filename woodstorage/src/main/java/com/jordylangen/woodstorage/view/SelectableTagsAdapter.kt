package com.jordylangen.woodstorage.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import com.jordylangen.woodstorage.R

import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView

class SelectableTagsAdapter internal constructor(private val callback: Callback) :
    RecyclerView.Adapter<SelectableTagsAdapter.SelectableTagViewHolder>() {
    private var selectableTags: MutableList<SelectableTag>? = null

    init {
        selectableTags = ArrayList()
    }

    interface Callback {
        fun tagSelectedChanged(selectableTag: SelectableTag, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableTagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_selectable_tag, parent, false)
        return SelectableTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectableTagViewHolder, position: Int) {
        val selectableTag = selectableTags!![position]
        holder.tagTextView.text = selectableTag.tag
        holder.isSelectedCheckbox.isChecked = selectableTag.isSelected

        holder.container.setOnClickListener {
            val newState = !selectableTag.isSelected
            selectableTag.isSelected = newState
            callback.tagSelectedChanged(selectableTag, newState)
            notifyDataSetChanged()
        }

        holder.isSelectedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            selectableTag.isSelected = isChecked
            callback.tagSelectedChanged(selectableTag, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return selectableTags!!.size
    }

    fun add(selectableTag: SelectableTag) {
        selectableTags!!.add(selectableTag)
        notifyItemInserted(selectableTags!!.size)
    }

    fun set(selectedTags: List<SelectableTag>) {
        this.selectableTags?.clear()
        this.selectableTags?.addAll(selectedTags)
        notifyDataSetChanged()
    }

    inner class SelectableTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: View = itemView.findViewById(R.id.selectable_tag_container)
        val tagTextView: TextView = itemView.findViewById(R.id.selectable_tag)
        val isSelectedCheckbox: CheckBox = itemView.findViewById(R.id.selectable_tag_is_selected)
    }
}
