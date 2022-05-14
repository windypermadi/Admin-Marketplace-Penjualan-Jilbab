package com.windypermadi.adminjilbabqu.model;

public class ProdukModel {
    private String idproduk;
    private String nama_produk;
    private String gambar;
    private String harga_format;
    private String diskon;
    private String harga_diskon_format;
    private String stok;
    private String status_diskon;

    public ProdukModel(String idproduk, String nama_produk, String gambar, String harga_format, String diskon, String harga_diskon_format, String stok, String status_diskon) {
        this.idproduk = idproduk;
        this.nama_produk = nama_produk;
        this.gambar = gambar;
        this.harga_format = harga_format;
        this.diskon = diskon;
        this.harga_diskon_format = harga_diskon_format;
        this.stok = stok;
        this.status_diskon = status_diskon;
    }

    public String getIdproduk() {
        return idproduk;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public String getGambar() {
        return gambar;
    }

    public String getHarga_format() {
        return harga_format;
    }

    public String getDiskon() {
        return diskon;
    }

    public String getHarga_diskon_format() {
        return harga_diskon_format;
    }

    public String getStok() {
        return stok;
    }

    public String getStatus_diskon() {
        return status_diskon;
    }
}
