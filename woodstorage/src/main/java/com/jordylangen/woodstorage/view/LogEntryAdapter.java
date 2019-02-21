package com.jordylangen.woodstorage.view;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;
import com.jordylangen.woodstorage.utils.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder> {

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

    private List<LogEntry> logs = new ArrayList<>();
    private Map<String, Integer> tagColors = new HashMap<>();

    @Override
    public LogEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_log, parent, false);
        return new LogEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogEntryViewHolder holder, int position) {
        LogEntry log = logs.get(position);
        holder.tagTextView.setText(log.getTag());
        holder.messageTextView.setText(log.getMessage());
        holder.priorityTextView.setText(getPriorityTextResource(log));
        holder.timestampTextView.setText(TIMESTAMP_FORMAT.format(log.getTimeStamp()));

        int color;
        if (tagColors.containsKey(log.getTag())) {
            color = tagColors.get(log.getTag());
        } else {
            color = ColorUtils.randomColor();
            tagColors.put(log.getTag(), color);
        }

        holder.colorIndicatorView.setBackgroundColor(color);
    }

    private int getPriorityTextResource(LogEntry log) {
        switch (log.getPriority()) {
            case Log.DEBUG:
                return R.string.log_level_debug;
            case Log.INFO:
                return R.string.log_level_info;
            case Log.WARN:
                return R.string.log_level_warn;
            case Log.ERROR:
                return R.string.log_level_error;
            default:
                return R.string.log_level_verbose;
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void add(LogEntry logEntry) {
        logs.add(logEntry);
        notifyItemInserted(logs.size());
    }

    public void add(LogEntry logEntry, int index) {
        logs.add(index, logEntry);
        notifyDataSetChanged();
    }

    public void clear() {
        logs.clear();
        notifyDataSetChanged();
    }

    class LogEntryViewHolder extends RecyclerView.ViewHolder {

        private View colorIndicatorView;
        private TextView tagTextView;
        private TextView priorityTextView;
        private TextView messageTextView;
        private TextView timestampTextView;

        LogEntryViewHolder(View itemView) {
            super(itemView);

            colorIndicatorView = itemView.findViewById(R.id.log_color_indicator);
            tagTextView = itemView.findViewById(R.id.log_tag);
            priorityTextView = itemView.findViewById(R.id.log_priority);
            messageTextView = itemView.findViewById(R.id.log_message);
            timestampTextView = itemView.findViewById(R.id.log_timestamp);
        }
    }
}
