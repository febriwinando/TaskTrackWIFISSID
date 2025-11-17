package tech.id.tasktrack;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import tech.id.tasktrack.model.Schedule;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

private final List<Schedule> list;
private final Context context;

public ScheduleAdapter(Context context, List<Schedule> list) {
    this.context = context;
    this.list = list;
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvTanggal, tvTask, tvLokasi, tvKeterangan;

    public ViewHolder(View itemView) {
        super(itemView);
        tvTanggal = itemView.findViewById(R.id.tvTanggal);
        tvTask = itemView.findViewById(R.id.tvTask);
        tvLokasi = itemView.findViewById(R.id.tvLokasi);
        tvKeterangan = itemView.findViewById(R.id.tvKeterangan);
    }
}

@Override
public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
    return new ViewHolder(v);
}

@Override
public void onBindViewHolder(ScheduleAdapter.@NonNull ViewHolder holder, int position) {
    Schedule item = list.get(position);

    Log.d("Scedule", String.valueOf(item.kegiatan));
    holder.tvTanggal.setText(item.tanggal);

    holder.tvTask.setText(
            "Task: " + (item.kegiatan != null ? item.kegiatan.task : "-")
    );

    holder.tvLokasi.setText(
            "Lokasi: " +
                    (item.lokasi != null ? item.lokasi.building : "-") +
                    " - " +
                    (item.lokasi != null ? item.lokasi.floor : "-")
    );

    holder.tvKeterangan.setText("Keterangan: " + item.keterangan);
}

@Override
public int getItemCount() {
    return list.size();
}
}