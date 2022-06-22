package com.skripsi.adminjilbabqu.model;

public class ProdukTransaksiModel {
    private String idtransaksi_detail;
    private String nama_produk;
    private String gambar;
    private String harga;
    private String diskon;
    private String jumlah_pembelian;
    private String subtotal_produk_format;
    private String deskripsi;

    public ProdukTransaksiModel(String idtransaksi_detail, String nama_produk, String gambar, String harga, String diskon, String jumlah_pembelian, String subtotal_produk_format, String deskripsi) {
        this.idtransaksi_detail = idtransaksi_detail;
        this.nama_produk = nama_produk;
        this.gambar = gambar;
        this.harga = harga;
        this.diskon = diskon;
        this.jumlah_pembelian = jumlah_pembelian;
        this.subtotal_produk_format = subtotal_produk_format;
        this.deskripsi = deskripsi;
    }

    public String getIdtransaksi_detail() {
        return idtransaksi_detail;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public String getGambar() {
        return gambar;
    }

    public String getHarga() {
        return harga;
    }

    public String getDiskon() {
        return diskon;
    }

    public String getJumlah_pembelian() {
        return jumlah_pembelian;
    }

    public String getSubtotal_produk_format() {
        return subtotal_produk_format;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
}