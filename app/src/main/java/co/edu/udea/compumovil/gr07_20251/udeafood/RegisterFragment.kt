package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterFragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        nameInput = view.findViewById(R.id.input_name)
        emailInput = view.findViewById(R.id.input_email)
        passwordInput = view.findViewById(R.id.input_password)
        confirmPasswordInput = view.findViewById(R.id.input_confirm_password)
        registerButton = view.findViewById(R.id.btn_register)
        goToLogin = view.findViewById(R.id.tv_go_to_login)

        firestore = FirebaseFirestore.getInstance()

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(name, email, password)
            }
        }

        goToLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }

    private fun registerUser(name: String, email: String, password: String) {
        registerButton.isEnabled = false

        val userId = UUID.randomUUID().toString()
        val userMap = mapOf(
            "id" to userId,
            "name" to name,
            "email" to email,
            "password" to password,
            "role" to "cliente"
        )

        firestore.collection("users")
            .document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                registerButton.isEnabled = true
            }
    }
}