package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emailInput = view.findViewById(R.id.input_email)
        passwordInput = view.findViewById(R.id.input_password)
        loginButton = view.findViewById(R.id.btn_login)

        firestore = FirebaseFirestore.getInstance()

        view.findViewById<TextView>(R.id.tv_create_account).setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginButton.isEnabled = false

            // Verificar si es tienda
            firestore.collection("stores")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { storeResult ->
                    if (!storeResult.isEmpty) {
                        val storeDoc = storeResult.documents[0]
                        val storedPassword = storeDoc.getString("password")
                        if (storedPassword == password) {
                            val store = storeDoc.toObject(Store::class.java)
                            CreateStoreFragment.userStore = store
                            Toast.makeText(requireContext(), "Bienvenido comerciante", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.addProductFragment)
                        } else {
                            Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                        loginButton.isEnabled = true
                    } else {
                        // Si no es tienda, verificar si es usuario (cliente)
                        firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { userResult ->
                                if (!userResult.isEmpty) {
                                    val userDoc = userResult.documents[0]
                                    val storedPassword = userDoc.getString("password")
                                    if (storedPassword == password) {
                                        Toast.makeText(requireContext(), "Bienvenido cliente", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.mapFragment)
                                    } else {
                                        Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                                }
                                loginButton.isEnabled = true
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error al buscar usuario", Toast.LENGTH_SHORT).show()
                                loginButton.isEnabled = true
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al buscar tienda", Toast.LENGTH_SHORT).show()
                    loginButton.isEnabled = true
                }
        }
    }
}