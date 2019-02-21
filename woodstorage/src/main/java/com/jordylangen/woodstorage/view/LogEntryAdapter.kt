package com.jordylangen.woodstorage.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.jordylangen.woodstorage.storage.LogEntry
import com.jordylangen.woodstorage.R
import com.jordylangen.woodstorage.view.utils.ColorUtils

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView

class LogEntryAdapter : RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder>() {

    companion object {
        @SuppressLint("ConstantLocale")
        private val TIMESTAMP_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    }

    private val logs = ArrayList<LogEntry>()
    private val tagColors = HashMap<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogEntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_log, parent, false)
        return LogEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogEntryViewHolder, position: Int) {
        val log = logs[position]
        holder.tagTextView.text = log.tag
        holder.messageTextView.text = log.message
        holder.priorityTextView.setText(getPriorityTextResource(log))
        holder.timestampTextView.text = TIMESTAMP_FORMAT.format(log.timeStamp)

        val color: Int
        if (tagColors.containsKey(log.tag)) {
            color = tagColors[log.tag]!!
        } else {
            color = ColorUtils.randomColor()
            tagColors[log.tag!!] = color
        }

        holder.colorIndicatorView.setBackgroundColor(color)
    }

    private fun getPriorityTextResource(log: LogEntry): Int {
        return when (log.priority) {
            Log.DEBUG -> R.string.log_level_debug
            Log.INFO -> R.string.log_level_info
            Log.WARN -> R.string.log_level_warn
            Log.ERROR -> R.string.log_level_error
            else -> R.string.log_level_verbose
        }
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    fun add(logEntry: LogEntry) {
        logs.add(logEntry)
        notifyItemInserted(logs.size)
    }

    fun add(logEntry: LogEntry, index: Int) {
        logs.add(index, logEntry)
        notifyDataSetChanged()
    }

    fun clear() {
        logs.clear()
        notifyDataSetChanged()
    }

    inner class LogEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorIndicatorView: View = itemView.findViewById(R.id.log_color_indicator)
        val tagTextView: TextView = itemView.findViewById(R.id.log_tag)
        val priorityTextView: TextView = itemView.findViewById(R.id.log_priority)
        val messageTextView: TextView = itemView.findViewById(R.id.log_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.log_timestamp)
    }
}
