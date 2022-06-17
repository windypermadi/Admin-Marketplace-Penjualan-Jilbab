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
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.skripsi.adminjilbabqu.R;
import com.skripsi.adminjilbabqu.helper.Connection;
import com.skripsi.adminjilbabqu.helper.utils.CustomDialog;
import com.skripsi.adminjilbabqu.menu.produk.DetailProdukActivity;
import com.skripsi.adminjilbabqu.model.ProdukModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProdukActivity extends AppCompatActivity {
    private RecyclerView rv_data;
    List<ProdukModel> ProdukModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);

        rv_data = findViewById(R.id.rv_data);

        ProdukModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);
    }

    @Override
    protected void onResume() {
        ProdukModel.clear();
        LoadData();
        super.onResume();
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminProduk.php")
                .addBodyParameter("tag", "list")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                ProdukModel bk = new ProdukModel(
                                        responses.getString("idproduk"),
                                        responses.getString("nama_produk"),
                                        responses.getString("gambar"),
                                        responses.getString("harga_format"),
                                        responses.getString("diskon"),
                                        responses.getString("harga_diskon_format"),
                                        responses.getString("stok"),
                                        responses.getString("status_diskon"));
                                ProdukModel.add(bk);
                            }

                            KategoriAdapter adapter = new KategoriAdapter(getApplicationContext(), ProdukModel);
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
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
                                if (kode.equals("0")) {
                                    //tidak ada data
                                    CustomDialog.errorDialog(ProdukActivity.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    CustomDialog.errorDialog(ProdukActivity.this, body.optString("pesan"));
                                } else {
                                    CustomDialog.errorDialog(ProdukActivity.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ProdukActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<ProdukModel> ProdukModel;

        KategoriAdapter(Context mCtx, List<ProdukModel> ProdukModel) {
            this.mCtx = mCtx;
            this.ProdukModel = ProdukModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_produk, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final ProdukModel produk = ProdukModel.get(i);
            holder.text_nama.setText(produk.getNama_produk());
            holder.text_harga.setText(produk.getHarga_format());
            holder.text_stok.setText(produk.getStok());
            Glide.with(getApplicationContext())
                    .load(produk.getGambar())
                    .error(R.color.colorGrey2)
                    .into(holder.img);

            holder.cv.setOnClickListener(v -> {
                Intent x = new Intent(mCtx, DetailProdukActivity.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                x.putExtra("idproduk", produk.getIdproduk());
                mCtx.startActivity(x);
            });
        }

        @Override
        public int getItemCount() {
            return ProdukModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_harga, text_stok;
            ImageView img;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                cv = itemView.findViewById(R.id.cv);
                text_harga = itemView.findViewById(R.id.text_harga);
                text_stok = itemView.findViewById(R.id.text_stok);
                img = itemView.findViewById(R.id.img);
            }
        }
    }
}