package com.taxi1.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.taxi1.R
import com.taxi1.Retrofit.Response.ProfileResponse
import com.taxi1.Retrofit.RetrofitHelper
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.activity.LoginActivity
import com.taxi1.activity.RegisterActivity
import com.taxi1.utils.Constants
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.Modifier

class EditProfileController(val context: Context, val view: IProfileView) : IEditProfileController {
    var dialog: AlertDialog? = null

    override fun editProfile(usernmame: String, email: String, phone:String,plateNumber: String,image: String) {

        val storedata = StoreUserData(context)
        val id = storedata.getString(Constants.USERID)
        val token = storedata.getString(Constants.TOKEN)
        val userid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), id.toString())

        val usertoken: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), token.toString())
        val uname1: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), usernmame.toString())
        val email1: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull()!!, email.toString())
        val phone1: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull()!!, phone.toString())
        val platenumber1: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull()!!, plateNumber.toString())

        var part: MultipartBody.Part? = null
        val file_path: String = image.toString()
        if (file_path != null && !file_path.isEmpty()) {
            val file = File(file_path)
            val reqFile = RequestBody.create(
                context.getContentResolver().getType(Utils.getImageContentUri(context, file)!!)!!
                    .toMediaTypeOrNull(), file
            )
            part = MultipartBody.Part.createFormData("image", file.name, reqFile)
        }

        val call: Call<ResponseBody>? =
            RetrofitHelper.getAPI().editProfile(userid,usertoken,uname1, email1,phone1,platenumber1, part)

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
                    val response: ProfileResponse =
                        gson.fromJson(reader, ProfileResponse::class.java)
                    if (response.getStatus() == 1) {
                        Toast.makeText(context, "" + response.getMessage(), Toast.LENGTH_SHORT)
                            .show()
                        val list = response.getResult()
                        view.onResponse(list)
                        Log.d("edit", "response: ===" + response.getResult())

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
                Log.d("edit", "onError: ===" + error)
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