package tech.id.tasktrack.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.id.tasktrack.R;
import tech.id.tasktrack.api.ApiClient;
import tech.id.tasktrack.api.ApiService;
import tech.id.tasktrack.api.SessionManager;
import tech.id.tasktrack.dbase.DatabaseHelper;
import tech.id.tasktrack.model.ApiResponse;
import tech.id.tasktrack.model.Schedule;
import tech.id.tasktrack.schedule.ScheduleAdapter;

public class TodayScheduleAdapter extends RecyclerView.Adapter<TodayScheduleAdapter.ViewHolder> {

    private final List<Schedule> list;
    private final Context context;
    SessionManager session;

    ApiService apiService;
    public TodayScheduleAdapter(Context context, List<Schedule> list) {
        this.context = context;
        this.list = list;
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.session = new SessionManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvTask, tvLokasi, tvKeterangan;
        ImageView ivUnChecked, ivChecked;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTask = itemView.findViewById(R.id.tvTask);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            ivUnChecked = itemView.findViewById(R.id.ivUnChecked);
            ivChecked = itemView.findViewById(R.id.ivChecked);

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

        holder.ivUnChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivUnChecked.setVisibility(View.INVISIBLE);
                holder.ivChecked.setVisibility(View.VISIBLE);

                updateVerifikasiPegawai(item.id, item.pegawai.id,item.tanggal, "ya");
                DatabaseHelper db = new DatabaseHelper(v.getContext());
                db.updateVerifikasiPegawai(
                        item.id,
                        item.pegawai.id,
                        item.tanggal,
                        "ya"
                );
            }
        });


        holder.ivChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivChecked.setVisibility(View.INVISIBLE);
                holder.ivUnChecked.setVisibility(View.VISIBLE);

                updateVerifikasiPegawai(item.id, item.pegawai.id,item.tanggal, "tidak");

                DatabaseHelper db = new DatabaseHelper(v.getContext());
                db.updateVerifikasiPegawai(
                        item.id,
                        item.pegawai.id,
                        item.tanggal,
                        "tidak"
                );

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void updateVerifikasiPegawai(int id, int pegawaiId, String tanggal, String verifikasi_pegawai) {
        String token = "Bearer " + session.getToken();

        Call<ApiResponse> call = apiService.updateVerifikasiPegawai(
                token,
                id,
                pegawaiId,
                tanggal,
                verifikasi_pegawai
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Schedule Ya", "Update success: " + response.body().message);
                } else {
                    Log.e("API Schedule Ya", "Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private ScheduleAdapter.OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(ScheduleAdapter.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(String tanggal, boolean status);
    }



}

