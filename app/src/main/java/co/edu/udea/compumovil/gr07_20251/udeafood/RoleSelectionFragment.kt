package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class RoleSelectionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_role_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btn_login_client).setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        view.findViewById<Button>(R.id.btn_login_store).setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        view.findViewById<Button>(R.id.btn_register_client).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        view.findViewById<Button>(R.id.btn_register_store).setOnClickListener {
            findNavController().navigate(R.id.createStoreFragment)
        }
    }
}
