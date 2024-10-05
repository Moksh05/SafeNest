package com.example.safenest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.safenest.adapters.Card
import com.example.safenest.adapters.SliderviewAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var cardAdapter: SliderviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the ViewPager2
        viewPager = rootView.findViewById(R.id.viewPager)

        // Create the card list
        val cardList = listOf(
            Card(R.drawable.login_img, "Home Card", "This is the Home card description."),
            Card(R.drawable.login_img, "Education Card", "This is the Education card description."),
            Card(R.drawable.login_img, "SOS Card", "This is the SOS card description."),
            Card(R.drawable.login_img, "Helpline Card", "This is the Helpline card description.")
        )

        // Set up the adapter
        cardAdapter = SliderviewAdapter(cardList)
        viewPager.adapter = cardAdapter

        return rootView
    }
    }


