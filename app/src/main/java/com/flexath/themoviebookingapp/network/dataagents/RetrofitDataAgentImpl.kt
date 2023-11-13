package com.flexath.themoviebookingapp.network.dataagents

import com.flexath.themoviebookingapp.data.vos.location.CitiesVO
import com.flexath.themoviebookingapp.data.vos.movie.BannersVO
import com.flexath.themoviebookingapp.data.vos.movie.CinemaInfoVO
import com.flexath.themoviebookingapp.data.vos.movie.MovieDetailsVO
import com.flexath.themoviebookingapp.data.vos.movie.MovieVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.CinemaVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.ConfigVO
import com.flexath.themoviebookingapp.data.vos.movie.SeatVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackCategoryVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackVO
import com.flexath.themoviebookingapp.data.vos.movie.PaymentVO
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.CheckoutBody
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.TicketCheckoutResponse
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.TicketCheckoutVO
import com.flexath.themoviebookingapp.data.vos.movie.VideoResponse
import com.flexath.themoviebookingapp.data.vos.movie.VideoVO
import com.flexath.themoviebookingapp.network.responses.SeatingPlanResponse
import com.flexath.themoviebookingapp.network.api.CinemaApi
import com.flexath.themoviebookingapp.network.firebase.FirebaseApi
import com.flexath.themoviebookingapp.network.firebase.RealtimeDatabaseFirebaseApiImpl
import com.flexath.themoviebookingapp.network.responses.*
import com.flexath.themoviebookingapp.network.utils.BASE_URL
import com.flexath.themoviebookingapp.network.utils.BASE_URL_TMDB
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitDataAgentImpl : CinemaDataAgent {

    private var mCinemaApi: CinemaApi? = null
    private var mCinemaApiTwo: CinemaApi? = null
    private val mFirebaseRealtime : FirebaseApi = RealtimeDatabaseFirebaseApiImpl

    init {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val retrofitTwo = Retrofit.Builder()
            .baseUrl(BASE_URL_TMDB)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        mCinemaApi = retrofit.create(CinemaApi::class.java)
        mCinemaApiTwo = retrofitTwo.create(CinemaApi::class.java)
    }

    override fun getCities(onSuccess: (List<CitiesVO>) -> Unit, onFailure: (String) -> Unit) {
        mCinemaApi?.getCities()
            ?.enqueue(object : Callback<CitiesListResponse> {
                override fun onResponse(
                    call: Call<CitiesListResponse>,
                    response: Response<CitiesListResponse>
                ) {
                    if (response.isSuccessful) {
                        val citiesList = response.body()?.data ?: listOf()
                        onSuccess(citiesList)
                    } else {
                        onFailure("Cities response failed")
                    }
                }

                override fun onFailure(call: Call<CitiesListResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun sendOTP(
        phone: String,
        onSuccess: (OTPResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.sendOTP(phone)
            ?.enqueue(object : Callback<OTPResponse> {
                override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        }
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun signInWithPhoneNumber(
        phone: String,
        otp: String,
        onSuccess: (OTPResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.signInWithPhoneNumber(phone, otp)
            ?.enqueue(object : Callback<OTPResponse> {
                override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        }
                    } else {
                        onFailure(response.body()?.message.toString())
                    }
                }

                override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun getBanners(onSuccess: (List<BannersVO>) -> Unit, onFailure: (String) -> Unit) {
        mFirebaseRealtime.getBanner(
            onSuccess= onSuccess,
            onFailure = onFailure
        )
    }

    override fun getNowPlayingMovies(
        onSuccess: (List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.getMovieList(
            onSuccess,onFailure
        )
    }

    override fun getComingSoonMovies(
        onSuccess: (List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.comingSoonList(
            onSuccess,onFailure
        )
    }

    override fun getMovieDetailsById(
        movieId: String,
        onSuccess: (MovieDetailsVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.getMovieDetailsById(movieId)
            ?.enqueue(object : Callback<MovieDetailsVO> {
                override fun onResponse(
                    call: Call<MovieDetailsVO>,
                    response: Response<MovieDetailsVO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        }
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<MovieDetailsVO>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun getMovieTrailerById(
        movieId: String,
        onSuccess: (List<VideoVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApiTwo?.getMovieTrailerById(movieId)
            ?.enqueue(object : Callback<VideoResponse> {
                override fun onResponse(
                    call: Call<VideoResponse>,
                    response: Response<VideoResponse>
                ) {
                    if (response.isSuccessful) {
                        val videoList = response.body()?.results ?: listOf()
                        onSuccess(videoList)
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun getCinemaTimeSlots(
        movieName: String,
        authorization: String,
        date: String,
        onSuccess: (List<CinemaVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.getCinemaTimeSlots(
            movieName,
            date,
            onSuccess = onSuccess,
            onFailure= onFailure
        )
    }

    override fun getCinemaConfig(onSuccess: (List<ConfigVO>) -> Unit, onFailure: (String) -> Unit) {
        mFirebaseRealtime.getConfig(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override fun getCinemaInfo(
        onSuccess: (List<CinemaInfoVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.getCinemaInfo()
            ?.enqueue(object : Callback<CinemaInfoResponse> {
                override fun onResponse(
                    call: Call<CinemaInfoResponse>,
                    response: Response<CinemaInfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val cinemaList = response.body()?.data ?: listOf()
                        onSuccess(cinemaList)
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<CinemaInfoResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun getSeatPlan(
        authorization: String,
        dayTimeSlotId: Int,
        bookingDate: String,
        onSuccess: (MutableList<MutableList<SeatVO>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.getSeatPlan(authorization, dayTimeSlotId, bookingDate)
            ?.enqueue(object : Callback<SeatingPlanResponse> {
                override fun onResponse(
                    call: Call<SeatingPlanResponse>,
                    response: Response<SeatingPlanResponse>
                ) {
                    if (response.isSuccessful) {
                        val seatList = response.body()?.data ?: mutableListOf()
                        onSuccess(seatList)
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<SeatingPlanResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun getSnackCategory(
        authorization: String,
        onSuccess: (List<SnackCategoryVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.getSnackCategory(
            onSuccess,onFailure
        )
    }

    override fun getSnackByCategory(
        authorization: String,
        categoryId: String,
        onSuccess: (List<SnackVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.getSnackByCategoryId(
            categoryId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override fun getPaymentTypes(
        authorization: String,
        onSuccess: (List<PaymentVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseRealtime.getPaymentTypes(
            onSuccess,
            onFailure
        )
    }

    override fun getTicketCheckout(
        authorization: String,
        ticketCheckout: CheckoutBody,
        onSuccess: (TicketCheckoutVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.getTicketCheckout(authorization, ticketCheckout)
            ?.enqueue(object : Callback<TicketCheckoutResponse> {
                override fun onResponse(
                    call: Call<TicketCheckoutResponse>,
                    response: Response<TicketCheckoutResponse>
                ) {
                    if (response.isSuccessful) {
                        val ticket = response.body()?.data
                        if (ticket != null) {
                            onSuccess(ticket)
                        }
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<TicketCheckoutResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }

    override fun logout(
        authorization: String,
        onSuccess: (LogoutResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mCinemaApi?.logout(authorization)
            ?.enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        }
                    } else {
                        onFailure("Don't make errors,Aung Thiha")
                    }
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    onFailure(t.message ?: "")
                }

            })
    }
}