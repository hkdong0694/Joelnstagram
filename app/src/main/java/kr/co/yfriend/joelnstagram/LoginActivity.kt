package kr.co.yfriend.joelnstagram

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        setListener()
        // printHashKey()
    }

    /**
     * Facebook HashKey 얻기 위한 Method
     */
    fun printHashKey() {
        try {
            val info: PackageInfo = packageManager
                .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
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
        // Google Login Client
        googleSignClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_default_client_id))
                .requestEmail()
                .build()
        )
        // Facebook Callback Manager
        callbackManager = CallbackManager.Factory.create()
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

        btn_facebook_login.setOnClickListener {
            facebookSignIn()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Facebook Login Method
     */
    private fun facebookSignIn() {
        // Facebook Login 접근 시 읽어올 데이터 종류 설정
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    getFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(applicationContext, "페이스북 로그인 취소", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(applicationContext, error?.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getFacebookAccessToken(token: AccessToken?) {
        token?.token?.let {
            val credential = FacebookAuthProvider.getCredential(it)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        gotoMainPage(task.result?.user)
                    }
                    else -> {
                        // 입력한 이메일이 이미 존재할 경우
                        Toast.makeText(
                            applicationContext,
                            task.exception?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
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