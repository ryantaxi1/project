package com.android.taxi1in_carapp.activity.Controller

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.RetrofitHelper
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.taxi1.ViewResponse.IProfileView
import com.taxi1.utils.Constants
import com.taxi1.utils.StoreUserData
import com.taxi1.utils.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.Modifier

class EditProfileController(val context: Context, val view: IProfileView) : IEditProfileController {
    var dialog: AlertDialog? = null

    override fun editProfile(firstName: String, lastName: String, phone: String, email: String, image: File?) {

        val storedata = StoreUserData(context)
        val id = storedata.getString(Constants.USERID)
        val token = storedata.getString(Constants.TOKEN)

        val userid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), id.toString())
        val usertoken: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), token.toString())
        val firstname: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), firstName.toString())
        val lastname: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), lastName.toString())
        val phoneNo: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), phone.toString())
        val newEmail: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), email.toString())

        // profile pic
        var profilePic: MultipartBody.Part? = null
        if(image != null){
            val profilePicRequestBody: RequestBody = image.asRequestBody("image/${image.extension}".toMediaTypeOrNull())
            profilePic = MultipartBody.Part.createFormData("profileImage", image.name, profilePicRequestBody)
        }

        val call: Call<ResponseBody>? =
            RetrofitHelper.getAPI().updateProfile(userid, usertoken, firstname, lastname, phoneNo, newEmail, profilePic)

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
                    val response: GetProfileResponse = gson.fromJson(reader, GetProfileResponse::class.java)
                    if (response.status == 1) {
                        Toast.makeText(context, "" + response.message, Toast.LENGTH_SHORT)
                            .show()
                        val list = response.result
                        view.onResponse(list)
                        Log.d("edit", "response: ===" + response.result)

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