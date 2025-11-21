package tech.id.tasktrack.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.id.tasktrack.model.ApiResponse;
import tech.id.tasktrack.model.LoginRequest;
import tech.id.tasktrack.model.LoginResponse;
import tech.id.tasktrack.model.Pegawai;
import tech.id.tasktrack.model.PegawaiResponse;
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

    @FormUrlEncoded
    @POST("schedule/update-verifikasi")
    Call<ApiResponse> updateVerifikasiPegawai(
            @Header("Authorization") String token,
            @Field("id") int id,
            @Field("pegawai_id") int pegawaiId,
            @Field("tanggal") String tanggal,
            @Field("verifikasi_pegawai") String verifikasi_pegawai
    );

    @GET("daftar_schedule/{id}")
    Call<ScheduleResponse> listScheduleEmployees(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Query("tanggal") String tanggal
    );
    @GET("pegawais")
    Call<PegawaiResponse> pegawais(@Header("Authorization") String token, @Field("id") int id);



}
