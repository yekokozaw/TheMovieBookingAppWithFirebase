package com.flexath.themoviebookingapp.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.signin.UserVO
import com.flexath.themoviebookingapp.network.auth.AuthManager
import com.flexath.themoviebookingapp.network.auth.FirebaseAuthManager
import com.flexath.themoviebookingapp.ui.activities.LoginScreenActivity
import com.flexath.themoviebookingapp.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile_home.*
import kotlinx.android.synthetic.main.layout_privacy_policy_dialog.btnClosePrivacyPolicy


class ProfileHomeFragment : Fragment() {

    private val mCinemaModel:CinemaModel = CinemaModelImpl
    private val mAuthModel : AuthManager = FirebaseAuthManager
    private var mUser:UserVO? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
        requestData()
    }

    private fun requestData() {
        mCinemaModel.getUsers(
            onSuccess = {
                showUserInformation(it)
            },
            onFailure = {
                Toast.makeText(requireActivity(),it,Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun showUserInformation(userList: List<UserVO>){
        for (user in userList){
            if (mAuthModel.getUserId() == user.userId){
                mUser = user
                tvUserName.text = mUser?.userName
                tvUserEmail.text = mUser?.email
                tvUserPhoneNumber.text = mUser?.phoneNumber
            }
        }
    }
    private fun setUpListeners() {
        btnLogOutProfileHome.setOnClickListener {

            val dialog = MaterialAlertDialogBuilder(requireActivity(),R.style.RoundedAlertDialog)
                .setTitle("Log Out ?")
                .setMessage("Are you sure to log out ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    (activity as MainActivity).finish()

                    Toast.makeText(requireActivity(),"Logout call succeeded",Toast.LENGTH_SHORT).show()
                    mCinemaModel.deleteAllEntities()
                    Intent(requireActivity(),LoginScreenActivity::class.java).also {
                        startActivity(it)
                    }
                    (activity as MainActivity).finish()

                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
                .create()
            dialog.show()
        }

        btnPrivacyPolicy.setOnClickListener {
            val dialog = BottomSheetDialog(requireActivity())
            val dialogView = layoutInflater.inflate(
                R.layout.layout_privacy_policy_dialog,
                null,
                false
            )
            dialog.setContentView(dialogView)
            dialog.setCancelable(true)
            dialog.show()

            dialog.btnClosePrivacyPolicy.setOnClickListener {
                dialog.dismiss()
            }
        }

        btnHelpAndSupportProfileHome.setOnClickListener {
            val dialog2 = BottomSheetDialog(requireActivity())
            val dialogView = layoutInflater.inflate(
                R.layout.layout_help_and_support_dialog,
                null,
                false
            )
            dialog2.setContentView(dialogView)
            dialog2.setCancelable(true)
            dialog2.show()

            dialog2.btnClosePrivacyPolicy.setOnClickListener {
                dialog2.dismiss()
            }
        }
    }
}