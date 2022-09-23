package jaul.alvin.uts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.btnLogin ->{
                val email :String = etEmail.text.toString().trim()
                val password :String = etPassword.text.toString().trim()
                if(email.isEmpty()){
                    etEmail.error = "Email tidak boleh kosong"
                    etEmail.requestFocus()
                    return
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etEmail.error = "Email tidak valid"
                    etEmail.requestFocus()
                    return
                }
                if(password.isEmpty() || password.length < 8){
                    etPassword.error = "Password tidak boleh kurang dari 8 karakter"
                    etPassword.requestFocus()
                    return
                }
                loginUser(email, password)
            }

            R.id.signup ->{
                val intent = Intent(this,SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }
    companion object {
        var TAG = MainActivity::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        btnLogin.setOnClickListener(this)

        signup.setOnClickListener(this)
    }
    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser!!
                    if (currentUser!=null){
                        if (currentUser.isEmailVerified){
                            val email = auth.currentUser!!.email.toString()
                            val df = db.collection("users").document(email).get()
                            df.addOnSuccessListener(OnSuccessListener {
                                val df = db.collection("users").document(email).get()
                                val password = it.data?.get("password").toString()
                                val nik = it.data?.get("nik").toString()
                                val nama = it.data?.get("nama").toString()
                                val alamat = it.data?.get("alamat").toString()
                                val no_telp = it.data?.get("no_telp").toString()
                                Intent(this, InputActivity::class.java)
                                    .also { dataInt ->
                                        dataInt.putExtra("nik",nik)
                                        dataInt.putExtra("email", email)
                                        dataInt.putExtra("password", password)
                                        dataInt.putExtra("nama", nama)
                                        dataInt.putExtra("alamat", alamat)
                                        dataInt.putExtra("no_telp", no_telp)
                                        dataInt.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(dataInt)
                                        return@OnSuccessListener
                                    }})
                        }
                        else{
                            Toast.makeText(
                                this, "Email anda belum terverifikasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnCompleteListener
                        }
                    }
                }else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Email/Password salah",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnCompleteListener
                }
            }
    }
}