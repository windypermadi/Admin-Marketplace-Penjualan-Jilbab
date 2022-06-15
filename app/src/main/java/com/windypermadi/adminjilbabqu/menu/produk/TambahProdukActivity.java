package com.windypermadi.adminjilbabqu.menu.produk;

import static com.windypermadi.adminjilbabqu.helper.utils.CustomProgressbar.customProgress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.windypermadi.adminjilbabqu.R;
import com.windypermadi.adminjilbabqu.helper.Connection;
import com.windypermadi.adminjilbabqu.helper.utils.CustomDialog;
import com.windypermadi.adminjilbabqu.menu.ProdukActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TambahProdukActivity extends AppCompatActivity implements TextWatcher {
    private EditText et_idkategori, et_kategori, et_nama, et_harga, et_jumlah ,
            et_gambar, et_status, et_diskon;
    private TextView text_simpan;
    private ImageView btn_upload, img_upload;
    ArrayList<HashMap<String, String>> dataKategori = new ArrayList<>();
    String idkategori, status_diskon;
    //upload
    Uri FilePath;
    String gambar = "kosong";
    Intent intent;
    Bitmap bitmap;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    // number format
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    public TambahProdukActivity() {
    }

    public TambahProdukActivity(EditText et_harga) {
        df = new DecimalFormat("#,###.##");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###");
        this.et_harga = et_harga;
        hasFractionalPart = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        hasFractionalPart = s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
    }

    @Override
    public void afterTextChanged(Editable s) {
        et_harga.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et_harga.getText().length();
            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);

            int cp = et_harga.getSelectionStart();
            if (hasFractionalPart) {
                et_harga.setText(df.format(n));
            } else {
                et_harga.setText(dfnd.format(n));
            }
            endlen = et_harga.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et_harga.getText().length()) {
                et_harga.setSelection(sel);
            } else {
                // place cursor at the end?
                et_harga.setSelection(et_harga.getText().length() - 1);
            }

        } catch (NumberFormatException | ParseException nfe) {
            // do nothing?
        }

        et_harga.addTextChangedListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        et_idkategori   = findViewById(R.id.et_idkategori);
        et_kategori     = findViewById(R.id.et_kategori);
        et_nama         = findViewById(R.id.et_nama);
        et_harga        = findViewById(R.id.et_harga);
        et_jumlah       = findViewById(R.id.et_jumlah);
        et_gambar       = findViewById(R.id.et_gambar);
        et_status       = findViewById(R.id.et_status);
        et_diskon       = findViewById(R.id.et_diskon);
        text_simpan     = findViewById(R.id.text_simpan);
        btn_upload      = findViewById(R.id.btn_upload);
        img_upload      = findViewById(R.id.img_upload);
        et_harga.addTextChangedListener(new TambahProdukActivity(et_harga));
        AksiTombol();
        loadKategori();
    }

    private void AksiTombol() {
        et_kategori.setOnClickListener(v -> poup_kategori());
        btn_upload.setOnClickListener(v -> {
            if (checkPermission()) {
                selectImage();
            } else {
                requestPermission();
            }
        });
        et_status.setOnClickListener(v -> {
            PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), et_status);
            dropDownMenu.getMenuInflater().inflate(R.menu.status_barang_diskon, dropDownMenu.getMenu());
            dropDownMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.ya) {
                    status_diskon = "Y";
                    et_status.setText("Ada Diskon");
                    et_diskon.setVisibility(View.VISIBLE);
                } else {
                    status_diskon = "N";
                    et_status.setText("Tidak Ada Diskon");
                    et_diskon.setVisibility(View.GONE);
                }
                return true;
            });
            dropDownMenu.show();
        });
        text_simpan.setOnClickListener(v -> {
            String idkategori = et_idkategori.getText().toString().trim();
            String kategori = et_kategori.getText().toString().trim();
            String nama = et_nama.getText().toString().trim();
            String harga = et_harga.getText().toString().trim().replaceAll("[^\\d]", "");
            String jumlah = et_jumlah.getText().toString().trim();
            String status_diskon = et_status.getText().toString().trim();
            String diskon = et_diskon.getText().toString().trim();

            if (!idkategori.isEmpty() && !kategori.isEmpty() && !nama.isEmpty() && !harga.isEmpty()
            && !jumlah.isEmpty()){
                if (status_diskon.equals("Y")){
                    if (!diskon.isEmpty()){
                        if (gambar.equals("isi")) {
                            File file = new File(getRealPathFromURI(FilePath));
                            customProgress.showProgress(this, false);
                            tambahdata(file);
                        } else {
                            customProgress.showProgress(this, false);
                            tambahdata(new File(""));
                        }
                    } else {
                        CustomDialog.errorDialog(TambahProdukActivity.this, "Jumlah diskon belum terisi");
                    }
                } else {
                    if (gambar.equals("isi")) {
                        File file = new File(getRealPathFromURI(FilePath));
                        customProgress.showProgress(this, false);
                        tambahdata(file);
                    } else {
                        customProgress.showProgress(this, false);
                        tambahdata(new File(""));
                    }
                }
            } else {
                CustomDialog.errorDialog(TambahProdukActivity.this, "Data harus lengkap tidak boleh ada data yang kosong");
            }
        });
    }

    private void loadKategori() {
        dataKategori.clear();
        AndroidNetworking.post(Connection.CONNECT + "AdminKategori.php")
                .addBodyParameter("tag", "list")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("idkategori", responses.optString("idkategori"));
                                map.put("nama_kategori", responses.optString("nama_kategori"));

                                dataKategori.add(map);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        customProgress.hideProgress();
                    }
                });
    }

    private void poup_kategori() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahProdukActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataKategori, R.layout.custom_list_kategori,
                new String[]{"idkategori", "nama_kategori"},
                new int[]{R.id.text_id, R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            idkategori = ((TextView) view.findViewById(R.id.text_id)).getText().toString();
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_kategori.setText(nama_kategori);
            et_idkategori.setText(idkategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    //upload foto
    private void selectImage() {
        img_upload.setImageResource(0);
        final CharSequence[] items = {"Ambil foto", "Pilih dari galeri",
                "Batal"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TambahProdukActivity.this);
        builder.setTitle("Upload bukti transaksi");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Ambil foto")) {
                //intent khusus untuk menangkap foto lewat kamera
                gambar = "isi";
                if (ContextCompat.checkSelfPermission(TambahProdukActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(TambahProdukActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            } else if (items[item].equals("Pilih dari galeri")) {
                gambar = "isi";
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), SELECT_FILE);
            } else if (items[item].equals("Batal")) {
                gambar = "kosong";
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(TambahProdukActivity.this, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(TambahProdukActivity.this.getContentResolver(), data.getData());
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(TambahProdukActivity.this, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void tambahdata(File file) {
        if (file.length() == 0) {
            AndroidNetworking.upload(Connection.CONNECT + "AdminProduk.php")
                    .addMultipartParameter("tag", "add")
                    .addMultipartParameter("nama_produk", et_nama.getText().toString().trim())
                    .addMultipartParameter("harga", et_harga.getText().toString().trim().replaceAll("[^\\d]", ""))
                    .addMultipartParameter("status_diskon", status_diskon)
                    .addMultipartParameter("diskon", et_diskon.getText().toString().trim())
                    .addMultipartParameter("stok", et_jumlah.getText().toString().trim())
                    .addMultipartParameter("idkategori", idkategori)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            successDialog(TambahProdukActivity.this, response.optString("pesan"));
                            customProgress.hideProgress();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("data1", "onError errorCode : " + error.getErrorCode());
                            Log.d("data1", "onError errorBody : " + error.getErrorBody());
                            Log.d("data1", "onError errorDetail : " + error.getErrorDetail());
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    CustomDialog.errorDialog(TambahProdukActivity.this, body.optString("pesan"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                CustomDialog.errorDialog(TambahProdukActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        } else {
            AndroidNetworking.upload(Connection.CONNECT + "AdminProduk.php")
                    .addMultipartParameter("tag", "add")
                    .addMultipartParameter("nama_produk", et_nama.getText().toString().trim())
                    .addMultipartParameter("harga", et_harga.getText().toString().trim().replaceAll("[^\\d]", ""))
                    .addMultipartParameter("status_diskon", status_diskon)
                    .addMultipartParameter("diskon", et_diskon.getText().toString().trim())
                    .addMultipartParameter("stok", et_jumlah.getText().toString().trim())
                    .addMultipartParameter("idkategori", idkategori)
                    .addMultipartFile("uploadedfile", file)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            successDialog(TambahProdukActivity.this, response.optString("pesan"));
                            customProgress.hideProgress();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("data1", "onError errorCode : " + error.getErrorCode());
                            Log.d("data1", "onError errorBody : " + error.getErrorBody());
                            Log.d("data1", "onError errorDetail : " + error.getErrorDetail());
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    CustomDialog.errorDialog(TambahProdukActivity.this, body.optString("pesan"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                CustomDialog.errorDialog(TambahProdukActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(TambahProdukActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TambahProdukActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(TambahProdukActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(TambahProdukActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
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