package ragalik.brain.balinasoft.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.type_item.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ragalik.brain.balinasoft.R
import ragalik.brain.balinasoft.camera.PathUtils
import ragalik.brain.balinasoft.ui.Adapter.Companion.fileWithUri
import ragalik.brain.balinasoft.ui.Adapter.Companion.outputFileUri
import ragalik.brain.balinasoft.data.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {

    private var typeViewModel: TypeViewModel? = null
    private val PICK_FILE_REQUEST = 1000
    var selectedUri: Uri? = null

    companion object {
        lateinit var activity: AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this
        val adapter = Adapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        typeViewModel = ViewModelProvider(this).get(TypeViewModel::class.java)
        typeViewModel!!.adPagedList.observe(this, Observer {
            adapter.submitList(it)
        })
        recyclerView.adapter = adapter

        refresher?.setOnRefreshListener {
            typeViewModel?.liveTypeDataSource?.value?.invalidate()
            refresher?.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FILE_REQUEST) {
            if (fileWithUri == null) {
                fileWithUri = File(data?.data?.let { PathUtils.getPath(activity, it) }!!)
                if (data.data != null) {
                    selectedUri = data.data
                }
            } else {
                selectedUri = outputFileUri?.normalizeScheme()
            }
            uploadFile(Adapter.name, Adapter.id)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Adapter.takePicture(this)
            } else {
                Toast.makeText(activity, "Отказано в доступе", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFile(name: String, id: Int) {
        if (selectedUri != null) {
            val filename = "$id.png"

            val body = MultipartBody.Part.createFormData("uploaded_file", filename, RequestBody.create(
                MediaType.parse("image/*"), fileWithUri!!))

            ApiClient.instance.uploadImage(RequestBody.create(MediaType.parse("multipart/form-data"), name), body, RequestBody.create(
                MediaType.parse("multipart/form-data"), "$id"))?.enqueue(object : Callback<String?> {

                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Toast.makeText(activity, "Изображение загружено!", Toast.LENGTH_LONG).show()
                        fileWithUri = null
                        Picasso.get().invalidate(selectedUri)
                        Picasso.get().load(selectedUri).into(image_view)
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {}
                })
        }
    }
}
