package com.training.companion.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.training.companion.R


class AuthFragment : Fragment() {

    private var callback: AuthFragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as AuthFragmentCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onStart() {
        super.onStart()
        val authButton = requireView().findViewById<Button>(R.id.login_auth_button)
        authButton.setOnClickListener {
            callback?.onAuthButtonClick()
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}

interface AuthFragmentCallback {
    fun onAuthButtonClick()
}