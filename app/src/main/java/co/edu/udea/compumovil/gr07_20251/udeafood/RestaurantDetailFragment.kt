package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantDetailFragment : Fragment() {

    private lateinit var productRecyclerView: RecyclerView
    private val productList = mutableListOf<Product>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_restaurant_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = arguments?.getString("name") ?: "Nombre desconocido"
        val description = arguments?.getString("description") ?: "Sin descripción"
        val hours = arguments?.getString("hours") ?: "Horario no disponible"
        val imageUrl = arguments?.getString("imageUrl") ?: ""
        val storeId = arguments?.getString("storeId")
        if (storeId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se encontró el ID de la tienda", Toast.LENGTH_SHORT).show()
            return
        }


        view.findViewById<TextView>(R.id.detail_name).text = name
        view.findViewById<TextView>(R.id.detail_type).text = description
        view.findViewById<TextView>(R.id.detail_hours).text = hours

        val imageView = view.findViewById<ImageView>(R.id.detail_image)
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_udea_logo)
            .into(imageView)

        view.findViewById<Button>(R.id.btn_back_to_list).setOnClickListener {
            requireActivity().onBackPressed()
        }

        productRecyclerView = view.findViewById(R.id.rv_products)
        productRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productRecyclerView.adapter = ProductAdapter(productList)

        if (storeId == null) {
            Toast.makeText(requireContext(), "Error: ID de tienda no recibido", Toast.LENGTH_LONG).show()
            return
        }

        firestore.collection("stores").document(storeId).collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (doc in result) {
                    val product = doc.toObject(Product::class.java)
                    productList.add(product)
                }
                productRecyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }
}

