package tech.id.tasktrack.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.id.tasktrack.model.LoginRequest;
import tech.id.tasktrack.model.LoginResponse;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.model.ScheduleResponse;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("pegawai")
    Call<List<Pegawai>> getPegawai(@Header("Authorization") String token);

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String token);

    @GET("schedule/pegawai/{id}")
    Call<ScheduleResponse> getScheduleByPegawai(
            @Header("Authorization") String token,
            @Path("id") int pegawaiId
    );

    @GET("schedule/pegawai/{id}/bulan")
    Call<ScheduleResponse> getScheduleByMonth(
            @Header("Authorization") String token,
            @Path("id") int pegawaiId,
            @Query("bulan") int bulan,
            @Query("tahun") int tahun
    );


}
