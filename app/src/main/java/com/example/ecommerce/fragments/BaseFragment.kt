package com.myshoppal.ui.fragments

import android.app.Dialog
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.google.android.material.snackbar.Snackbar

/**
 * A base fragment class is used to define the functions and members which we will use in all the fragments.
 * It inherits the Fragment class so in other fragment class we will replace the Fragment with BaseFragment.
 */
open class BaseFragment : Fragment() {

    /**
     * This is a progress dialog instance which we will initialize later on.
     */
    private lateinit var progressDialog: Dialog
    private lateinit var tvProgressText: TextView
    // END

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        progressDialog = Dialog(requireActivity())
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(false)

        tvProgressText = progressDialog.findViewById(R.id.tv_progress_text)

        tvProgressText.text = text

        progressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun showSnackBar(message : String, errorMessage : Boolean){
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSnackBarError))
        }
        else{
            snackBarView.background = resources.getDrawable(R.color.colorSnackBarSuccess)
        }
        snackBar.show()

    }
}