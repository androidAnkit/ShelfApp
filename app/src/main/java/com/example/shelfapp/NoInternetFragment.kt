package com.example.shelfapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.shelfapp.Util.Utility

class NoInternetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_no_internet, container, false)

        val activityName = arguments?.getString("activityName")
        val retry = view.findViewById<Button>(R.id.retry)

        retry.setOnClickListener {
            if(activity?.let { it1 -> Utility(it1).isNetworkAvailable() } == true) {
                when (activityName) {
                    "LoginActivity" -> {
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }

                    "SignUpActivity" -> {
                        val intent = Intent(activity, SignUpActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }

                    "BookListActivity" -> {
                        val intent = Intent(activity, BookListActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }

                    "BookDetailsActivity" -> {
                        val intent = Intent(activity, BookDetailsActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        }

        return view
    }
}