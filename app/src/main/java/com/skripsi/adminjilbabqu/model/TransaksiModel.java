package com.skripsi.adminjilbabqu.model;

public class TransaksiModel {
    private String idtransaksi;
    private String nama_pelanggan;
    private String jumlah;
    private String status;

    public TransaksiModel(String idtransaksi, String nama_pelanggan, String jumlah, String status) {
        this.idtransaksi = idtransaksi;
        this.nama_pelanggan = nama_pelanggan;
        this.jumlah = jumlah;
        this.status = status;
    }

    public String getIdtransaksi() {
        return idtransaksi;
    }

    public String getNama_pelanggan() {
        return nama_pelanggan;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getStatus() {
        return status;
    }
}
