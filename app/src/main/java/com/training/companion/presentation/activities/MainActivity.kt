package com.training.companion.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.training.companion.R
import com.training.companion.databinding.ActivityMainBinding
import com.training.companion.presentation.fragments.toplevel.SettingsFragment


class MainActivity : AppCompatActivity(), SettingsFragment.LogoutCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        navController = findNavController(R.id.main_fragment_container)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.powerWorkoutPlaceFragment -> hideBottomNavigation()
                R.id.planChoiceFragment -> hideBottomNavigation()
                R.id.powerWorkoutScenarioFragment -> hideBottomNavigation()
                R.id.powerWorkoutRestTimeFragment -> hideBottomNavigation()
                R.id.planEditFragment -> hideBottomNavigation()
                R.id.planDetailsFragment -> hideBottomNavigation()
                R.id.exerciseChoiceFragment -> hideBottomNavigation()
                R.id.exerciseDetailsFragment -> hideBottomNavigation()
                R.id.newPlanTypeFragment -> hideBottomNavigation()
                R.id.equipmentFilterFragment -> hideBottomNavigation()
                R.id.workoutTypeChoiceFragment -> hideBottomNavigation()
                R.id.setCompletionBottomSheet -> hideBottomNavigation()
                R.id.exerciseRestTimeBottomSheet -> hideBottomNavigation()
                R.id.exerciseSetsBottomSheet -> hideBottomNavigation()
                R.id.exerciseRepsPlanEditBottomSheet -> hideBottomNavigation()
                R.id.exerciseWeightBottomSheet -> hideBottomNavigation()
                R.id.exerciseDurationBottomSheet -> hideBottomNavigation()
                R.id.workoutResultsFragment -> hideBottomNavigation()
                R.id.suspenseFragment -> hideBottomNavigation()
                else -> {
                    showBottomNavigation()
                }
            }
        }
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigation.isVisible = false
    }

    private fun showBottomNavigation() {
        binding.bottomNavigation.isVisible = true
    }
}