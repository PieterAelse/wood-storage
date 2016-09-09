package com.jordylangen.woodstorage.view;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryAdapter.LogEntryViewHolder> {

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

    private List<LogEntry> logs = new ArrayList<>();
    private Map<String, Integer> tagColors = new HashMap<>();

    @Override
    public LogEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_log, parent, false);
        return new LogEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogEntryViewHolder holder, int position) {
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

    public class LogEntryViewHolder extends RecyclerView.ViewHolder {

        private View colorIndicatorView;
        private TextView tagTextView;
        private TextView priorityTextView;
        private TextView messageTextView;
        private TextView timestampTextView;

        public LogEntryViewHolder(View itemView) {
            super(itemView);

            colorIndicatorView = itemView.findViewById(R.id.log_color_indicator);
            tagTextView = (TextView) itemView.findViewById(R.id.log_tag);
            priorityTextView = (TextView) itemView.findViewById(R.id.log_priority);
            messageTextView = (TextView) itemView.findViewById(R.id.log_message);
            timestampTextView = (TextView) itemView.findViewById(R.id.log_timestamp);
        }
    }
}
