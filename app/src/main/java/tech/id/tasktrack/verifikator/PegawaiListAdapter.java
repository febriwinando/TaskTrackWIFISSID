package tech.id.tasktrack.verifikator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import tech.id.tasktrack.R;
import tech.id.tasktrack.verifikator.model.PegawaiSimple;

public class PegawaiListAdapter extends RecyclerView.Adapter<PegawaiListAdapter.ViewHolder> {

    Context context;
    List<PegawaiSimple> pegawaiList;

    public PegawaiListAdapter(Context context, List<PegawaiSimple> pegawaiList) {
        this.context = context;
        this.pegawaiList = pegawaiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_pegawai, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PegawaiSimple p = pegawaiList.get(position);
        holder.tvPegawaiName.setText(p.name);
        holder.tvEmployeeIDs.setText("Employee ID: "+p.employee_id);
    }

    @Override
    public int getItemCount() {
        return pegawaiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPegawaiName, tvEmployeeIDs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPegawaiName = itemView.findViewById(R.id.tvPegawaiName);
            tvEmployeeIDs = itemView.findViewById(R.id.tvEmployeeIDs);
        }
    }
}