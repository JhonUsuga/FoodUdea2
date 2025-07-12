package co.edu.udea.compumovil.gr07_20251.udeafood

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))
        return inflater.inflate(R.layout.fragment_osm_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        map = view.findViewById(R.id.osm_map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(6.2675, -75.5686)
        map.controller.setZoom(18.0)
        map.controller.setCenter(startPoint)

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("stores")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val name = doc.getString("name") ?: continue
                    val hours = doc.getString("hours") ?: ""
                    val minPrice = doc.getLong("minPrice")?.toInt() ?: 0
                    val location = doc.getString("location") ?: ""
                    val description = doc.getString("description") ?: ""
                    val imageUrl = doc.getString("imageUrl") ?: ""

                    val parts = location.split(",")
                    if (parts.size == 2) {
                        val lat = parts[0].trim().toDoubleOrNull()
                        val lon = parts[1].trim().toDoubleOrNull()

                        if (lat != null && lon != null) {
                            val geo = GeoPoint(lat, lon)
                            loadMarkerIcon(imageUrl, geo, name, hours, minPrice, description)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar tiendas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMarkerIcon(
        imageUrl: String,
        position: GeoPoint,
        name: String,
        hours: String,
        price: Int,
        description: String
    ) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .circleCrop()
            .into(object : CustomTarget<Bitmap>(100, 100) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val marker = Marker(map)
                    marker.position = position
                    marker.title = name
                    marker.subDescription = "Horario: $hours\nDesde $$price\n$description"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.icon = BitmapDrawable(resources, makeDropShape(resource))
                    marker.setOnMarkerClickListener { m, _ ->
                        m.showInfoWindow()
                        true
                    }
                    map.overlays.add(marker)
                    map.invalidate()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun makeDropShape(original: Bitmap): Bitmap {
        val size = 100
        val output = Bitmap.createBitmap(size, size + 20, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val path = Path().apply {
            moveTo(size / 2f, 0f)
            quadTo(size.toFloat(), size.toFloat() / 2, size / 2f, (size + 20).toFloat())
            quadTo(0f, size.toFloat() / 2, size / 2f, 0f)
            close()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawPath(path, paint)

        val shader = BitmapShader(original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        canvas.drawPath(path, paint)

        return output
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}


