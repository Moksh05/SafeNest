package com.example.safenest.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safenest.R
import com.example.safenest.adapters.SafePlacesAdapter
import com.example.safenest.models.SafePlace
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SafePlacesBottomSheet(list: MutableList<SafePlace>) : BottomSheetDialogFragment() {
    private lateinit var listener: BottomSheetListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SafePlacesAdapter
    private var nearbyPlacesList: MutableList<SafePlace> = list

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.safe_places_bottom_sheet, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SafePlacesAdapter(nearbyPlacesList) { safePlace ->
            openGoogleMaps(safePlace)
        }
        recyclerView.adapter = adapter

        val closeButton: Button = view.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun openGoogleMaps(safePlace: SafePlace) {
        val gmmIntentUri = Uri.parse("geo:${safePlace.lat},${safePlace.lng}?q=${safePlace.name}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }


//    private fun generateDummySafePlaces(): List<SafePlace> {
//        return listOf(
//            SafePlace(
//                name = "Community Health Centre",
//                icon = "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/hospital-71.png",
//                lat = 31.4799525,
//                lng = 76.1738829,
//                placeId = "ChIJt8sMwnbcGjkRJQ2xVPrBmD0"
//            ),
//            SafePlace(
//                name = "Local Hospital",
//                icon = "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/hospital-71.png",
//                lat = 31.4812515,
//                lng = 76.1752343,
//                placeId = "ChIJxyzMwnbcGjkRJQ2xVPrBmD0"
//            ),
//            SafePlace(
//                name = "Fire Station",
//                icon = "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/firestation-71.png",
//                lat = 31.4785535,
//                lng = 76.1725363,
//                placeId = "ChIJabcMwnbcGjkRJQ2xVPrBmD0"
//            )
//        )
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomSheetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
    }

    interface BottomSheetListener {
        fun onButtonClicked(input: String)
    }
}
