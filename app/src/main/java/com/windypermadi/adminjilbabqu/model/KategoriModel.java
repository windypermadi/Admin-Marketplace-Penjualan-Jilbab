package com.windypermadi.adminjilbabqu.model;

public class KategoriModel {
    private String idkategori;
    private String nama_kategori;

    public KategoriModel(String idkategori, String nama_kategori) {
        this.idkategori = idkategori;
        this.nama_kategori = nama_kategori;
    }

    public String getIdkategori() {
        return idkategori;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }
}
