package co.edu.udea.compumovil.gr07_20251.udeafood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.widget.Button
import android.widget.LinearLayout




class PickLocationFragment : Fragment() {
    private lateinit var map: MapView

    companion object {
        var selectedLocation: GeoPoint? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))
        return inflater.inflate(R.layout.fragment_pick_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        map = view.findViewById(R.id.map_location_picker)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val defaultCenter = GeoPoint(6.2675, -75.5686)
        map.controller.setZoom(18.0)
        map.controller.setCenter(defaultCenter)

        val confirmLayout = view.findViewById<LinearLayout>(R.id.confirmation_buttons)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm_location)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_location)

        var marker: Marker? = null

        map.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val proj = map.projection
                val point = proj.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                // Guardar ubicación
                selectedLocation = point

                // Mostrar marcador
                marker?.let { map.overlays.remove(it) }
                marker = Marker(map).apply {
                    position = point
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Ubicación seleccionada"
                    showInfoWindow()
                }
                map.overlays.add(marker)
                map.invalidate()

                confirmLayout.post {
                    confirmLayout.visibility = View.VISIBLE

                    val markerOffsetY = 50
                    val offsetX = confirmLayout.width / 2

                    confirmLayout.translationX = event.x - offsetX
                    confirmLayout.translationY = event.y + markerOffsetY
                }


            }
            true
        }

        btnConfirm.setOnClickListener {

            findNavController().popBackStack() // Volver a registro
        }

        btnCancel.setOnClickListener {
            selectedLocation = null
            confirmLayout.visibility = View.GONE
            marker?.let {
                map.overlays.remove(it)
                map.invalidate()
            }
        }
    }


}