package ovh.plrapps.mapview.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ovh.plrapps.mapview.MapView
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.addCallout
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.removeMarker
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.demo.R
import ovh.plrapps.mapview.demo.fragments.views.MapMarker
import ovh.plrapps.mapview.demo.fragments.views.MarkerCallout
import ovh.plrapps.mapview.markers.MarkerTapListener

/**
 * An example showing the simplest usage of [MapView].
 */
class MapAloneFragment : Fragment() {
    private lateinit var parentView: ViewGroup
    private val mapView:MapView?=null

    /**
     * The [MapView] should always be added inside [onCreateView], to ensure state save/restore.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment, container, false).also {
            parentView = it as ViewGroup
            configureMapView(it)
        }
    }

    /**
     * In this example, the configuration is done **immediately** after the [MapView] is added to
     * the view hierarchy, in [onCreateView].
     * But it's not mandatory, it could have been done later on. However, beware to configure only once.
     */
    private fun configureMapView(view: View) {
        val mapView = view.findViewById<MapView>(R.id.mapview) ?: return
        val tileStreamProvider = TileStreamProvider { row, col, zoomLvl ->
            try {
                view.context.assets?.open("esp2/$zoomLvl/$row/$col.jpg")
            } catch (e: Exception) {
                null
            }
        }
        val tileSize = 256
        val config = MapViewConfiguration(
                7, 15360, 8640, tileSize, tileStreamProvider
        ).setMaxScale(3f)

        mapView.configure(config)
        mapView.defineBounds(0.0, 0.0, 1.0, 1.0)

        addNewMarker(mapView, 0.595, 0.56, "Residential Building")
        addNewMarker(mapView, 0.35, 0.506, "Street View")

     //   var specialMarker = addSpecialMarker(mapView)


        /* When a marker is tapped, we want to show a callout view */
        mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                if (view is MapMarker) {
                    val callout = MarkerCallout(mapView.context)
                    callout.setTitle(view.name)
                    callout.setSubTitle("position: ${view.x} , ${view.y}")
                    mapView.addCallout(callout, view.x, view.y, -0.5f, -1.2f, 0f, 0f)
                    callout.transitionIn()
                }
            }
        })

        /* Below is the configuration of the button to add/remove the special marker */


    }
    private fun addSpecialMarker(mapView: MapView): MapMarker {
        val x = 0.6
        val y = 0.4
        val marker = MapMarker(requireContext(), x, y, "special marker").apply {
            setColorFilter(ContextCompat.getColor(this.context, R.color.colorAccent))
            setImageResource(R.drawable.map_marker_circle)
        }

        /* Since the marker is circular, we want to center it on the position. So we use 0.5f as
         * relative anchors */
        mapView.addMarker(marker, x, y, relativeAnchorLeft = -0.5f, relativeAnchorTop = -0.5f)
        return marker
    }

    private fun addNewMarker(mapView: MapView, x: Double, y: Double, name: String) {
        val marker = MapMarker(requireContext(), x, y, name).apply {
            setImageResource(R.drawable.map_marker)
        }

        mapView.addMarker(marker, x, y)
    }
}
private const val ADD_MARKER = "Add marker"
private const val REMOVE_MARKER = "Remove marker"