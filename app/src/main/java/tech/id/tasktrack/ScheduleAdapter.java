package tech.id.tasktrack;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.id.tasktrack.model.Schedule;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.GridViewHolder> {
    public final static Locale localeID = new Locale("in", "ID");
    List<Schedule> schedules;
    ArrayList<String> tanggalCalendar = new ArrayList<>();
    ArrayList<String> tanggalJadwal = new ArrayList<>();
    Context context;
    public static SimpleDateFormat BULAN = new SimpleDateFormat("MM", localeID);
    public static SimpleDateFormat TAHUN = new SimpleDateFormat("yyyy", localeID);
    public void printDatesInMonth(int year, int month) {

        tanggalCalendar.clear();
        tanggalJadwal.clear();

        SimpleDateFormat fmt = new SimpleDateFormat("d");
        SimpleDateFormat fmtJadwalSift = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat fmtBefore = new SimpleDateFormat("d/M");
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cal.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        String newDate = fmtBefore.format(calendar.getTime());
        String dateLasmonth = fmtJadwalSift.format(calendar.getTime());

        tanggalCalendar.add(newDate);
        tanggalJadwal.add(dateLasmonth);

        for (int i = 0; i < daysInMonth; i++) {
            tanggalCalendar.add(fmt.format(cal.getTime()));
            tanggalJadwal.add(fmtJadwalSift.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    private OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(String tanggal, boolean status);
    }

    public ScheduleAdapter(Context context, List<Schedule> schedules, int year, int month) {
        this.context = context;
        this.schedules = schedules;

        tanggalCalendar = new ArrayList<>();
        tanggalJadwal = new ArrayList<>();
        printDatesInMonth(year, month);

    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_jadwal, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {

        String tanggal = tanggalJadwal.get(position);
        holder.txtTanggal.setText(tanggalCalendar.get(position));

        boolean adaJadwal = false;


        for (Schedule s : schedules) {
            if (s.tanggal.equals(tanggal)) {
                adaJadwal = true;

                break;

            }

        }

        if (adaJadwal) {
            holder.cvScheduleList.setBackgroundResource(R.drawable.bg_card);
            holder.txtTanggal.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white)
            );
        } else {
            holder.cvScheduleList.setBackgroundResource(R.drawable.bg_card_none);
        }
        String finalTanggal = tanggal;
        boolean finalAdaJadwal = adaJadwal;
        holder.rlTanggal.setOnClickListener(v ->
                onItemClickCallback.onItemClicked(finalTanggal, finalAdaJadwal)
        );

    }

    @Override
    public int getItemCount() {
        return tanggalJadwal.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggal;
        CardView cvScheduleList;
        RelativeLayout rlTanggal;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            cvScheduleList = itemView.findViewById(R.id.cvScheduleList);
            rlTanggal = itemView.findViewById(R.id.rlTanggal);

        }
    }
}
