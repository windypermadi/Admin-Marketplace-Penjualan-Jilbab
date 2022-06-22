package com.skripsi.adminjilbabqu.menu.transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.skripsi.adminjilbabqu.MainActivity;
import com.skripsi.adminjilbabqu.R;
import com.skripsi.adminjilbabqu.helper.Connection;
import com.skripsi.adminjilbabqu.helper.utils.CekKoneksi;
import com.skripsi.adminjilbabqu.helper.utils.CustomDialog;
import com.skripsi.adminjilbabqu.helper.utils.CustomProgressbar;
import com.skripsi.adminjilbabqu.menu.PenjualanActivity;
import com.skripsi.adminjilbabqu.model.ProdukTransaksiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailTransaksi extends AppCompatActivity {
    String idtransaksi;
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private RelativeLayout tombol_footer;
    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_produk;
    List<ProdukTransaksiModel> ProdukTransaksiModel;
    private SwipeRefreshLayout swipe_refresh;
    String sub_total, sub_total_format;
    private TextView text_konfirmasi;
    String kode, invoice, nama, tanggal_transaksi;

    private TextView text_invoice, text_pelanggan, text_total_bayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        Intent i = getIntent();
        idtransaksi = i.getStringExtra("idtransaksi");

        ly11 = findViewById(R.id.ly11);
        ly22 = findViewById(R.id.ly22);
        ly00 = findViewById(R.id.ly00);
        rv_produk = findViewById(R.id.rv_produk);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        tombol_footer = findViewById(R.id.tombol_footer);
        text_konfirmasi = findViewById(R.id.text_konfirmasi);

        text_invoice = findViewById(R.id.text_invoice);
        text_pelanggan = findViewById(R.id.text_pelanggan);
        text_total_bayar = findViewById(R.id.text_total_bayar);

        ProdukTransaksiModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_produk.setHasFixedSize(true);
        rv_produk.setLayoutManager(x);
        rv_produk.setNestedScrollingEnabled(true);

        ActionButton();
    }

    @Override
    protected void onResume() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        ly22.setVisibility(View.GONE);
        ProdukTransaksiModel.clear();
        LoadData();
        super.onResume();
    }

    private void ActionButton() {
        text_konfirmasi.setOnClickListener(v -> {
            KonfirmasiBarang(idtransaksi);
        });
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            ly22.setVisibility(View.GONE);
            ProdukTransaksiModel.clear();
            LoadData();
        });
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
                        KonfirmasisuccessDialog(DetailTransaksi.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(DetailTransaksi.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(DetailTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void KonfirmasisuccessDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            startActivity(new Intent(DetailTransaksi.this, MainActivity.class));
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminPenjualan.php")
                .addBodyParameter("tag", "transaksi_detail")
                .addBodyParameter("idtransaksi", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject responses = response.optJSONObject("data_transaksi");
                        sub_total_format = responses.optString("sub_total_format");
                        sub_total = responses.optString("sub_total");
                        invoice = responses.optString("invoice");
                        nama = responses.optString("nama");
                        tanggal_transaksi = responses.optString("tanggal_transaksi");

                        text_invoice.setText(invoice);
                        text_pelanggan.setText(nama);
                        text_total_bayar.setText(sub_total_format);

                        try {
                            JSONArray jsonArray = response.optJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject res = jsonArray.getJSONObject(i);
                                ProdukTransaksiModel bk = new ProdukTransaksiModel(
                                        res.getString("idtransaksi_detail"),
                                        res.getString("nama_produk"),
                                        res.getString("gambar"),
                                        res.getString("harga"),
                                        res.getString("diskon"),
                                        res.getString("jumlah_pembelian"),
                                        res.getString("subtotal_produk_format"),
                                        res.getString("deskripsi"));
                                ProdukTransaksiModel.add(bk);
                            }
                            KeranjangAdapter adapter = new KeranjangAdapter(getApplicationContext(), ProdukTransaksiModel);
                            rv_produk.setAdapter(adapter);

                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.VISIBLE);
                            ly22.setVisibility(View.GONE);
                            tombol_footer.setVisibility(View.VISIBLE);

                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(View.GONE);
                            ly00.setVisibility(View.GONE);
                            ly22.setVisibility(View.GONE);
                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
                                if (kode.equals("0")) {
                                    //tidak ada data
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    tombol_footer.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(DetailTransaksi.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    //mencapai batas limit
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.VISIBLE);
                                    ly22.setVisibility(View.GONE);
                                    tombol_footer.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(DetailTransaksi.this, body.optString("pesan"));
                                } else {
                                    //2 tiket dibatalkan
                                    customProgress.hideProgress();
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    tombol_footer.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(DetailTransaksi.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(DetailTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<ProdukTransaksiModel> ProdukTransaksiModel;

        KeranjangAdapter(Context mCtx, List<ProdukTransaksiModel> ProdukTransaksiModel) {
            this.mCtx = mCtx;
            this.ProdukTransaksiModel = ProdukTransaksiModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_keranjang_act, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final ProdukTransaksiModel keranjang = ProdukTransaksiModel.get(i);
            holder.text_nama.setText(keranjang.getNama_produk());
            holder.text_harga.setText(keranjang.getHarga());
            holder.text_jumlah.setText(keranjang.getSubtotal_produk_format());
            holder.text_total.setText(keranjang.getJumlah_pembelian());
            holder.text_catatan.setText(keranjang.getDeskripsi());
            Glide.with(getApplicationContext())
                    .load(keranjang.getGambar())
                    .error(R.color.colorGrey2)
                    .into(holder.img);
        }

        @Override
        public int getItemCount() {
            return ProdukTransaksiModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_harga, text_total, text_jumlah, text_catatan;
            ImageView img;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_harga = itemView.findViewById(R.id.text_harga);
                text_total = itemView.findViewById(R.id.text_total);
                text_jumlah = itemView.findViewById(R.id.text_jumlah);
                img = itemView.findViewById(R.id.img);
                cv = itemView.findViewById(R.id.cv);
                text_catatan = itemView.findViewById(R.id.text_catatan);
            }
        }
    }

    public void successDialog(final Context context, final String alertText) {
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            onResume();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}