package kr.co.yfriend.joelnstagram

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        setListener()
    }

    // OnActivityResult 가 Deprecated 가 되면서 추가된 내용
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val googleResult = Auth.GoogleSignInApi.getSignInResultFromIntent(it)
                    if (googleResult != null) {
                        if (googleResult.isSuccess) {
                            val account = googleResult.signInAccount
                            firebaseAuthWithGoogle(account)
                        }
                    }
                }
            }
        }

    /**
     * initView ( 초기화 ) Method
     */
    private fun initView() {
        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        googleSignClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_default_client_id))
                .requestEmail()
                .build()
        )
    }

    /**
     * OnClick Listener Method
     */
    private fun setListener() {
        btn_email_login.setOnClickListener {
            signInAndSignUp()
        }

        btn_google_login.setOnClickListener {
            googleSignIn()
        }
    }

    /**
     * Firebase Email 형식 회원가입 Method
     */
    private fun signInAndSignUp() {
        auth.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        // 회원가입이 성공했을 경우
                        gotoMainPage(it.result?.user)
                    }
                    it.exception?.message.isNullOrEmpty() -> {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 입력한 이메일이 이미 존재할 경우
                        signInEmail()
                    }
                }
            }
    }

    /**
     * Firebase Email 형식 로그인 Method
     */
    private fun signInEmail() {
        auth.signInWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        gotoMainPage(it.result?.user)
                    }
                    else -> {
                        // 입력한 이메일이 이미 존재할 경우
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    /**
     * Google Login Method
     */
    private fun googleSignIn() {
        val signInIntent = googleSignClient.signInIntent
        startForResult.launch(signInIntent)
    }

    /**
     * Firebase Google Login Method
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            when {
                it.isSuccessful -> {
                    // Google Login 성공
                    gotoMainPage(it.result?.user)
                }
                else -> {
                    // 입력한 이메일이 이미 존재할 경우
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Main Page 이동 Method
     */
    private fun gotoMainPage(user: FirebaseUser?) {
        user?.let {
            // User 정보 넘기기
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

}