package com.flexath.themoviebookingapp.data.model

import android.content.Context
import com.flexath.themoviebookingapp.data.vos.location.CitiesVO
import com.flexath.themoviebookingapp.data.vos.movie.*
import com.flexath.themoviebookingapp.data.vos.movie.cinema.CinemaVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.ConfigVO
import com.flexath.themoviebookingapp.data.vos.movie.SeatVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackCategoryVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackVO
import com.flexath.themoviebookingapp.data.vos.movie.PaymentVO
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.CheckoutBody
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.TicketCheckoutVO
import com.flexath.themoviebookingapp.data.vos.movie.VideoVO
import com.flexath.themoviebookingapp.data.vos.signin.TokenVO
import com.flexath.themoviebookingapp.data.vos.signin.UserVO
import com.flexath.themoviebookingapp.data.vos.ticket.BookingVO
import com.flexath.themoviebookingapp.data.vos.ticket.TicketInformation
import com.flexath.themoviebookingapp.network.dataagents.CinemaDataAgent
import com.flexath.themoviebookingapp.network.dataagents.RetrofitDataAgentImpl
import com.flexath.themoviebookingapp.network.firebase.CloudFirestoreFirebaseApi
import com.flexath.themoviebookingapp.network.firebase.CloudFirestoreFirebseApiImpl
import com.flexath.themoviebookingapp.network.firebase.FirebaseApi
import com.flexath.themoviebookingapp.network.firebase.RealtimeDatabaseFirebaseApiImpl
import com.flexath.themoviebookingapp.network.responses.LogoutResponse
import com.flexath.themoviebookingapp.network.responses.OTPResponse
import com.flexath.themoviebookingapp.persistence.CinemaRoomDatabase

object CinemaModelImpl : CinemaModel {

    private val mMovieDataAgent: CinemaDataAgent = RetrofitDataAgentImpl
    private var mCinemaDatabase: CinemaRoomDatabase? = null
    private var mMovie: MovieDetailsVO? = null
    private var snackList: MutableList<SnackVO> = mutableListOf()
    override var mFirebaseApi: FirebaseApi = RealtimeDatabaseFirebaseApiImpl
    override var mCloudFirestoreApi: CloudFirestoreFirebaseApi = CloudFirestoreFirebseApiImpl

    override fun addUser(user: UserVO) {
        mCloudFirestoreApi.addUser(user)
    }

    override fun getUsers(onSuccess: (users: List<UserVO>) -> Unit, onFailure: (String) -> Unit) {
        mCloudFirestoreApi.getUsers(onSuccess,onFailure)
    }

    fun initCinemaDatabase(context: Context) {
        mCinemaDatabase = CinemaRoomDatabase.getDBInstance(context)
    }

    override fun insertCities(
        onSuccess: (List<CitiesVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getCities(onSuccess = {
            mCinemaDatabase?.getDao()?.insertCites(it)
            onSuccess(it)
        }, onFailure)
    }

    override fun getCities() = mCinemaDatabase?.getDao()?.getAllCities()

    override fun sendOTP(
        phone: String,
        onSuccess: (OTPResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.sendOTP(phone, onSuccess = onSuccess, onFailure = onFailure)
    }

    override fun signInWithPhoneNumber(
        phone: String,
        otp: String,
        onSuccess: (OTPResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.signInWithPhoneNumber(
            phone, otp,
            onSuccess = {
                mCinemaDatabase?.getDao()?.insertSignInInformation(it)
                onSuccess(it)
            },
            onFailure = onFailure
        )
    }

    override fun getOtp(code: Int) = mCinemaDatabase?.getDao()?.getSignInInformation(code)
    override fun getToken() = mCinemaDatabase?.getDao()?.getToken()

    override fun addToken(token: TokenVO) {
        mCinemaDatabase?.getDao()?.addToken(token)
    }

    override fun getBanners(onSuccess: (List<BannersVO>) -> Unit, onFailure: (String) -> Unit) {
        mMovieDataAgent.getBanners(onSuccess = {
            onSuccess(it)
        }, onFailure = onFailure)
    }

    override fun getNowPlayingMovies(
        onSuccess: (List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        onSuccess(mCinemaDatabase?.getDao()?.getMoviesByType(NOW_PLAYING_MOVIE) ?: listOf())
        mMovieDataAgent.getNowPlayingMovies(onSuccess = {

            it.forEach { movie ->
                movie.type = NOW_PLAYING_MOVIE
            }
            mCinemaDatabase?.getDao()?.insertMovies(it)
            onSuccess(it)
        }, onFailure = onFailure)
    }

    override fun getComingSoonMovies(
        onSuccess: (List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        onSuccess(mCinemaDatabase?.getDao()?.getMoviesByType(COMING_SOON_MOVIE) ?: listOf())
        mMovieDataAgent.getComingSoonMovies(onSuccess = {

            it.forEach { movie ->
                movie.type = COMING_SOON_MOVIE
            }
            mCinemaDatabase?.getDao()?.insertMovies(it)
            onSuccess(it)
        }, onFailure = onFailure)
    }

    override fun getMovieDetailsById(
        movieId: String,
        onSuccess: (MovieDetailsVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
//        mCinemaDatabase?.getDao()?.getMovieById(movieId = movieId.toInt())?.let {
//            onSuccess(it)
//        }

        mMovieDataAgent.getMovieDetailsById(movieId, onSuccess = { movie ->
            movie.genres?.let {

            }
            mMovie = movie
            //val db = mCinemaDatabase?.getDao()?.getMovieById(movieId = movieId.toInt())
            //movie.type = db?.type.toString()
            //mCinemaDatabase?.getDao()?.insertSingleMovie(movie)
            onSuccess(movie)
        }, onFailure = onFailure)
    }

    override fun getMovieTrailerById(
        movieId: String,
        onSuccess: (List<VideoVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getMovieTrailerById(movieId, onSuccess, onFailure)
    }

    override fun getMovieByIdForTicket(movieId: String) = mMovie

    override fun getCinemaTimeSlots(
        movieName: String,
        authorization: String,
        date: String,
        onSuccess: (List<CinemaVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getCinemaTimeSlots(
            movieName = movieName,
            authorization = authorization,
            date = date,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override fun insertCinemaConfig(
        onSuccess: (List<ConfigVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getCinemaConfig(onSuccess = {
            mCinemaDatabase?.getDao()?.insertCinemaConfig(it)
            onSuccess(it)
        }, onFailure = onFailure)
    }

    override fun getCinemaConfig(key: String) = mCinemaDatabase?.getDao()?.getCinemaConfig(key)

    override fun insertCinemaInfo(
        onSuccess: (List<CinemaInfoVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getCinemaInfo(onSuccess = {
            mCinemaDatabase?.getDao()?.insertCinemaInfo(it)
            onSuccess(it)
        }, onFailure = onFailure)
    }

    override fun getCinemaInfo(cinemaId: Int,onSuccess: (CinemaDetailsVO) -> Unit,onFailure: (String) -> Unit){
        mFirebaseApi.cinemaDetails(
            cinemaId,
            onSuccess,
            onFailure
        )
    }

    override fun getSeatPlan(
        authorization: String,
        dayTimeSlotId: Int,
        bookingDate: String,
        onSuccess: (MutableList<MutableList<SeatVO>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getSeatPlan(authorization, dayTimeSlotId, bookingDate, onSuccess, onFailure)
    }

    //Seat Plan with Firebase
    override fun getSeatList(
        movieName: String,
        bookingDate: String,
        timeSlotId : String,
        onSuccess: (groceries: MutableList<SeatVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseApi.getSeatList(movieName,bookingDate,timeSlotId,onSuccess,onFailure)
    }

    override fun getSnackCategory(
        authorization: String,
        onSuccess: (List<SnackCategoryVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getSnackCategory(authorization, onSuccess, onFailure)
    }

    override fun getSnackByCategory(
        authorization: String,
        categoryId: String,
        onSuccess: (List<SnackVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getSnackByCategory(authorization, categoryId, onSuccess = {
            snackList = it as MutableList
            onSuccess(it)
        }, onFailure)
    }

    override fun getSnackList(): MutableList<SnackVO> = snackList

    override fun getPaymentTypes(
        authorization: String,
        onSuccess: (List<PaymentVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getPaymentTypes(authorization, onSuccess, onFailure)
    }

    override fun getTicketCheckout(
        authorization: String,
        ticketCheckout: CheckoutBody,
        onSuccess: (TicketCheckoutVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.getTicketCheckout(authorization, ticketCheckout, onSuccess, onFailure)
    }

    override fun logout(
        authorization: String,
        onSuccess: (LogoutResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mMovieDataAgent.logout(authorization, onSuccess, onFailure)
    }

    override fun deleteAllEntities() {
        mCinemaDatabase?.getDao()?.deleteAllEntities()
    }

    override fun insertTicket(ticket: TicketInformation) {
        mCinemaDatabase?.getDao()?.insertTicket(ticket)
    }

    override fun getAllTickets(): List<TicketInformation>? =
        mCinemaDatabase?.getDao()?.getAllTickets()

    override fun deleteTicket(ticketId: Int) {
        mCinemaDatabase?.getDao()?.deleteTicket(ticketId)
    }

    override fun buyingTicket(
        movieName: String,
        bookingDate: String,
        timeSlotId: String,
        seatName: String, type: String) {
        mFirebaseApi.buyingTicket(movieName,bookingDate,timeSlotId,seatName,type)
    }

    override fun getBookingData(
        bookingCode: String,
        onSuccess: (bookingData: BookingVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        mFirebaseApi.getBookingData(bookingCode,onSuccess,onFailure)
    }

    override fun addBookingData(
        movieName: String,
        tickets: String,
        cinema: String,
        bookingDate: String,
        bookingCode: String,
        isBought: Boolean
    ) {
        mFirebaseApi.addBookingData(movieName,tickets,cinema,bookingDate,bookingCode,isBought)
    }

}