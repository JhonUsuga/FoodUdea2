package co.edu.udea.compumovil.gr07_20251.udeafood

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddProductFragment : Fragment() {

    private lateinit var productImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameInput = view.findViewById<EditText>(R.id.input_product_name)
        val descriptionInput = view.findViewById<EditText>(R.id.input_product_description)
        val priceInput = view.findViewById<EditText>(R.id.input_product_price)
        addButton = view.findViewById(R.id.btn_add_product)
        productImageView = view.findViewById(R.id.img_store_logo) // reutilizado para el producto

        productImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 200)
        }

        addButton.setOnClickListener {
            val name = nameInput.text.toString()
            val description = descriptionInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull()

            if (name.isBlank() || description.isBlank() || price == null) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageUrl = selectedImageUri?.toString()
                ?: "https://i.imgur.com/zM9aZ4F.png" // imagen por defecto si no selecciona

            val product = Product(
                name = name,
                description = description,
                price = price,
                imageUrl = imageUrl
            )

            val store = CreateStoreFragment.userStore
            if (store == null) {
                Toast.makeText(requireContext(), "No se encontró tienda asociada", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addButton.isEnabled = false

            FirebaseFirestore.getInstance()
                .collection("stores")
                .document(store.id)
                .collection("products")
                .add(product)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto guardado correctamente", Toast.LENGTH_SHORT).show()

                    nameInput.text.clear()
                    descriptionInput.text.clear()
                    priceInput.text.clear()
                    productImageView.setImageResource(R.drawable.ic_udea_logo)
                    selectedImageUri = null
                    addButton.isEnabled = true
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al guardar producto", Toast.LENGTH_SHORT).show()
                    addButton.isEnabled = true
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            productImageView.setImageURI(selectedImageUri)
        }
    }
}