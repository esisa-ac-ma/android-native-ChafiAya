package esisa.ac.ma.projet_natif.adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import esisa.ac.ma.projet_natif.R;
import esisa.ac.ma.projet_natif.entities.Call;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {
    private Context context;
    private Map<String, Boolean> titleExpansionMap;
    private List<Call> calls;
    private List<Call> filteredCalls;

    public CallAdapter(Context context, Map<String, Call> latestCallsMap, Map<String, List<Call>> groupedCallsMap) {
        this.context = context;
        this.titleExpansionMap = new HashMap<>();
        this.calls = new ArrayList<>();
        this.filteredCalls = new ArrayList<>();

        // Add latest calls first
        for (Call call : latestCallsMap.values()) {
            calls.add(call);
        }

        // Group calls by title and set initial expansion state
        for (String title : groupedCallsMap.keySet()) {
            List<Call> callsForTitle = groupedCallsMap.get(title);
            calls.addAll(callsForTitle); // Add all calls for this title
            titleExpansionMap.put(title, false); // Set initial expansion state for this title
        }
        this.filteredCalls.addAll(calls);
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        Call call = filteredCalls.get(position);
        holder.bindCall(call);
    }

    @Override
    public int getItemCount() {
        return filteredCalls.size();
    }

    public void filterList(String query) {
        filteredCalls.clear();
        if (query.isEmpty()) {
            // Initially, only show the latest call for each title
            Map<String, Call> latestCallsMap = new HashMap<>();
            for (Call call : calls) {
                if (!latestCallsMap.containsKey(call.getTitle())) {
                    latestCallsMap.put(call.getTitle(), call);
                } else {
                    // If another call for the same title is more recent, replace it
                    Call existingCall = latestCallsMap.get(call.getTitle());
                    if (call.getDate() > existingCall.getDate()) {
                        latestCallsMap.put(call.getTitle(), call);
                    }
                }
            }
            filteredCalls.addAll(latestCallsMap.values());
        } else {
            for (Call call : calls) {
                if (call.getTitle().toLowerCase().equals(query.toLowerCase())) {
                    filteredCalls.add(call);
                }
            }
        }
        notifyDataSetChanged();
    }



    public void filterList2(String query) {
        filteredCalls.clear();
        if (query.isEmpty()) {
            filteredCalls.addAll(calls);
        } else {
            for (Call call : calls) {
                if (call.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredCalls.add(call);
                }
            }
        }
        notifyDataSetChanged();
    }


    public class CallViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView typeTextView;
        TextView dateTextView;
        TextView durationTextView;
        TextView callTimeTextView;
        ImageView expandCollapseImageView;
        RecyclerView groupedCallsRecyclerView;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            typeTextView = itemView.findViewById(R.id.text_view_type);
            dateTextView = itemView.findViewById(R.id.text_view_date);
            durationTextView = itemView.findViewById(R.id.text_view_duration);
            callTimeTextView = itemView.findViewById(R.id.text_view_call_time);
            expandCollapseImageView = itemView.findViewById(R.id.expand_collapse_image_view);
            groupedCallsRecyclerView = itemView.findViewById(R.id.grouped_calls_recycler_view);

            // Set click listener for the title view
            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Call call = filteredCalls.get(position);
                        String title = call.getTitle();
                        boolean expanded = !titleExpansionMap.get(title); // Toggle expanded state
                        titleExpansionMap.put(title, expanded);
                        notifyItemChanged(position);

                        // Update visibility of grouped calls RecyclerView
                        if (expanded) {
                            groupedCallsRecyclerView.setVisibility(View.VISIBLE);
                            updateFilteredCalls();
                            // Set up grouped calls RecyclerView here
                        } else {
                            groupedCallsRecyclerView.setVisibility(View.GONE);
                            filterList("");
                        }

                        // Update the filtered list of calls

                    }
                }
            });
        }

        public void bindCall(Call call) {
            if (call != null) {
                String title = call.getTitle();
                boolean expanded = titleExpansionMap.get(title);

                // Show or hide the grouped calls based on the expanded state
                if (expanded) {
                    groupedCallsRecyclerView.setVisibility(View.VISIBLE);
                    // Set up grouped calls RecyclerView here
                } else {
                    groupedCallsRecyclerView.setVisibility(View.GONE);
                }


                titleTextView.setText(title);

                // Set text color based on call type
                switch (call.getType()) {
                    case "Incoming":
                        typeTextView.setTextColor(context.getResources().getColor(R.color.green));
                        break;
                    case "Outgoing":
                        typeTextView.setTextColor(context.getResources().getColor(R.color.blue));
                        break;
                    case "Missed":
                        typeTextView.setTextColor(context.getResources().getColor(R.color.red));
                        break;
                    case "Unknown":
                        typeTextView.setTextColor(context.getResources().getColor(R.color.black));
                        break;
                }

                typeTextView.setText(call.getType());
                dateTextView.setText(getFormattedDate(call.getDate()));
                durationTextView.setText(String.valueOf(call.getDuration()));
                callTimeTextView.setText(call.getCallTime());
            }
        }

        private int getRandomColor() {
            Random rnd = new Random();
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }

        private void updateFilteredCalls() {
            filteredCalls.clear();
            for (Call call : calls) {
                String title = call.getTitle();
                if (titleExpansionMap.get(title)) {
                    filteredCalls.add(call);
                }
            }
            notifyDataSetChanged();
        }

        private String getFormattedDate(long date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return simpleDateFormat.format(new Date(date));
        }
    }
}
