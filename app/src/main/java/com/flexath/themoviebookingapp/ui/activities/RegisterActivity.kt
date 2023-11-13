package com.flexath.themoviebookingapp.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.AuthenticationModel
import com.flexath.themoviebookingapp.data.model.AuthenticationModelImpl
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val mAuthenticationModel: AuthenticationModel = AuthenticationModelImpl
    private val mCloudFirestoreModel : CinemaModel = CinemaModelImpl

    companion object {
        fun newIntentFromRegisterScreen(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setUpListeners()
    }

    private fun setUpListeners(){
        btnRegister.setOnClickListener {
            val userEmail = edtRegisterEmail.text.toString()
            val userPassword = edtRegisterPassword.text.toString()
            val userPhoneNumber = edtPhoneNumber.text.toString()
            val userName = edtUsername.text.toString()

            if (userEmail.isEmpty()|| userPassword.isEmpty()||userPhoneNumber.isEmpty()){
                Toast.makeText(this,"Please Enter All Fields", Toast.LENGTH_LONG).show()
            }
            else{
                if (isValidPhoneNumber(userPhoneNumber) && isValidEmail(userEmail)){
                    if (userPassword.length < 7){
                        Toast.makeText(this,"Password should be at least 8 characters long", Toast.LENGTH_LONG).show()
                    }
                    else{
                        mAuthenticationModel.register(
                            userName = userName,
                            email = userEmail,
                            password = edtRegisterPassword.text.toString(),
                            phoneNumber = userPhoneNumber,
                            onSuccess = {
                                mCloudFirestoreModel.addUser(it)
                                startActivity(MainActivity.newIntentFromMainActivity(this))
                            },
                            onFailure = {
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                else Toast.makeText(this,"Email or phone number is invalid", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }


    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Remove any non-digit characters from the phone number
        val digitsOnly = phoneNumber.replace("\\D".toRegex(), "")

        // Check if the phone number has a maximum length of 11
        return digitsOnly.length <= 11
    }

}