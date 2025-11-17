package tech.id.tasktrack.model;

public class Pegawai {
    public int id;
    public String name;
    public String nik;
    public String employee_id;
    public String email;
    public String nomor_wa;
    public String level;
    public String status;
    public String inactive_reason;
    public String foto;

    public String getInactive_reason() {
        return inactive_reason;
    }

    public void setInactive_reason(String inactive_reason) {
        this.inactive_reason = inactive_reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getNomor_wa() {
        return nomor_wa;
    }

    public void setNomor_wa(String nomor_wa) {
        this.nomor_wa = nomor_wa;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
