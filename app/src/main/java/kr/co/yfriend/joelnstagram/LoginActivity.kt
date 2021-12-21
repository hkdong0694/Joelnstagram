package kr.co.yfriend.joelnstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        setListener()
    }

    /**
     * initView ( 초기화 ) Method
     */
    private fun initView() {
        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
    }

    /**
     * OnClick Listener Method
     */
    private fun setListener() {
        btn_email_login.setOnClickListener {
            signInAndSignUp()
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
     * Main Page 이동 Method
     */
    private fun gotoMainPage(user: FirebaseUser?) {
        user?.let {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

}