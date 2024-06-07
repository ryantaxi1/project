package com.android.taxi1in_carapp.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.RetrofitHelper
import com.android.taxi1in_carapp.Retrofit.newresponse.SigninResponse
import com.taxi1.ViewResponse.ILoginView
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

class LoginController(val context: Context, val view: ILoginView) : ILoginController {
    var dialog: AlertDialog? = null

    override fun login(phoneNo: String, password: String) {
        val call: Call<ResponseBody>? = RetrofitHelper.getAPI().login(phoneNo, password)

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
                    val response: SigninResponse = gson.fromJson(reader, SigninResponse::class.java)

                    if (response.status == 1) {
                        Utils.showToast(context, response.message)
                        val storedata = StoreUserData(context)
                        storedata.setString(Constants.USERID, response.result!!.id.toString())
                        storedata.setString(Constants.USERNAME, response.result!!.firstName.toString())
                        storedata.setString(Constants.USERNAME, response.result!!.lastName.toString())
                        storedata.setString(Constants.EMAIL,response.result!!.email.toString())
                        storedata.setString(Constants.PHONE, response.result!!.phone.toString())
                        storedata.setString(Constants.TOKEN, response.result!!.token.toString())
                        storedata.setString(Constants.PLATE_NUMBER, response.result!!.plateNumber.toString())
                        storedata.setString(Constants.DRIVER_ID, response.result!!.driverId.toString())
                        storedata.setBoolean(Constants.ISLOGGEDIN,true)
                        view.onLogin(response)
                        Log.d("TAG", "response: ===" + response)

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