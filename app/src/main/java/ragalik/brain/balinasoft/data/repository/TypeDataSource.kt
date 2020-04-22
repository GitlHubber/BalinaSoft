package ragalik.brain.balinasoft.data.repository

import androidx.paging.PageKeyedDataSource
import ragalik.brain.balinasoft.data.api.ApiClient
import ragalik.brain.balinasoft.data.vo.Content
import ragalik.brain.balinasoft.data.vo.TypeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypeDataSource : PageKeyedDataSource<Int, Content>() {

    companion object {
        const val PAGE_SIZE = 20
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Content>) {
        getTypes(FIRST_PAGE, "initial", callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Content>) {
        getTypes(params.key, "after", null, callback)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Content>) {
        getTypes(params.key, "before", null, callback)
    }

    private fun getTypes (page: Int, status: String, initialCallback: LoadInitialCallback<Int, Content>? = null, callback: LoadCallback<Int, Content>? = null) {
        val call = ApiClient.instance.getTypes(page)
        call.enqueue(object : Callback<TypeResponse> {
            override fun onResponse(call: Call<TypeResponse>, response: Response<TypeResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val responseItems = apiResponse?.content

                    responseItems?.let {
                        val key: Int
                        when (status) {
                            "initial" -> initialCallback?.onResult(responseItems, null, FIRST_PAGE + 1)
                            "before" -> {
                                key = if (page > 1) page - 1 else 0
                                callback?.onResult(responseItems, key)
                            }
                            "after" -> {
                                key = page + 1
                                callback?.onResult(responseItems, key)
                            }
                            else -> ""
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TypeResponse>, t: Throwable) {}
        })
    }
}
