package tech.id.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import tech.id.tasktrack.model.Schedule;

public class TodayScheduleAdapter extends RecyclerView.Adapter<TodayScheduleAdapter.ViewHolder> {

private final List<Schedule> list;
private final Context context;

public TodayScheduleAdapter(Context context, List<Schedule> list) {
    this.context = context;
    this.list = list;
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvTanggal, tvTask, tvLokasi, tvKeterangan;

    public ViewHolder(View itemView) {
        super(itemView);
        tvTask = itemView.findViewById(R.id.tvTask);
        tvLokasi = itemView.findViewById(R.id.tvLokasi);
    }
}

@Override
public TodayScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
    return new ViewHolder(v);
}

@Override
public void onBindViewHolder(TodayScheduleAdapter.@NonNull ViewHolder holder, int position) {
    Schedule item = list.get(position);

    holder.tvTask.setText((item.kegiatan != null ? item.kegiatan.task : "-")
    );

    holder.tvLokasi.setText(
                    (item.lokasi != null ? item.lokasi.building : "-") +
                    " - " +
                    (item.lokasi != null ? item.lokasi.floor : "-")
    );

}

@Override
public int getItemCount() {
    return list.size();
}
}