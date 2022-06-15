package com.windypermadi.adminjilbabqu.menu;

import static com.windypermadi.adminjilbabqu.helper.utils.CustomProgressbar.customProgress;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.windypermadi.adminjilbabqu.MainActivity;
import com.windypermadi.adminjilbabqu.R;
import com.windypermadi.adminjilbabqu.helper.Connection;
import com.windypermadi.adminjilbabqu.helper.utils.CustomDialog;
import com.windypermadi.adminjilbabqu.model.TransaksiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LaporanActivity extends AppCompatActivity {
    private EditText et_total, et_mulai, et_akhir;
    private TextView text_simpan;
    String tanggal_awal, tanggal_akhir;
    String total_tagihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        et_total = findViewById(R.id.et_total);
        et_mulai = findViewById(R.id.et_mulai);
        et_akhir = findViewById(R.id.et_akhir);
        text_simpan = findViewById(R.id.text_simpan);

        AksiTombol();
    }

    private void AksiTombol() {
        et_mulai.setOnClickListener(v -> filterTanggal());
        text_simpan.setOnClickListener(v -> {
            LoadData();
        });
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.post(Connection.CONNECT + "AdminLaporan.php")
                .addBodyParameter("tanggal_awal", tanggal_awal)
                .addBodyParameter("tanggal_akhir", tanggal_akhir)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                total_tagihan = responses.optString("total_tagihan_format");
                            }

                            et_total.setText(total_tagihan);
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

    private void filterTanggal() {
        SmoothDateRangePickerFragment smoothDateRangePickerFragment =
                SmoothDateRangePickerFragment
                        .newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                       int yearStart, int monthStart,
                                                       int dayStart, int yearEnd,
                                                       int monthEnd, int dayEnd) {
                                String date = "You picked the following date range: \n"
                                        + "From " + dayStart + "/" + (++monthStart)
                                        + "/" + yearStart + " To " + dayEnd + "/"
                                        + (++monthEnd) + "/" + yearEnd;

                                tanggal_awal = yearStart + "-" + monthStart + "-" + dayStart;
                                tanggal_akhir = yearEnd + "-" + monthEnd + "-" + dayEnd;
                                et_mulai.setText(yearStart + "-" + monthStart + "-" + dayStart);
                                et_akhir.setText(yearEnd + "-" + monthEnd + "-" + dayEnd);
                            }
                        });
        smoothDateRangePickerFragment.show(getFragmentManager(), "Datepickerdialog");
    }
}