package com.taxi1.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.google.gson.GsonBuilder
import com.taxi1.R
import com.taxi1.Retrofit.Response.CommanResponse
import com.taxi1.Retrofit.Response.LoginResponse
import com.taxi1.Retrofit.RetrofitHelper
import com.taxi1.ViewResponse.IForgotPasswordView
import com.taxi1.ViewResponse.ILoginView
import com.taxi1.activity.LoginActivity
import com.taxi1.activity.RegisterActivity
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

class ForgotPasswordController(val context: Context, val view: IForgotPasswordView) : IForgotPasswordController {
    var dialog: AlertDialog? = null

    override fun forgotPassword(email: String) {
        val call: Call<ResponseBody>? = RetrofitHelper.getAPI().forgotpassword(email)
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
                    val response: CommanResponse = gson.fromJson(reader, CommanResponse::class.java)
                    if (response.getStatus() == 1) {
                        Utils.showToast(context, response.getMessage())

                        view.onForgot(response)
                        Log.d("TAG", "response: ===" + response)

                    } else {
                        Utils.dismissProgress()
                        Utils.showToast(context, response.getMessage())
                        if(response.getMessage().toString().equals(context.resources.getString(R.string.txt_authorize)))
                        {
                            Utils.cleardata(context)

                            if(dialog == null || dialog?.isShowing != true){
                                dialog = Utils.showPositiveAlertDialog(context, "Logout", "Please login again.")
                            }else{ }

//                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                } catch (e: IOException) {
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
                Utils.showToast(context, error.toString())

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