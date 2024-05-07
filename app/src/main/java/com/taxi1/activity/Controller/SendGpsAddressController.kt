package com.taxi1.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.RetrofitHelper
import com.taxi1.ViewResponse.ICommanView
import com.taxi1.ViewResponse.ISendGpsLocationView
import com.taxi1.utils.Constants
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.Modifier


class SendGpsAddressController (val context: Context, val view: ISendGpsLocationView) : ISendGPSAddressController {
//    var dialog: AlertDialog? = null

    override fun locationAddress(address: String, lat: String, long: String, speed: String, direction: String) {
        val storedata = StoreUserData(context)
        val id = storedata.getString(Constants.USERID)
        val token = storedata.getString(Constants.TOKEN)

        val call: Call<ResponseBody>? = RetrofitHelper.getAPI().gpsStoreAddress(id,token, address, lat, long, speed, direction)

//        Utils.showProgress(context)
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
                    val response: CommanResponse = gson.fromJson(reader, CommanResponse::class.java)
                    if (response.getStatus() == 1) {
                        view.onSendGpsLocation(response)
                        Log.d("TAG", "response send gps location: ===" + response)

                    } else {
                        Utils.dismissProgress()
                        /*if(response.getMessage().toString().equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }
                        }*/
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onError(code: Int, error: String?) {
                Log.d("TAG", "onError gps tracker: ===" + error)
                /*Utils.showToast(context, error.toString())

                error?.let {
                    try {
                        val obj = JSONObject(error)
                        if(obj.getString("message").equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }

//                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }else{ }
                    }catch (e: Exception){
                        Log.d("TAG", "onError catch: === $e")
                    }
                }*/
            }
        })
    }
}