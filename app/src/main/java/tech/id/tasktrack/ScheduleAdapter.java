package tech.id.tasktrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.GridViewHolder> {
    public final static Locale localeID = new Locale("in", "ID");

    static ArrayList<String> tanggalCalendar = new ArrayList<>();
    static ArrayList<String> tanggalJadwal = new ArrayList<>();
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

    public ScheduleAdapter(Context context) {


        int bulan = Integer.parseInt(BULAN.format(new Date()));
        int tahun = Integer.parseInt(TAHUN.format(new Date()));

        printDatesInMonth(tahun, bulan);

    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_jadwal, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.tanggal.setText(tanggalCalendar.get(position));
    }

    @Override
    public int getItemCount() {
        return tanggalJadwal.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            tanggal = itemView.findViewById(R.id.txtTanggal);

        }
    }
}
