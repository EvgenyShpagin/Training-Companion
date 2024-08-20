package com.training.companion.presentation.fragments.toplevel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.training.companion.R
import com.training.companion.data.repositories.AppSettings
import com.training.companion.data.repositories.AppSettings.editSoundMode
import com.training.companion.data.repositories.AppSettings.editVibrationMode
import com.training.companion.databinding.FragmentSettingsBinding
import com.training.companion.presentation.dialogs.UnitsBottomSheet
import com.training.companion.presentation.viewmodels.SettingsObservable


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsObservable = SettingsObservable()
    private var auth: FirebaseAuth? = null
    private var logoutCallback: LogoutCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logoutCallback = context as LogoutCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        Glide.with(this).load(auth!!.currentUser?.photoUrl).into(binding.userIcon)
    }

    override fun onStart() {
        super.onStart()

//        binding.workoutReminder.setOnClickListener {
//
//        }
//
//        binding.extraConfirmAction.setOnClickListener {
//
//        }
//
//        binding.language.setOnClickListener {
//
//        }

        binding.units.setOnClickListener {
            UnitsBottomSheet().show(childFragmentManager, null)
        }

        binding.sound.setOnClickListener {
            editSoundMode(!AppSettings.soundIsOn)
        }

        binding.vibration.setOnClickListener {
            editVibrationMode(!AppSettings.vibrationIsOn)
        }

//        binding.clearData.setOnClickListener {
//
//        }

        binding.logout.setOnClickListener {
            showSubmitLogoutDialog {
                auth!!.signOut()
                logoutCallback?.onLogout()
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        logoutCallback = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSubmitLogoutDialog(onSubmit: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.setting_logout)
            .setMessage(R.string.setting_logout_submit_message)
            .setPositiveButton(R.string.submit) { _, _ ->
                onSubmit.invoke()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    interface LogoutCallback {
        fun onLogout()
    }
}