package com.pinqze.softclu.vbnjk.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pinqze.softclu.MainActivity
import com.pinqze.softclu.R
import com.pinqze.softclu.databinding.FragmentLoadPlinkZenBinding
import com.pinqze.softclu.vbnjk.data.shar.PlinkZenSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlinkZenLoadFragment : Fragment(R.layout.fragment_load_plink_zen) {
    private lateinit var plinkZenLoadBinding: FragmentLoadPlinkZenBinding

    private val plinkZenLoadViewModel by viewModel<PlinkZenLoadViewModel>()

    private val plinkZenSharedPreference by inject<PlinkZenSharedPreference>()

    private var plinkZenUrl = ""

    private val plinkZenRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            plinkZenNavigateToSuccess(plinkZenUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                plinkZenSharedPreference.plinkZenNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                plinkZenNavigateToSuccess(plinkZenUrl)
            } else {
                plinkZenNavigateToSuccess(plinkZenUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plinkZenLoadBinding = FragmentLoadPlinkZenBinding.bind(view)

        plinkZenLoadBinding.plinkZenGrandButton.setOnClickListener {
            val plinkZenPermission = Manifest.permission.POST_NOTIFICATIONS
            plinkZenRequestNotificationPermission.launch(plinkZenPermission)
            plinkZenSharedPreference.plinkZenNotificationRequestedBefore = true
        }

        plinkZenLoadBinding.plinkZenSkipButton.setOnClickListener {
            plinkZenSharedPreference.plinkZenNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            plinkZenNavigateToSuccess(plinkZenUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                plinkZenLoadViewModel.plinkZenHomeScreenState.collect {
                    when (it) {
                        is PlinkZenLoadViewModel.PlinkZenHomeScreenState.PlinkZenLoading -> {

                        }

                        is PlinkZenLoadViewModel.PlinkZenHomeScreenState.PlinkZenError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is PlinkZenLoadViewModel.PlinkZenHomeScreenState.PlinkZenSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val plinkZenPermission = Manifest.permission.POST_NOTIFICATIONS
                                val plinkZenPermissionRequestedBefore = plinkZenSharedPreference.plinkZenNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), plinkZenPermission) == PackageManager.PERMISSION_GRANTED) {
                                    plinkZenNavigateToSuccess(it.data)
                                } else if (!plinkZenPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > plinkZenSharedPreference.plinkZenNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    plinkZenLoadBinding.plinkZenNotiGroup.visibility = View.VISIBLE
                                    plinkZenLoadBinding.plinkZenLoadingGroup.visibility = View.GONE
                                    plinkZenUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(plinkZenPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > plinkZenSharedPreference.plinkZenNotificationRequest) {
                                        plinkZenLoadBinding.plinkZenNotiGroup.visibility = View.VISIBLE
                                        plinkZenLoadBinding.plinkZenLoadingGroup.visibility = View.GONE
                                        plinkZenUrl = it.data
                                    } else {
                                        plinkZenNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    plinkZenNavigateToSuccess(it.data)
                                }
                            } else {
                                plinkZenNavigateToSuccess(it.data)
                            }
                        }

                        PlinkZenLoadViewModel.PlinkZenHomeScreenState.PlinkZenNotInternet -> {
                            plinkZenLoadBinding.plinkZenStateGroup.visibility = View.VISIBLE
                            plinkZenLoadBinding.plinkZenLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun plinkZenNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_plinkZenLoadFragment_to_plinkZenV,
            bundleOf(PLINK_ZEN_D to data)
        )
    }

    companion object {
        const val PLINK_ZEN_D = "plinkZenData"
    }
}