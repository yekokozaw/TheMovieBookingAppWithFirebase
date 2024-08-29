package com.flexath.themoviebookingapp.network.firebase

import com.flexath.themoviebookingapp.data.vos.movie.BannerVO
import com.flexath.themoviebookingapp.data.vos.movie.BannersVO
import com.flexath.themoviebookingapp.data.vos.movie.CinemaDetailsVO
import com.flexath.themoviebookingapp.data.vos.movie.MovieVO
import com.flexath.themoviebookingapp.data.vos.movie.PaymentVO
import com.flexath.themoviebookingapp.data.vos.movie.SeatVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackCategoryVO
import com.flexath.themoviebookingapp.data.vos.movie.SnackVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.CinemaVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.ConfigVO
import com.flexath.themoviebookingapp.data.vos.ticket.BookingVO
import com.google.firebase.firestore.model.mutation.MutationBatch

interface FirebaseApi {
    fun getSeatList(
        movieName: String,
        bookingDate: String,
        timeslotId: String,
        onSuccess: (groceries: MutableList<SeatVO>) -> Unit,
        onFailure: (String) -> Unit)
    fun buyingTicket(
        movieName: String,
        bookingDate: String,
        timeslotId: String,
        seat_name: String,
        type: String)

    fun cinemaDetails(
        cinemaId : Int,
        onSuccess: (banner: CinemaDetailsVO) -> Unit,
        onFailure: (String) -> Unit
    )

    fun getBanner(
        onSuccess: (banner: List<BannersVO>) -> Unit, onFailure: (String) -> Unit
    )

    fun getCinemaTimeSlots(
        movieName : String,
        date : String,
        onSuccess : (cinemas: List<CinemaVO>) -> Unit, onFailure: (String) -> Unit
    )

    fun getConfig(
        onSuccess : (config: List<ConfigVO>) -> Unit, onFailure: (String) -> Unit
    )

    fun getSnackCategory(
        onSuccess: (snackCategory: List<SnackCategoryVO>) -> Unit , onFailure: (String) -> Unit
    )

    fun getSnackByCategoryId(
        categoryId : String,
        onSuccess: (snacks: List<SnackVO>) -> Unit ,onFailure: (String) -> Unit
    )

    fun getPaymentTypes(
        onSuccess: (payment: List<PaymentVO>) -> Unit, onFailure: (String) -> Unit
    )

    fun getMovieList(
        onSuccess: (movies: List<MovieVO>) -> Unit , onFailure: (String) -> Unit
    )

    fun comingSoonList(
        onSuccess: (movies: List<MovieVO>) -> Unit , onFailure: (String) -> Unit
    )

    fun getBookingData(
        bookingCode : String,
        onSuccess: (bookingData: BookingVO) -> Unit,
        onFailure: (String) -> Unit
    )

    fun addBookingData(
        movieName: String,
        tickets : String,
        cinema : String,
        bookingDate: String,
        bookingCode: String,
        isBought : Boolean,

    )
}