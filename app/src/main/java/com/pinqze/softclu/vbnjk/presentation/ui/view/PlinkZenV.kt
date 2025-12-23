package com.pinqze.softclu.vbnjk.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication
import com.pinqze.softclu.vbnjk.presentation.ui.load.PlinkZenLoadFragment
import org.koin.android.ext.android.inject

class PlinkZenV : Fragment(){

    private lateinit var plinkZenPhoto: Uri
    private var plinkZenFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val plinkZenTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        plinkZenFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        plinkZenFilePathFromChrome = null
    }

    private val plinkZenTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            plinkZenFilePathFromChrome?.onReceiveValue(arrayOf(plinkZenPhoto))
            plinkZenFilePathFromChrome = null
        } else {
            plinkZenFilePathFromChrome?.onReceiveValue(null)
            plinkZenFilePathFromChrome = null
        }
    }

    private val plinkZenDataStore by activityViewModels<PlinkZenDataStore>()


    private val plinkZenViFun by inject<PlinkZenViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (plinkZenDataStore.plinkZenView.canGoBack()) {
                        plinkZenDataStore.plinkZenView.goBack()
                        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "WebView can go back")
                    } else if (plinkZenDataStore.plinkZenViList.size > 1) {
                        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "WebView can`t go back")
                        plinkZenDataStore.plinkZenViList.removeAt(plinkZenDataStore.plinkZenViList.lastIndex)
                        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "WebView list size ${plinkZenDataStore.plinkZenViList.size}")
                        plinkZenDataStore.plinkZenView.destroy()
                        val previousWebView = plinkZenDataStore.plinkZenViList.last()
                        plinkZenAttachWebViewToContainer(previousWebView)
                        plinkZenDataStore.plinkZenView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (plinkZenDataStore.plinkZenIsFirstCreate) {
            plinkZenDataStore.plinkZenIsFirstCreate = false
            plinkZenDataStore.plinkZenContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return plinkZenDataStore.plinkZenContainerView
        } else {
            return plinkZenDataStore.plinkZenContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "onViewCreated")
        if (plinkZenDataStore.plinkZenViList.isEmpty()) {
            plinkZenDataStore.plinkZenView = PlinkZenVi(requireContext(), object :
                PlinkZenCallBack {
                override fun plinkZenHandleCreateWebWindowRequest(plinkZenVi: PlinkZenVi) {
                    plinkZenDataStore.plinkZenViList.add(plinkZenVi)
                    Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "WebView list size = ${plinkZenDataStore.plinkZenViList.size}")
                    Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "CreateWebWindowRequest")
                    plinkZenDataStore.plinkZenView = plinkZenVi
                    plinkZenVi.plinkZenSetFileChooserHandler { callback ->
                        plinkZenHandleFileChooser(callback)
                    }
                    plinkZenAttachWebViewToContainer(plinkZenVi)
                }

            }, plinkZenWindow = requireActivity().window).apply {
                plinkZenSetFileChooserHandler { callback ->
                    plinkZenHandleFileChooser(callback)
                }
            }
            plinkZenDataStore.plinkZenView.plinkZenFLoad(arguments?.getString(
                PlinkZenLoadFragment.PLINK_ZEN_D) ?: "")
//            ejvview.fLoad("www.google.com")
            plinkZenDataStore.plinkZenViList.add(plinkZenDataStore.plinkZenView)
            plinkZenAttachWebViewToContainer(plinkZenDataStore.plinkZenView)
        } else {
            plinkZenDataStore.plinkZenViList.forEach { webView ->
                webView.plinkZenSetFileChooserHandler { callback ->
                    plinkZenHandleFileChooser(callback)
                }
            }
            plinkZenDataStore.plinkZenView = plinkZenDataStore.plinkZenViList.last()

            plinkZenAttachWebViewToContainer(plinkZenDataStore.plinkZenView)
        }
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "WebView list size = ${plinkZenDataStore.plinkZenViList.size}")
    }

    private fun plinkZenHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        plinkZenFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Launching file picker")
                    plinkZenTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Launching camera")
                    plinkZenPhoto = plinkZenViFun.plinkZenSavePhoto()
                    plinkZenTakePhoto.launch(plinkZenPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                plinkZenFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun plinkZenAttachWebViewToContainer(w: PlinkZenVi) {
        plinkZenDataStore.plinkZenContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            plinkZenDataStore.plinkZenContainerView.removeAllViews()
            plinkZenDataStore.plinkZenContainerView.addView(w)
        }
    }


}