package my.edu.tarc.warehouserit3g2.changeRack

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

import my.edu.tarc.warehouserit3g2.Data.RackListAdapter
import my.edu.tarc.warehouserit3g2.R
import kotlin.collections.ArrayList

import my.edu.tarc.warehouserit3g2.databinding.FragmentRackListBinding






class RackList_Fragment : Fragment(), RackListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentRackListBinding
    var rack = ArrayList<String>()
    lateinit var adapter: RackListAdapter
    lateinit var myRecyclerView : RecyclerView
    private  var  QR: ImageView? = null
    private val navController by lazy { NavHostFragment.findNavController(this)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rack_list_, container, false)

        val db = Firebase.firestore
        rack.clear()

        myRecyclerView = binding.RackRecycleView

        db.collection("Rack").orderBy("Rack ID")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    rack.add(document.id)
                }

                adapter = RackListAdapter(rack,this)
                myRecyclerView.adapter = adapter

                binding.rackListSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    android.widget.SearchView.OnQueryTextListener {
                    val myRecyclerView : RecyclerView = binding.RackRecycleView


                    override fun onQueryTextSubmit(query: String?): Boolean {

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {

                        adapter.filter.filter(newText)


                        return false
                    }
                })
            }
        return binding.root
    }

    override fun onItemClick(position: Int) {

        val clickedItem  = rack[position]

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
        basicAlert(clickedItem)
    }


    fun basicAlert(rackId: String) {




        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Rack ID")
        QR = ImageView(this.requireContext())

        val bitmaps = generateQRCode(rackId)

        QR?.setImageBitmap(bitmaps)
        builder.setView(QR)

        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 400
        val height = 400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) { Log.d(ContentValues.TAG, "generateQRCode: ${e.message}") }
        return bitmap
    }


}