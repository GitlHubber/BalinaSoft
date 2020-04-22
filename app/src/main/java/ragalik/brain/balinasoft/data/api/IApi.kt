package ragalik.brain.balinasoft.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import ragalik.brain.balinasoft.data.vo.TypeResponse
import retrofit2.Call
import retrofit2.http.*

interface IApi {

    @GET("api/v2/photo/type")
    fun getTypes(@Query("page") page: Int): Call<TypeResponse>

    @Multipart
    @POST("api/v2/photo")
    fun uploadImage(@Part("name") name: RequestBody,
                           @Part photo: MultipartBody.Part?,
                           @Part("typeId") typeId: RequestBody
    ): Call<String?>?
}