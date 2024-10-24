package com.example.safenest

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView


class education : Fragment() {

    private lateinit var selfdefence : CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_education, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selfdefence = view.findViewById(R.id.self_defence)
        selfdefence.setOnClickListener {
            val selfdenfenceintent = Intent(requireContext(),self_defence_Activity::class.java)
            startActivity(selfdenfenceintent)
        }
    }
}