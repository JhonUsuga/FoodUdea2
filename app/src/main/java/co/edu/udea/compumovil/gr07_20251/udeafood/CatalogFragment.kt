package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CatalogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val restaurants = mutableListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_catalog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_restaurants)

        val adapter = RestaurantAdapter(restaurants) { restaurant ->
            val bundle = Bundle().apply {
                putString("storeId", restaurant.id)
                putString("name", restaurant.name)
                putString("description", restaurant.description)
                putString("hours", restaurant.hours)
                putString("imageUrl", restaurant.imageUrl)
            }
            findNavController().navigate(R.id.restaurantDetailFragment, bundle)
        }

        recyclerView.adapter = adapter

        firestore.collection("stores")
            .get()
            .addOnSuccessListener { result ->
                restaurants.clear()
                for (doc in result) {
                    val id = doc.id
                    val name = doc.getString("name") ?: continue
                    val description = doc.getString("description") ?: ""
                    val hours = doc.getString("hours") ?: ""
                    val imageUrl = doc.getString("imageUrl") ?: ""

                    restaurants.add(Restaurant(id, name, description, hours, imageUrl))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar tiendas", Toast.LENGTH_SHORT).show()
            }
    }
}