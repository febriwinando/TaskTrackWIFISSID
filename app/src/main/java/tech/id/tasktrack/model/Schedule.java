package tech.id.tasktrack.model;

public class Schedule {

    public int id;
    public String tanggal;
    public String keterangan;

    public Pegawai pegawai;
    public Kegiatan kegiatan;
    public Lokasi lokasi;

    public int created_by;
    public String created_ip;
    public int updated_by;
    public String updated_ip;
    public int verifikator_id;
    public String verifikasi_pegawai;
    public String verifikasi_verifikator;

}

