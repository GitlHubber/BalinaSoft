package ragalik.brain.balinasoft.ui

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.type_item.view.*
import ragalik.brain.balinasoft.ui.MainActivity.Companion.activity
import ragalik.brain.balinasoft.R
import ragalik.brain.balinasoft.data.vo.Content
import java.io.File

class Adapter : PagedListAdapter <Content, Adapter.ViewHolder>(ITEM_COMPARATOR) {

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean =
                newItem == oldItem
        }
        private val PICK_FILE_REQUEST = 1000
        var fileWithUri: File? = null
        var outputFileUri: Uri? = null
        var name: String = ""
        var id: Int = 0

        fun takePicture(appCompatActivity: AppCompatActivity) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            fileWithUri = File(Environment.getExternalStorageDirectory().toString() + "/DCIM/", ".jpg")
            outputFileUri = FileProvider.getUriForFile(appCompatActivity, appCompatActivity.applicationContext.packageName + ".provider", fileWithUri!!)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                outputFileUri
            )
            appCompatActivity.startActivityForResult(intent,
                PICK_FILE_REQUEST
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val typeResponse = getItem(position)

        holder.itemView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissionStatus: Int = activity.checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE)
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    name = typeResponse?.name!!
                    id = typeResponse.id!!
                    takePicture(
                        activity
                    )
                } else {
                    name = typeResponse?.name!!
                    id = typeResponse.id!!
                    activity.requestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            } else {
                takePicture(
                    activity
                )
            }
        }

        typeResponse?.image.toString().let {
            var correctPath = it
            correctPath = "https" + correctPath.substring(4, correctPath.length)

                Picasso.get()
                    .load(correctPath)
                    .fit()
                    .centerCrop()
                    .into(holder.itemView.image_view)
        }

        typeResponse?.let {
            holder.bind(typeResponse)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.image_view
        private val name = view.name
        private val id = view.type_id

        fun bind (content: Content) {
            name.text = content.name
            id.text = "${content.id}"
        }
    }
}
