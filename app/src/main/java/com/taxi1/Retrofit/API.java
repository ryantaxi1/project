package com.taxi1.Retrofit;

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

    String URLPrefix = "api/";

    //Call<User> getUser(@Header("Authorization") String authorization)

    @POST(URLPrefix + "register")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("username") String username,
                                @Field("email") String email,
                                @Field("phone") String phone,
                                @Field("driver_id") String driver_id,
                                @Field("plate_number") String platenumber,
                                @Field("password") String password);

    @POST(URLPrefix + "login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password);


    @POST(URLPrefix + "forgotPassword")
    @FormUrlEncoded
    Call<ResponseBody> forgotpassword(@Field("email") String email);

    @POST(URLPrefix + "ChangePassword")
    @FormUrlEncoded
    Call<ResponseBody> changepassword(@Field("id") String userid,
                                      @Field("token") String token,
                                      @Field("old_password") String oldpassword,
                                      @Field("new_password") String newpassword);


    @GET()
    Call<ResponseBody> getProfile(@Url() String url);


    @Multipart
    @POST(URLPrefix + "updateProfile")
    Call<ResponseBody> editProfile(
            @Part("id") RequestBody userid,
            @Part("token") RequestBody usertoken,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("plate_number") RequestBody plate_number,
            @Part MultipartBody.Part userImage);


    @POST(URLPrefix + "logout")
    @FormUrlEncoded
    Call<ResponseBody> logout(@Field("userid") String id, @Field("token") String token);


    @POST(URLPrefix + "startSosCall")
    @FormUrlEncoded
    Call<ResponseBody> callStart(@Field("id") String id, @Field("token") String token, @Field("number") String number);

    @POST(URLPrefix + "endSosCall")
    @FormUrlEncoded
    Call<ResponseBody> callStop(@Field("id") String userid,
                                      @Field("token") String token,
                                      @Field("call_id") String callid);

    @POST(URLPrefix + "storeAddress")
    @FormUrlEncoded
    Call<ResponseBody> storeAddress(@Field("id") String userid,
                                      @Field("token") String token,
                                      @Field("address") String address,
                                      @Field("latitude") String latitude,
                                      @Field("longitude") String longitude,
                                      @Field("call_id") String callid,
                                      @Field("api_time") String api_time);

    @POST(URLPrefix + "getCharges")
    @FormUrlEncoded
    Call<ResponseBody> getCharges(@Field("id") String userid,
                                      @Field("token") String token,
                                      @Field("carType") String carType);



    @POST(URLPrefix + "getHoliday")
    @FormUrlEncoded
    Call<ResponseBody> getHoliday(@Field("id") String userid, @Field("token") String token);

    @POST(URLPrefix + "tripStart")
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
                                             @Field("additionalCharge") int additionalCharge,
                                             @Field("dateTime") String dateTime,
                                             @Field("platNumber") String platNumber,
                                             @Field("driverId") String driverId);

    @GET(URLPrefix + "getTripRecord")
    Call<ResponseBody> allTripRecords(@Query("userId") int userId, @Query("userToken") String userToken);

    @POST(URLPrefix + "deleteTrip")
    @FormUrlEncoded
    Call<ResponseBody> deleteTrip(@Field("userId") int userid,
                                  @Field("userToken") String token,
                                  @Field("tripId") int tripId);

    @POST(URLPrefix + "storeGPSAddress")
    @FormUrlEncoded
    Call<ResponseBody> gpsStoreAddress(@Field("id") String userid,
                                    @Field("token") String token,
                                    @Field("address") String address,
                                    @Field("latitude") String latitude,
                                    @Field("longitude") String longitude,
                                    @Field("speed") String speed,
                                    @Field("direction") String direction);
}
