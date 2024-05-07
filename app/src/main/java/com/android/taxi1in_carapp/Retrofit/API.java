package com.android.taxi1in_carapp.Retrofit;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface API {


    //Call<User> getUser(@Header("Authorization") String authorization)

    @POST("login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("phone") String phoneNo,
                             @Field("password") String password);

    @POST("register")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("firstName") String firstName,
                                @Field("lastName") String lastName,
                                @Field("email") String email,
                                @Field("phone") String phone,
                                @Field("driverId") String driverId,
                                @Field("plateNumber") String plateNumber,
                                @Field("password") String password,
                                @Field("confirmPassword") String confirmPassword);

    @POST("forgotPassword")
    @FormUrlEncoded
    Call<ResponseBody> forgotPassword(@Field("email") String email);

    @POST("verifyemailotp")
    @FormUrlEncoded
    Call<ResponseBody> verifyEmailOtp(@Field("email") String email, @Field("otp") String otp);

    @POST("resetPassword")
    @FormUrlEncoded
    Call<ResponseBody> resetPassword(@Field("email") String email, @Field("new_password") String newPassword, @Field("confirm_password") String confirmPassword);

    @GET("getProfile")
    Call<ResponseBody> getProfile(@Query("id") String id, @Query("token") String token);

    @POST("updateProfile")
    @Multipart
    Call<ResponseBody> updateProfile(@Part("id") RequestBody id,
                                     @Part("token") RequestBody token,
                                     @Part("firstName") RequestBody firstName,
                                     @Part("lastName") RequestBody lastName,
                                     @Part("phone") RequestBody phone,
                                     @Part("email") RequestBody email,
                                     @Part MultipartBody.Part profileImage);

    @POST("addVehical")
    @FormUrlEncoded
    Call<ResponseBody> addVehical(@Field("id") String id, @Field("token") String token, @Field("plateNumber") String plateNumber);

    @POST("changeVehical")
    @FormUrlEncoded
    Call<ResponseBody> changeVehicle(@Field("id") String id, @Field("token") String token, @Field("vehicalId") String vehicleId);

    @POST("deleteVehical")
    @FormUrlEncoded
    Call<ResponseBody> deleteVehicle(@Field("id") String id, @Field("token") String token, @Field("vehicalId") String vehicleId);

    @POST("ChangePassword")
    @FormUrlEncoded
    Call<ResponseBody> changePassword(@Field("id") String userid,
                                      @Field("token") String token,
                                      @Field("old_password") String oldPassword,
                                      @Field("new_password") String newPassword);

    @GET("getDefaultNumber")
    Call<ResponseBody> getDefaultNumber(@Query("id") String id, @Query("token") String token);

    @POST("startSosCall")
    @FormUrlEncoded
    Call<ResponseBody> callStart(@Field("id") String id, @Field("token") String token, @Field("number") String number);


    @POST("storeAddress")
    @FormUrlEncoded
    Call<ResponseBody> storeAddress(@Field("id") String userid,
                                    @Field("token") String token,
                                    @Field("address") String address,
                                    @Field("latitude") String latitude,
                                    @Field("longitude") String longitude,
                                    @Field("call_id") String callid);

    @POST("endSosCall")
    @FormUrlEncoded
    Call<ResponseBody> callStop(@Field("id") String userid,
                                @Field("token") String token,
                                @Field("call_id") String callid);

    @POST("tripStart")
    @FormUrlEncoded
    Call<ResponseBody> tripStartContinueStop(@Field("userId") int userid,
                                             @Field("userToken") String token,
                                             @Field("tripId") int tripId,
                                             @Field("carType") String carType,
                                             @Field("address") String address,
                                             @Field("latitude") double latitude,
                                             @Field("longitude") double longitude,
                                             @Field("speed") int speed,
                                             @Field("tripStatus") String tripStatus,
                                             @Field("additionalCharge") double additionalCharge,
                                             @Field("dateTime") String dateTime,
                                             @Field("platNumber") String platNumber,
                                             @Field("driverId") String driverId,
                                             @Field("isDistance") Boolean isDistance);

    @GET("getTripRecord")
    Call<ResponseBody> allTripRecords(@Query("userId") int userId, @Query("userToken") String userToken);

    @POST("deleteTrip")
    @FormUrlEncoded
    Call<ResponseBody> deleteTripRecords(@Field("userId") int userId, @Field("userToken") String userToken, @Field("tripId") String tripId);

    @POST("logout")
    @FormUrlEncoded
    Call<ResponseBody> logout(@Field("email") String email);

    @GET("getActiveTrip")
    Call<ResponseBody> getActiveTrip(@Query("userId") int userId, @Query("userToken") String userToken);
}
