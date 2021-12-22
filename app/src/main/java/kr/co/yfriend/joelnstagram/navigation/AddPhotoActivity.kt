package kr.co.yfriend.joelnstagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import kr.co.yfriend.joelnstagram.R
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        initView()
        setListener()
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // This is path to the selected image
                uri = result.data?.data
                iv_photo.setImageURI(uri)
            } else {
                // Exit the addPhotoActivity if you leave album without seleting it
                finish()
            }
        }

    /**
     * InitView ( 초기화 )
     */
    private fun initView() {
        // Initiate Storage
        storage = FirebaseStorage.getInstance()

        val photoPickIntent = Intent(Intent.ACTION_PICK)
        photoPickIntent.type = "image/*"
        // 갤러리 연동
        startForResult.launch(photoPickIntent)
    }

    /**
     * setListener Method
     */
    private fun setListener() {
        btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    /**
     * 사진 Upload -> Firebase Storage
     */
    private fun contentUpload() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_" + timeStamp + "_.png"
        val storageRef = storage.reference.child("images").child(imageFileName)

        uri?.let {
            storageRef.putFile(it).addOnSuccessListener {
                Toast.makeText(applicationContext, "업로드 성공", Toast.LENGTH_SHORT).show()
            }
        }
    }

}