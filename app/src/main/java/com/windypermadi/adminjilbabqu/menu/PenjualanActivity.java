package com.windypermadi.adminjilbabqu.menu;

import static com.windypermadi.adminjilbabqu.helper.utils.CustomProgressbar.customProgress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.windypermadi.adminjilbabqu.MainActivity;
import com.windypermadi.adminjilbabqu.R;
import com.windypermadi.adminjilbabqu.helper.Connection;
import com.windypermadi.adminjilbabqu.helper.utils.CustomDialog;
import com.windypermadi.adminjilbabqu.model.KategoriModel;
import com.windypermadi.adminjilbabqu.model.TransaksiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PenjualanActivity extends AppCompatActivity {
    private TextView text_belum, text_sudah;
    private RecyclerView rv_data, rv_data2;
    List<TransaksiModel> TransaksiModel;
    private LinearLayout ly_kosong2, ly_kosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);

        text_belum = findViewById(R.id.text_belum);
        text_sudah = findViewById(R.id.text_sudah);
        rv_data    = findViewById(R.id.rv_data);
        rv_data2   = findViewById(R.id.rv_data2);
        ly_kosong   = findViewById(R.id.ly_kosong);
        ly_kosong2   = findViewById(R.id.ly_kosong2);

        TransaksiModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        TransaksiModel = new ArrayList<>();
        LinearLayoutManager y = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data2.setHasFixedSize(true);
        rv_data2.setLayoutManager(y);
        rv_data2.setNestedScrollingEnabled(true);

        LoadData();
        AksiTombol();
    }

    private void AksiTombol() {
        text_belum.setOnClickListener(v -> {
            TransaksiModel.clear();
            rv_data.setVisibility(View.VISIBLE);
            rv_data2.setVisibility(View.GONE);
            LoadData();
        });
        text_sudah.setOnClickListener(v -> {
            TransaksiModel.clear();
            rv_data.setVisibility(View.GONE);
            rv_data2.setVisibility(View.VISIBLE);
            LoadDataSudah();
        });
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminPenjualan.php")
                .addBodyParameter("tag", "list_sebelum")
                .addBodyParameter("iduser", MainActivity.iduser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                TransaksiModel bk = new TransaksiModel(
                                        responses.getString("idtransaksi"),
                                        responses.getString("nama"),
                                        responses.getString("total_tagihan"),
                                        responses.getString("invoice"));
                                TransaksiModel.add(bk);
                            }

                            KategoriAdapter adapter = new KategoriAdapter(getApplicationContext(), TransaksiModel);
                            rv_data.setAdapter(adapter);

                            ly_kosong.setVisibility(View.GONE);
                            ly_kosong2.setVisibility(View.GONE);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
//                                if (kode.equals("0")) {
//                                    //tidak ada data
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                } else if (kode.equals("1")) {
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                } else {
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                }
                            } catch (JSONException ignored) {
                            }
                            ly_kosong2.setVisibility(View.GONE);
                            ly_kosong.setVisibility(View.VISIBLE);
                            customProgress.hideProgress();
                        } else {
                            CustomDialog.errorDialog(PenjualanActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            customProgress.hideProgress();
                        }
                    }
                });
    }

    public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TransaksiModel> TransaksiModel;

        KategoriAdapter(Context mCtx, List<TransaksiModel> TransaksiModel) {
            this.mCtx = mCtx;
            this.TransaksiModel = TransaksiModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_penjualan, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final TransaksiModel tr = TransaksiModel.get(i);
            holder.text_nama.setText(tr.getNama_pelanggan());
            holder.text_total.setText(tr.getJumlah());

            holder.text_konfirmasi.setOnClickListener(v -> {
                KonfirmasiBarang(tr.getIdtransaksi());
            });
        }

        @Override
        public int getItemCount() {
            return TransaksiModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_total;
            TextView text_konfirmasi;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_total = itemView.findViewById(R.id.text_total);
                text_konfirmasi = itemView.findViewById(R.id.text_konfirmasi);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

    private void LoadDataSudah() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminPenjualan.php")
                .addBodyParameter("tag", "list_sesudah")
                .addBodyParameter("iduser", MainActivity.iduser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                TransaksiModel bk = new TransaksiModel(
                                        responses.getString("idtransaksi"),
                                        responses.getString("nama"),
                                        responses.getString("total_tagihan"),
                                        responses.getString("invoice"));
                                TransaksiModel.add(bk);
                            }

                            SesudahAdapter adapter = new SesudahAdapter(getApplicationContext(), TransaksiModel);
                            rv_data2.setAdapter(adapter);

                            ly_kosong.setVisibility(View.GONE);
                            ly_kosong2.setVisibility(View.GONE);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
//                                if (kode.equals("0")) {
//                                    //tidak ada data
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                } else if (kode.equals("1")) {
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                } else {
//                                    CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
//                                }
                            } catch (JSONException ignored) {
                            }
                            ly_kosong.setVisibility(View.GONE);
                            ly_kosong2.setVisibility(View.VISIBLE);
                            customProgress.hideProgress();
                        } else {
                            CustomDialog.errorDialog(PenjualanActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
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
        }

        @Override
        public int getItemCount() {
            return TransaksiModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_total;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_total = itemView.findViewById(R.id.text_total);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

    private void KonfirmasiBarang(String id) {
        AndroidNetworking.post(Connection.CONNECT + "AdminPenjualan.php")
                .addBodyParameter("tag", "konfirmasi")
                .addBodyParameter("idtransaksi", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successDialog(PenjualanActivity.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(PenjualanActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(PenjualanActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void successDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}