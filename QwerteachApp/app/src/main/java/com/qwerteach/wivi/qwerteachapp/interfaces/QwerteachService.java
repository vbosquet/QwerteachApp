package com.qwerteach.wivi.qwerteachapp.interfaces;

import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.UserJson;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by wivi on 19/01/17.
 */

public interface QwerteachService {

    @GET("users/{id}")
    Call<JsonResponse> getStudentId(@Path("id") String userId,
                                    @Header("X-User-Email") String email,
                                    @Header("X-User-Token") String token);

    @PUT("users/{id}")
    Call<JsonResponse> getStudentInfos(@Path("id") String userId,
                                       @Body UserJson userJson,
                                       @Header("X-User-Email") String email,
                                       @Header("X-User-Token") String token);

    @Multipart
    @PUT("users/{id}")
    Call<JsonResponse> uploadAvatar(@Path("id") String userId,
                                    @Part MultipartBody.Part file,
                                    @Header("X-User-Email") String email,
                                    @Header("X-User-Token") String token);

}
