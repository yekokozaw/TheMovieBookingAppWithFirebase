package com.flexath.themoviebookingapp.ui.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.activity_logo_screen.pbLoading
import kotlinx.android.synthetic.main.activity_logo_screen.tvProgressBar

class LogoScreenActivity : AppCompatActivity() {

    private val mCinemaModel: CinemaModel = CinemaModelImpl

    private var progressAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_screen)

        progressAnimator = ValueAnimator.ofInt(0, 100)
        progressAnimator?.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            pbLoading.progress = progress
            tvProgressBar.text = "$progress%"
        }
        progressAnimator?.duration = 3000
        progressAnimator?.start()

        setUpNetworkCall()

        Handler(Looper.getMainLooper()).postDelayed({
            if(mCinemaModel.getToken()?.token?.isNotEmpty() == true) {
                MainActivity.newIntentFromMainActivity(this).also {
                    startActivity(it)
                }
            } else {
                LoginScreenActivity.newIntentFromLoginScreen(this).also {
                    startActivity(it)
                    finish()
                }
            }
        },3000)
    }


    override fun onRestart() {
        super.onRestart()
        finish()
    }

    private fun setUpNetworkCall() {
        mCinemaModel.insertCities(
            onSuccess = {

            },
            onFailure = {
                //StyleableToast.makeText(this,"Cities fail",Toast.LENGTH_SHORT,R.style.myErrorToast).show()
            }
        )

        mCinemaModel.insertCinemaConfig(
            onSuccess = {
                //StyleableToast.makeText(this,"Config Success",Toast.LENGTH_LONG,R.style.myErrorToast).show()
            },
            onFailure = {
                Log.i("ConfigATH",it)
                //StyleableToast.makeText(this,"Config Fail",Toast.LENGTH_SHORT,R.style.myErrorToast).show()
            }
        )

        mCinemaModel.insertCinemaInfo(
            onSuccess = {

            },
            onFailure = {
                //StyleableToast.makeText(this,"Cinema Info Fail",Toast.LENGTH_SHORT,R.style.myErrorToast).show()
            }
        )
    }

    override fun onDestroy() {
        progressAnimator?.cancel()
        super.onDestroy()
    }

}