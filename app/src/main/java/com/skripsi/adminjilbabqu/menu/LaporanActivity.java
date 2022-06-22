package com.skripsi.adminjilbabqu.menu;

import static com.skripsi.adminjilbabqu.helper.utils.CustomProgressbar.customProgress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.skripsi.adminjilbabqu.R;
import com.skripsi.adminjilbabqu.helper.Connection;
import com.skripsi.adminjilbabqu.helper.utils.CustomDialog;
import com.skripsi.adminjilbabqu.menu.transaksi.DetailTransaksi;
import com.skripsi.adminjilbabqu.model.TransaksiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LaporanActivity extends AppCompatActivity {
    private EditText et_total, et_mulai, et_akhir;
    private TextView text_simpan;
    String tanggal_awal, tanggal_akhir;
    String total_tagihan, total_tagihan_format;
    List<TransaksiModel> TransaksiModel;
    private RecyclerView rv_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        et_total = findViewById(R.id.et_total);
        et_mulai = findViewById(R.id.et_mulai);
        et_akhir = findViewById(R.id.et_akhir);
        text_simpan = findViewById(R.id.text_simpan);
        rv_data = findViewById(R.id.rv_data);

        TransaksiModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        AksiTombol();
    }

    private void AksiTombol() {
        et_mulai.setOnClickListener(v -> filterTanggal());
        text_simpan.setOnClickListener(v -> {
            if (et_mulai.length() == 0){
                Toast.makeText(this, "Tanggal belum dipilih", Toast.LENGTH_SHORT).show();
            } else {
                LoadData();
            }
        });
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminLaporan.php")
                .addBodyParameter("tanggal_awal", tanggal_awal)
                .addBodyParameter("tanggal_akhir", tanggal_akhir)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject responses = response.optJSONObject("data_transaksi");
                        total_tagihan_format = responses.optString("total_tagihan_format");

                        et_total.setText(total_tagihan_format);

                        try {
                            JSONArray jsonArray = response.optJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                TransaksiModel bk = new TransaksiModel(
                                        res.getString("idtransaksi"),
                                        res.getString("nama"),
                                        res.getString("total_tagihan"),
                                        res.getString("invoice"));
                                TransaksiModel.add(bk);
                            }

                            SesudahAdapter adapter = new SesudahAdapter(getApplicationContext(), TransaksiModel);
                            rv_data.setAdapter(adapter);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            CustomDialog.errorDialog(LaporanActivity.this, "Data tidak ditemukan");
                        } else {
                            CustomDialog.errorDialog(LaporanActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            customProgress.hideProgress();
                        }
                    }
                });
    }

    public class SesudahAdapter extends RecyclerView.Adapter<SesudahAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TransaksiModel> TransaksiModel;

        SesudahAdapter(Context mCtx, List<TransaksiModel> TransaksiModel) {
            this.mCtx = mCtx;
            this.TransaksiModel = TransaksiModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_penjualan_sudah, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final TransaksiModel tr = TransaksiModel.get(i);
            holder.text_nama.setText(tr.getNama_pelanggan());
            holder.text_total.setText(tr.getJumlah());

            holder.text_detail.setOnClickListener(v -> {
                Intent a = new Intent(mCtx, DetailTransaksi.class);
                a.putExtra("idtransaksi", tr.getIdtransaksi());
                startActivity(a);
            });
        }

        @Override
        public int getItemCount() {
            return TransaksiModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_total, text_detail;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_total = itemView.findViewById(R.id.text_total);
                text_detail = itemView.findViewById(R.id.text_detail);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

    private void filterTanggal() {
        SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                SmoothDateRangePickerFragment
                        .newInstance((view, yearStart, monthStart, dayStart, yearEnd, monthEnd, dayEnd) -> {
                            String date = "You picked the following date range: \n"
                                    + "From " + dayStart + "/" + (++monthStart)
                                    + "/" + yearStart + " To " + dayEnd + "/"
                                    + (++monthEnd) + "/" + yearEnd;

                            tanggal_awal = yearStart + "-" + monthStart + "-" + dayStart;
                            tanggal_akhir = yearEnd + "-" + monthEnd + "-" + dayEnd;
                            et_mulai.setText(yearStart + "-" + monthStart + "-" + dayStart);
                            et_akhir.setText(yearEnd + "-" + monthEnd + "-" + dayEnd);
                        });
        smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
    }
}