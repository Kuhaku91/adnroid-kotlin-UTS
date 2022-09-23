package jaul.alvin.uts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_databarang.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InputActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener{

    lateinit var db : DatabaseReference
    lateinit var adapter : ListAdapter
    var alBrg = ArrayList<HashMap<String, Any>>()
    var Brg = Barang()
    var hm = HashMap<String, Any>()

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedday = 0
    var savedmonth = 0
    var savedyear = 0
    var savedhour = 0
    var savedminute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_databarang)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        btnTambah.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        lvMhs.setOnItemClickListener(itemClick)
        pickdate()

    }

    override fun onStart() {
        super.onStart()
        db = FirebaseDatabase.getInstance().getReference("Tabel")
        showData()
    }

    val itemClick = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        hm = HashMap()
        hm = alBrg.get(i)
        edtKDBrg.setText(hm.get("KodeBrg").toString()!!)
        edtNamaBrg.setText(hm.get("Nama").toString()!!)
        edtLocBrg.setText(hm.get("loc").toString()!!)
        edtDateTimeBrg.setText(hm.get("datetime").toString()!!)
    }

    private fun getDateTimeCalender(){
        val cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    fun pickdate(){
        btnDate.setOnClickListener {
            getDateTimeCalender()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedday = dayOfMonth
        savedmonth = month
        savedyear = year
        getDateTimeCalender()
        TimePickerDialog(this,this,hour,minute,true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedhour = hourOfDay
        savedminute = minute
        edtDateTimeBrg.setText("$savedday/$savedmonth/$savedyear, Jam $savedhour ; $savedminute")
    }

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.btnTambah -> {
                Brg.kdBrg = edtKDBrg.text.toString()
                Brg.namaBrg = edtNamaBrg.text.toString()
                Brg.loc = edtLocBrg.text.toString()
                Brg.DateTime = edtDateTimeBrg.text.toString()
                db.child(Brg.kdBrg!!).setValue(Brg)
            }
            R.id.btnDelete -> {
                db.child(Brg.kdBrg!!).removeValue()
            }
        }
        edtKDBrg.setText("");edtNamaBrg.setText("");edtLocBrg.setText("");edtDateTimeBrg.setText("")
    }

    fun showData(){
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dataSnapShotIterable = snapshot.children
                var iterator = dataSnapShotIterable.iterator()
                alBrg.clear()
                while (iterator.hasNext()) {
                    Brg = iterator.next().getValue(Barang::class.java)!!
                    hm = HashMap()
                    hm.put("KodeBrg", Brg.kdBrg!!)
                    hm.put("Nama", Brg.namaBrg!!)
                    hm.put("loc", Brg.loc!!)
                    hm.put("datetime", Brg.DateTime!!)
                    alBrg.add(hm)
                }
                adapter = SimpleAdapter(
                    this@InputActivity,
                    alBrg,
                    R.layout.row_barang,
                    arrayOf("KodeBrg","Nama","loc","datetime"),
                    intArrayOf(R.id.txKodeBrg, R.id.txNamaBarang, R.id.txLocation, R.id.txDateTime)
                )
                lvMhs.setAdapter(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@InputActivity,
                    "Connection to database error : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}