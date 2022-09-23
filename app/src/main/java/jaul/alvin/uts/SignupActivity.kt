package jaul.alvin.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        btnRegister.setOnClickListener(this)
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        val actionbar = supportActionBar
        actionbar!!.title = "Sign Up"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnRegister ->{
                val nik : String = etRegNIK.text.toString().trim()
                val email :String = etRegEmail.text.toString().trim()
                val password :String = etRegPassword.text.toString().trim()
                val nama :String = etRegNama.text.toString().trim()
                val alamat :String = etRegAlamat.text.toString().trim()
                val no_telp :String = etRegTelp.text.toString().trim()

                if(nik.isEmpty()){
                    etRegNIK.error = "NIK harus diisi"
                    etRegNIK.requestFocus()
                    return
                }

                if(email.isEmpty()){
                    etRegEmail.error = "Email harus diisi"
                    etRegEmail.requestFocus()
                    return
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etRegEmail.error = "Email tidak valid"
                    etRegEmail.requestFocus()
                    return
                }

                if(password.isEmpty() || password.length < 8){
                    etRegPassword.error = "Password harus diisi"
                    etRegPassword.requestFocus()
                    return
                }

                if(nama.isEmpty()){
                    etRegNama.error = "Nama Barang harus diisi"
                    etRegNama.requestFocus()
                    return
                }

                if(alamat.isEmpty()){
                    etRegAlamat.error = "Alamat harus diisi"
                    etRegAlamat.requestFocus()
                    return
                }

                if(no_telp.isEmpty()){
                    etRegTelp.error = "Nomor Telepon harus diisi"
                    etRegTelp.requestFocus()
                    return
                }

                val userdata = hashMapOf(
                    "nim" to nik,
                    "email" to email,
                    "nama" to nama,
                    "password" to password,
                    "alamat" to alamat,
                    "no_telp" to no_telp,
                )
                registUser(email, password,userdata)
            }
        }
    }

    fun registUser(email: String, password: String,userdata:HashMap<String,String>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val currentUser = auth.currentUser!!
                    if (currentUser!=null){
                        currentUser!!.updateProfile(userProfileChangeRequest {
                            displayName = etRegNama.text.toString()
                        })
                        currentUser!!.sendEmailVerification()
                    }
                    val DocumentReference = db.collection("users").document(email)
                    DocumentReference.set(userdata)
                    Toast.makeText(
                        this, "Berhasil mendaftarkan user, silahkan cek email anda untuk verifikasi akun anda",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }
    }

    companion object {
        var TAG = MainActivity::class.java.simpleName
    }
}