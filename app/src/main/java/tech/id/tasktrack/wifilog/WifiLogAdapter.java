package tech.id.tasktrack.wifilog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tech.id.tasktrack.R;

public class WifiLogAdapter extends RecyclerView.Adapter<WifiLogAdapter.GridViewHolder> {
    public final static Locale localeID = new Locale("in", "ID");

    Context context;

    List<WifiLog> wifiLogs = new ArrayList<>();

//    public void setData(List<WifiLog> data) {
//        this.wifiLogs = data;
//        notifyDataSetChanged();
//    }


    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(String tanggal, boolean status);
    }

    public WifiLogAdapter(Context context, List<WifiLog> wifiLogs ) {
        this.context = context;
        this.wifiLogs = wifiLogs;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_log, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        WifiLog item = wifiLogs.get(position);

        String[] dt = splitTimestamp(item.timestamp);
        holder.tvJamLog.setText(dt[1]);
        holder.tvMenitLog.setText(dt[2]);
//        holder.tvTimestamp.setText("Time: "+dt[1]);
        holder.tvSSIDLog.setText(item.ssid);
        holder.tvIPWIFILog.setText(item.ipAddress);

    }
//
//    private String[] splitTimestamp(String timestamp) {
//        // contoh timestamp: "2025-11-17 09:45:12"
//        if (timestamp == null || !timestamp.contains(" ")) {
//            return new String[]{"-", "-"};
//        }
//
//        String[] parts = timestamp.split(" ");
//        String date = parts[0]; // yyyy-MM-dd
//        String time = parts[1].substring(0, 5); // jam:menit
//
//        return new String[]{date, time};
//    }

    private String[] splitTimestamp(String timestamp) {
        if (timestamp == null || !timestamp.contains(" ")) {
            return new String[]{"-", "-", "-"};
        }

        String[] parts = timestamp.split(" ");
        String date = parts[0];

        // bagian waktu: HH:mm:ss
        String[] timeParts = parts[1].split(":");
        String hour = timeParts.length > 0 ? timeParts[0] : "-";
        String minute = timeParts.length > 1 ? timeParts[1] : "-";

        return new String[]{date, hour, minute};
    }


    @Override
    public int getItemCount() {
        return wifiLogs.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tvSSIDLog, tvTimestamp, tvIPWIFILog, tvJamLog, tvMenitLog;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSSIDLog = itemView.findViewById(R.id.tvSSIDLog);
            tvJamLog = itemView.findViewById(R.id.tvJamLog);
            tvMenitLog = itemView.findViewById(R.id.tvMenitLog);
            tvIPWIFILog = itemView.findViewById(R.id.tvIPWIFILog);
        }
    }
}