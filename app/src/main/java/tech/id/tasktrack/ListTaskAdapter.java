package tech.id.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tech.id.tasktrack.model.Schedule;

public class ListTaskAdapter extends RecyclerView.Adapter<ListTaskAdapter.GridViewHolder>{

    private final List<Schedule> schedules;
    private final Context context;

    public ListTaskAdapter(List<Schedule> schedules, Context context) {
        this.context = context;
        this.schedules = schedules;
    }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_task, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);

        holder.tvTask.setText(schedule.kegiatan.task);
        holder.tvLokasi.setText(schedule.lokasi.building+ " - " +schedule.lokasi.floor +" - "+schedule.lokasi.building+ " - " +schedule.lokasi.floor);
        holder.tvSSID.setText(schedule.lokasi.ssid+ " - " +schedule.lokasi.ip_wifi);

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        TextView tvTask, tvLokasi, tvSSID;

        GridViewHolder(View itemView) {
            super(itemView);

            tvTask = itemView.findViewById(R.id.tvTask);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvSSID = itemView.findViewById(R.id.tvSSID);


        }
    }
}
