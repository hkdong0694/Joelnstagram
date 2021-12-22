package kr.co.yfriend.joelnstagram.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import kr.co.yfriend.joelnstagram.R
import kr.co.yfriend.joelnstagram.navigation.model.ContentDTO
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseStore: FirebaseFirestore

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
        // Initiate Auth
        auth = FirebaseAuth.getInstance()
        // Initiate FireStore
        firebaseStore = FirebaseFirestore.getInstance()

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
            // Storage 저장 Method
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

        // Promise Method
        uri?.let {
            storageRef.putFile(it).continueWith {
                return@continueWith storageRef.downloadUrl
            }.addOnCompleteListener {
                val contentDTO = ContentDTO()

                // Insert downloadUri of image
                contentDTO.imageUri = uri.toString()
                // Insert uid of user
                contentDTO.userName = auth.currentUser?.uid
                // Insert userId ( Email )
                contentDTO.userId = auth.currentUser?.email
                // Insert explain of content
                contentDTO.explain = et_content.text.toString()
                // Insert Current TimeStamp
                contentDTO.timestamp = System.currentTimeMillis()

                // FireStore Save CallBack
                firebaseStore.collection("images").document().set(contentDTO)

                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        // uri?.let {
        //     storageRef.putFile(it).addOnSuccessListener {
        //         storageRef.downloadUrl.addOnCompleteListener { uri ->
        //             val contentDTO = ContentDTO()
        //
        //             // Insert downloadUri of image
        //             contentDTO.imageUri = uri.toString()
        //             // Insert uid of user
        //             contentDTO.userName = auth.currentUser?.uid
        //             // Insert userId ( Email )
        //             contentDTO.userId = auth.currentUser?.email
        //             // Insert explain of content
        //             contentDTO.explain = et_content.text.toString()
        //             // Insert Current TimeStamp
        //             contentDTO.timestamp = System.currentTimeMillis()
        //
        //             // FireStore Save CallBack
        //             firebaseStore.collection("images").document().set(contentDTO)
        //
        //             setResult(Activity.RESULT_OK)
        //             finish()
        //         }
        //     }
        // }
    }

}