package co.edu.udea.compumovil.gr07_20251.udeafood

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateStoreFragment : Fragment() {

    private lateinit var storeImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var saveBtn: Button

    companion object {
        var userStore: Store? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_store, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = view.findViewById<EditText>(R.id.input_store_name)
        val hours = view.findViewById<EditText>(R.id.input_store_hours)
        val price = view.findViewById<EditText>(R.id.input_min_price)
        val isOpen = view.findViewById<CheckBox>(R.id.check_open)
        val description = view.findViewById<EditText>(R.id.input_location_description)
        val locationText = view.findViewById<TextView>(R.id.text_selected_location)
        val pickLocationBtn = view.findViewById<Button>(R.id.btn_pick_location)
        val emailField = view.findViewById<EditText>(R.id.input_store_email)
        val passwordField = view.findViewById<EditText>(R.id.input_store_password)
        storeImageView = view.findViewById(R.id.img_store_logo)
        saveBtn = view.findViewById(R.id.btn_save_store)

        // Mostrar ubicación seleccionada si existe
        PickLocationFragment.selectedLocation?.let {
            locationText.text = "Lat: %.5f, Lon: %.5f".format(it.latitude, it.longitude)
        }

        // Seleccionar imagen
        storeImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        // Navegar a selección de ubicación
        pickLocationBtn.setOnClickListener {
            findNavController().navigate(R.id.pickLocationFragment)
        }

        // Guardar tienda
        saveBtn.setOnClickListener {
            val geo = PickLocationFragment.selectedLocation
            if (geo == null) {
                Toast.makeText(requireContext(), "Debes seleccionar una ubicación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val storeName = name.text.toString().trim()
            val desc = description.text.toString().trim()
            val hrs = hours.text.toString().trim()
            val min = price.text.toString().toIntOrNull() ?: 0
            val open = isOpen.isChecked
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (storeName.isBlank() || desc.isBlank() || hrs.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val storeId = UUID.randomUUID().toString()
            val imageUrl = selectedImageUri?.toString()
                ?: "https://i.imgur.com/zM9aZ4F.png"

            val store = Store(
                id = storeId,
                name = storeName,
                location = "%.5f, %.5f".format(geo.latitude, geo.longitude),
                description = desc,
                hours = hrs,
                minPrice = min,
                open = open,
                imageResId = 0,
                imageUrl = imageUrl,
                products = mutableListOf(),
                email = email,
                password = password
            )

            FirebaseFirestore.getInstance()
                .collection("stores")
                .document(storeId)
                .set(store)
                .addOnSuccessListener {
                    userStore = store
                    Toast.makeText(requireContext(), "Tienda guardada correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.addProductFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al guardar tienda", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            storeImageView.setImageURI(selectedImageUri)
        }
    }
}