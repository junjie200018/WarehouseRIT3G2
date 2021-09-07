package my.edu.tarc.warehouserit3g2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import my.edu.tarc.warehouserit3g2.databinding.FragmentProductMovementBinding

class ProductMovement_Fragment : Fragment() {

    private lateinit var binding: FragmentProductMovementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_movement_,container, false)



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_movement_, container, false)
    }
}