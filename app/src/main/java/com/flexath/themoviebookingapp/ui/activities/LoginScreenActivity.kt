package com.flexath.themoviebookingapp.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.AuthenticationModel
import com.flexath.themoviebookingapp.data.model.AuthenticationModelImpl
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.signin.TokenVO
import com.flexath.themoviebookingapp.network.auth.AuthManager
import com.flexath.themoviebookingapp.network.auth.FirebaseAuthManager
import com.flexath.themoviebookingapp.ui.helpers.MobileNumberValidationChecker
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.activity_login_screen.*


class LoginScreenActivity : AppCompatActivity() {

    private val mMovieModel: CinemaModel = CinemaModelImpl
    private val mAuthenticationModel: AuthenticationModel = AuthenticationModelImpl
    private val mAuthModel : AuthManager = FirebaseAuthManager

    private lateinit var mMobileNumberValidationChecker: MobileNumberValidationChecker

    companion object {
        fun newIntentFromLoginScreen(context: Context): Intent {
            return Intent(context, LoginScreenActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        setUpListeners()

    }

//    private fun onClickVerifyPhoneNumberButton() {
//        rlVerifyPhoneNumber.setOnClickListener {
//
//            val mobileNumber = etMobileNumber.editText?.text.toString()
//            mMobileNumberValidationChecker = MobileNumberValidationChecker(mobileNumber)
//
//            if(isValidatedMobileNumber()){
//                etMobileNumber.error = null
//                etMobileNumber.isErrorEnabled = false
//                ccpLogin.registerCarrierNumberEditText(etMobileNumber?.editText)
//                mMovieModel.sendOTP(
//                    ccpLogin.fullNumber,
//                    onSuccess = {
//                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
//                        startActivity(OtpScreenActivity.newIntentFromOtpScreen(this, ccpLogin.fullNumber))
//                        finish()
//                    },
//                    onFailure = {
//                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//                    }
//                )
//            }else{
//                etMobileNumber.error = mMobileNumberValidationChecker.mobileNumberError
//                outlineMobileNumber.visibility = View.INVISIBLE
//            }
//        }
//
//        rlVerifyGoogleAccount.setOnClickListener {
//            Intent(Intent.ACTION_VIEW).also {
//                startActivity(it)
//            }
//        }
//    }

    private fun setUpListeners(){
        btnLogin.setOnClickListener {
            if (edtEmailAddress.text.toString().isEmpty()|| edtPassword.text.toString().isEmpty()){
                StyleableToast.makeText(this,"Please Enter All Fields",Toast.LENGTH_LONG,R.style.myErrorToast).show()
            }
            else {
                mAuthenticationModel.login(
                    edtEmailAddress.text.toString(),
                    edtPassword.text.toString(),
                    onSuccess = {
                        mMovieModel.addToken(TokenVO(mAuthModel.getUserId()))
                        startActivity(MainActivity.newIntentFromMainActivity(this))
                    },
                    onFailure = {
                        StyleableToast.makeText(this,it,Toast.LENGTH_LONG,R.style.myErrorToast).show()
                    }
                )
            }
        }

        tvForgetPassword.setOnClickListener {
            val userEmail = edtEmailAddress.text.toString()
            if (userEmail.isEmpty()){
                StyleableToast.makeText(this,"Enter Email Address",Toast.LENGTH_LONG,R.style.myErrorToast).show()
            }
            else{
                mAuthenticationModel.forgetPassword(
                    userEmail,
                    onSuccess = {

                        StyleableToast.makeText(this,"Check your email address",Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        StyleableToast.makeText(this,it,Toast.LENGTH_LONG,R.style.myErrorToast).show()
                    }
                )
            }
        }

        btnRegister.setOnClickListener {
            startActivity(RegisterActivity.newIntentFromRegisterScreen(this))
        }
    }

    private fun isValidatedMobileNumber(): Boolean {
        return mMobileNumberValidationChecker.isValidatedMobileNumber()
    }
}