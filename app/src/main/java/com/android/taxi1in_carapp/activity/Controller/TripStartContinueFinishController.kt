package com.android.taxi1in_carapp.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.Response.TripStartContinueStopResponse
import com.android.taxi1in_carapp.Retrofit.RetrofitHelper
import com.taxi1.ViewResponse.ITripStartContinueStopView
import com.taxi1.utils.Constants
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.Modifier

class TripStartContinueFinishController(val context: Context, val view: ITripStartContinueStopView) : ITripStartContinueStopController {

    var dialog: AlertDialog? = null

    override fun sendTripStartContinueStop(
        tripId: Int,
        carType: String,
        address: String,
        latitude: Double,
        longitude: Double,
        speed: Int,
        tripStatus: String,
        additionalCharge: Double,
        dateTime: String,
        platNumber: String,
        driverId: String,
        isDistance: Boolean
    ) {
        val storedata = StoreUserData(context)
        val id = storedata.getString(Constants.USERID)
        val token = storedata.getString(Constants.TOKEN)

        val call: Call<ResponseBody>? = RetrofitHelper.getAPI().tripStartContinueStop(
            id.toInt(),
            token,
            tripId,
            carType,
            address,
            latitude,
            longitude,
            speed,
            tripStatus,
            additionalCharge,
            dateTime,
            platNumber,
            driverId,
            isDistance
        )

        if(tripStatus == "start" || tripStatus == "finish"){
            Utils.showProgress(context)
        }

        RetrofitHelper.callApi(call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody?>?) {
                Utils.dismissProgress()
                try {
                    val resp = body!!.body()!!.string()
                    val reader: Reader = StringReader(resp) as Reader
                    val builder = GsonBuilder()
                    builder.excludeFieldsWithModifiers(
                        Modifier.FINAL,
                        Modifier.TRANSIENT,
                        Modifier.STATIC
                    )
                    val gson = builder.create()
                    val response: TripStartContinueStopResponse = gson.fromJson(reader, TripStartContinueStopResponse::class.java)
                    if (response.status == 1) {
//                        Utils.showToast(context, response.message)
                        view.onResponse(response)
                        Log.d("TAG", "response trip status $tripStatus : === $response")

                    } else {
                        Utils.dismissProgress()
                        Utils.showToast(context, response.message)
                        if(response.message.toString().equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }

//                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(code: Int, error: String?) {
                Utils.showToast(context, "Something Went Wrong")
                /*Utils.showToast(context, error.toString())
                Utils.dismissProgress()
                Log.d("TAG", "onError: ===" + error)
                if(error.toString().equals(context.resources.getString(R.string.txt_authorize)))
                {
                    Utils.cleardata(context)

                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }*/

                Utils.dismissProgress()
                Log.d("TAG", "onError: ===" + error)
//                Utils.showToast(context, error.toString())

                error?.let {

                    try {
                        val obj = JSONObject(error)

//                    Utils.showToast(context, obj.getString("message"))

                        if(obj.getString("message").equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }


//                            context.startActivity(Intent(context, LoginActivity::class.java))

                        } else { }
                    }catch (e: Exception){
                        Log.d("TAG", "onError catch: === $e")
                    }
                }
            }
        })
    }

    override fun getActiveTrip() {
        val storeData = StoreUserData(context)
        val id = storeData.getString(Constants.USERID)
        val token = storeData.getString(Constants.TOKEN)

        val call: Call<ResponseBody>? = RetrofitHelper.getAPI().getActiveTrip(id.toInt(), token)

        Utils.showProgress(context)

        RetrofitHelper.callApi(call, object : RetrofitHelper.ConnectionCallBack {
            override fun onSuccess(body: Response<ResponseBody?>?) {
                Utils.dismissProgress()
                try {
                    val resp = body!!.body()!!.string()
                    val reader: Reader = StringReader(resp) as Reader
                    val builder = GsonBuilder()
                    builder.excludeFieldsWithModifiers(
                        Modifier.FINAL,
                        Modifier.TRANSIENT,
                        Modifier.STATIC
                    )
                    val gson = builder.create()
                    val response: TripStartContinueStopResponse = gson.fromJson(reader, TripStartContinueStopResponse::class.java)
                    if (response.status == 1) {
//                        Utils.showToast(context, response.message)
                        view.getActiveTripResponse(response)
                        Log.d("TAG", "response get trip active status response : $response")

                    } else {
                        Utils.dismissProgress()
                        Utils.showToast(context, response.message)
                        if(response.message.toString().equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }

//                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onError(code: Int, error: String?) {
                /*Utils.showToast(context, error.toString())
                Utils.dismissProgress()
                Log.d("TAG", "onError: ===" + error)
                if(error.toString().equals(context.resources.getString(R.string.txt_authorize)))
                {
                    Utils.cleardata(context)

                    context.startActivity(Intent(context, RegisterActivity::class.java))
                }*/

                Utils.dismissProgress()
                Log.d("TAG", "onError: ===" + error)
//                Utils.showToast(context, error.toString())

                error?.let {

                    try {
                        val obj = JSONObject(error)

//                    Utils.showToast(context, obj.getString("message"))

                        if(obj.getString("message").equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }


//                            context.startActivity(Intent(context, LoginActivity::class.java))

                        } else { }
                    }catch (e: Exception){
                        Log.d("TAG", "onError catch: === $e")
                    }
                }
            }
        })
    }
}