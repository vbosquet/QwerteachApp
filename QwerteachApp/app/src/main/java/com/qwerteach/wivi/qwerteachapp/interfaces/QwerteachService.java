package com.qwerteach.wivi.qwerteachapp.interfaces;

import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserJson;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by wivi on 19/01/17.
 */

public interface QwerteachService {

    @GET("users/{id}")
    Call<JsonResponse> getStudentId(@Path("id") String userId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("users/{id}")
    Call<JsonResponse> getStudentInfos(@Path("id") String userId, @Body Map<String, User> body, @Header("X-User-Email") String email,
                                       @Header("X-User-Token") String token);

    @Multipart
    @PUT("users/{id}")
    Call<JsonResponse> uploadAvatar(@Path("id") String userId, @Part MultipartBody.Part file, @Header("X-User-Email") String email,
                                    @Header("X-User-Token") String token);

    @GET("profiles/find_level")
    Call<JsonResponse> getLevels();

    @GET("adverts")
    Call<JsonResponse> getAdverts(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("adverts/show/{id}")
    Call<JsonResponse> showAdvertInfos(@Path("id") int advertId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @DELETE("adverts/{id}")
    Call<JsonResponse> deleteSmallAd(@Path("id") int advertId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PATCH("adverts/{id}")
    Call<JsonResponse> updateSmallAd(@Path("id") int advertId, @Query("topic_id") int topicId, @Body Map<String, SmallAd> body,
                                     @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("topic_groups")
    Call<JsonResponse> getTopicGroups();

    @GET("topic_choice")
    Call<JsonResponse> getTopics(@Query("group_id") int topicGroupId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("level_choice")
    Call<JsonResponse> getLevels(@Query("topic_id") int topicId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("adverts")
    Call<JsonResponse> createNewAdvert(@Body Map<String, SmallAd> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

}