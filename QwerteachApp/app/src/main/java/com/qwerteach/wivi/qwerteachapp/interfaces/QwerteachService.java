package com.qwerteach.wivi.qwerteachapp.interfaces;

import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Lesson;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserBankAccount;
import com.qwerteach.wivi.qwerteachapp.models.UserWalletInfos;

import java.util.HashMap;
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
import retrofit2.http.Url;

/**
 * Created by wivi on 19/01/17.
 */

public interface QwerteachService {

    @GET("users/{id}")
    Call<JsonResponse> getUserInfos(@Path("id") int userId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("users/{id}")
    Call<JsonResponse> getStudentInfos(@Path("id") int userId, @Body Map<String, User> body, @Header("X-User-Email") String email,
                                       @Header("X-User-Token") String token);

    @Multipart
    @PATCH("users/{id}")
    Call<JsonResponse> uploadAvatar(@Path("id") int userId, @Part MultipartBody.Part file, @Header("X-User-Email") String email,
                                    @Header("X-User-Token") String token);

    @GET("users/find_level")
    Call<JsonResponse> getLevels();

    @GET("offers")
    Call<JsonResponse> getAdverts(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("offers/{id}")
    Call<JsonResponse> showAdvertInfos(@Path("id") int advertId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @DELETE("offers/{id}")
    Call<JsonResponse> deleteSmallAd(@Path("id") int advertId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PATCH("offers/{id}")
    Call<JsonResponse> updateSmallAd(@Path("id") int advertId, @Query("topic_id") int topicId, @Body Map<String, SmallAd> body,
                                     @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("topic_groups")
    Call<JsonResponse> getAllTopicGroups();

    @GET("topics")
    Call<JsonResponse> getAllTopics();

    @GET("topic_choice")
    Call<JsonResponse> getTopics(@Query("group_id") int topicGroupId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("level_choice")
    Call<JsonResponse> getLevels(@Query("topic_id") int topicId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("offers")
    Call<JsonResponse> createNewAdvert(@Body Map<String, SmallAd> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("get_infos_for_detailed_prices_modal")
    Call<JsonResponse> getInfosForDetailedPrices(@Query("id") int advertId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("users/{user_id}/lesson_requests/topic_groups")
    Call<JsonResponse> getTeacherTopicGroups(@Path("user_id") int teacherId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("users/{user_id}/lesson_requests/topics/{topic_group_id}")
    Call<JsonResponse> getTeacherTopics(@Path("user_id") int teacherId, @Path("topic_group_id") int topicGroupId,
                                        @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("users/{user_id}/lesson_requests/levels/{topic_id}")
    Call<JsonResponse> getTeacherLevels(@Path("user_id") int teacherId, @Path("topic_id") int topicId,
                                        @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("users/{user_id}/lesson_requests")
    Call<JsonResponse> createNewLesson(@Path("user_id") int teacherId, @Body Map<String, Lesson> body,
                                       @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("wallets/get_total_wallet/{user_id}")
    Call<JsonResponse> getTotalWallet(@Path("user_id") int userId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("profs")
    Call<JsonResponse> getSearchResults(@Query("topic") String topic, @Query("search_sorting") String searchSortingOption, @Query("page") int pageNumber,
                                        @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("messages")
    Call<JsonResponse> sendMessageToTeacher(@Body Map<String, Message> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("users/{user_id}/lesson_requests/payment")
    Call<JsonResponse> payLesson(@Path("user_id") int teacherId, @Query("mode") String paymentmode,
                                 @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("users/{user_id}/lesson_requests/payment")
    Call<JsonResponse> payLessonWithCreditCard(@Path("user_id") int teacherId, @Query("mode") String paymentmode, @Query("card_id") String cardId,
                                               @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET
    Call<JsonResponse> finalizePaymentWithCard(@Url String url, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("user/mangopay/index_wallet")
    Call<JsonResponse> getAllWallletInfos(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("user/mangopay/card_info")
    Call<JsonResponse> getPreRegistrationCardData(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("user/mangopay/update_bank_accounts")
    Call<JsonResponse> addNewBankAccount(@Body Map<String, UserBankAccount> data, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("user/mangopay/desactivate_bank_account/{id}")
    Call<JsonResponse> desactivateBankAccount(@Path("id") String bankAccountId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("user/mangopay/edit_wallet")
    Call<JsonResponse> updateUserWallet(@Body Map<String, UserWalletInfos> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("user/mangopay/direct_debit")
    Call<JsonResponse> loadUserWallet(@Body Map<String, String> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("user/mangopay/make_payout")
    Call<JsonResponse> makePayout(@Body Map<String, String> body, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons")
    Call<JsonResponse> getLessons(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/find_lesson_informations/{lesson_id}")
    Call<JsonResponse> getLessonInfos(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/index_pagination")
    Call<JsonResponse> getMoreHistoryLessons(@Query("lesson_type") String lessonType, @Query("page") int pageNumber,
                                             @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/{lesson_id}/cancel")
    Call<JsonResponse> cancelLesson(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/{lesson_id}/accept")
    Call<JsonResponse> acceptLesson(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/{lesson_id}/refuse")
    Call<JsonResponse> refuseLesson(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/{lesson_id}/pay_teacher")
    Call<JsonResponse> payTeacher(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("lessons/{lesson_id}/dispute")
    Call<JsonResponse> disputeLesson(@Path("lesson_id") int lessonId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("users/{teacher_id}/reviews")
    Call<JsonResponse> letReviewToTeacher(@Path("teacher_id") int teacherId, @Body Map<String, Review> body,
                                          @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("dashboard")
    Call<JsonResponse> getDashboardInfos(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("conversations/{id}/reply")
    Call<JsonResponse> reply(@Path("id") int conversationId, @Body Map<String, String> body,
                             @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("mailbox/inbox")
    Call<JsonResponse> getConversations(@Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @PUT("lessons/{id}")
    Call<JsonResponse> updateLesson(@Path("id") int lessonId, @Body Map<String, Lesson> body,
                                    @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("sessions")
    Call<JsonResponse> signIn(@Body Map<String, HashMap<String, String>> body);

    @POST("registrations")
    Call<JsonResponse> signUpWithEmail(@Body Map<String, HashMap<String, String>> body);

    @GET("conversation/show_more/{id}/{page}")
    Call<JsonResponse> getMoreMessages(@Path("id") int conversationId, @Path("page") int pageNumber,
                                       @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("conversations/{id}")
    Call<JsonResponse> showMessages(@Path("id") int conversationId, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @POST("users/{user_id}/lesson_requests/calculate")
    Call<JsonResponse> calculateUserLessonRequest(@Path("user_id") int teacherId, @Body Map<String, String> body,
                                                  @Header("X-User-Email") String email, @Header("X-User-Token") String token);

    @GET("user/mangopay/transactions_index")
    Call<JsonResponse> getMoreTransactions(@Query("page") int pageNumber, @Header("X-User-Email") String email, @Header("X-User-Token") String token);

}
