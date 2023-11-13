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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object RealtimeDatabaseFirebaseApiImpl : FirebaseApi {

    private val database: DatabaseReference = Firebase.database.reference
    //private val database = FirebaseDatabase.getInstance().reference
    override fun getSeatList(
        movieName: String,
        bookingDate: String,
        timeSlotId : String,
        onSuccess: (groceries: MutableList<SeatVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child(movieName).child(bookingDate).child(timeSlotId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val seatList = arrayListOf<SeatVO>()
                snapshot.children.forEach { dataSnapShot ->
                    dataSnapShot.getValue(SeatVO::class.java)?.let {
                        seatList.add(it)
                    }
                }
                onSuccess(seatList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }

        })
    }

    override fun buyingTicket(
        movieName: String,
        bookingDate: String,
        timeSlotId: String,
        seat_name: String,
        type: String) {
        database.child(movieName).child(bookingDate).child(timeSlotId).child(seat_name).setValue(SeatVO(0,type,seat_name," ",4500,false))
    }

    override fun getBanner(
        onSuccess: (banner: List<BannersVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("banner").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bannerList = arrayListOf<BannersVO>()
                snapshot.children.forEach{ dataSnapShot ->
                    dataSnapShot.getValue(BannersVO::class.java)?.let {
                        bannerList.add(it)
                    }
                }
                onSuccess(bannerList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun getCinemaTimeSlots(
        movieName : String,
        date: String,
        onSuccess: (cinemas: List<CinemaVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("cinemaList").child(movieName).child(date).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val cinemaList = arrayListOf<CinemaVO>()
                snapshot.children.forEach { dataSnapshot ->
                    dataSnapshot.getValue(CinemaVO::class.java)?.let {
                        cinemaList.add(it)
                    }
                }
                onSuccess(cinemaList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun getConfig(
        onSuccess: (config: List<ConfigVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("config").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val configList = arrayListOf<ConfigVO>()
                snapshot.children.forEach { dataSnapShot->
                    dataSnapShot.getValue(ConfigVO::class.java)?.let {
                        configList.add(it)
                    }
                }
                onSuccess(configList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun getSnackCategory(
        onSuccess: (snackCategory: List<SnackCategoryVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("snackCategory").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList = arrayListOf<SnackCategoryVO>()
                snapshot.children.forEach { dataSnapshot ->
                    dataSnapshot.getValue(SnackCategoryVO::class.java)?.let {
                        categoryList.add(it)
                    }
                }
                onSuccess(categoryList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        }

        )
    }

    override fun getSnackByCategoryId(
        categoryId: String,
        onSuccess: (snacks: List<SnackVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("snacks").child(categoryId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val snackList = arrayListOf<SnackVO>()
                snapshot.children.forEach {dataSnapshot ->
                    dataSnapshot.getValue(SnackVO::class.java)?.let {
                        snackList.add(it)
                    }
                }
                onSuccess(snackList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun getPaymentTypes(
        onSuccess: (payment: List<PaymentVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("payment").addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val paymentList = arrayListOf<PaymentVO>()
                snapshot.children.forEach {  dataSnapshot ->
                    dataSnapshot.getValue(PaymentVO::class.java)?.let {
                        paymentList.add(it)
                    }
                }
                onSuccess(paymentList)
            }
            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun cinemaDetails(
        cinemaId: Int,
        onSuccess: (cinema: CinemaDetailsVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("cinemaDetails").child(cinemaId.toString()).addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(CinemaDetailsVO::class.java)?.let{
                    onSuccess(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }
    override fun getMovieList(
        onSuccess: (movies: List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("NowShowing").addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val movieList = arrayListOf<MovieVO>()
                snapshot.children.forEach {dataSnapshot ->
                    dataSnapshot.getValue(MovieVO::class.java)?.let {
                        movieList.add(it)
                    }
                }
                onSuccess(movieList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun comingSoonList(
        onSuccess: (movies: List<MovieVO>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("ComingSoon").addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val movieList = arrayListOf<MovieVO>()
                snapshot.children.forEach {dataSnapshot ->
                    dataSnapshot.getValue(MovieVO::class.java)?.let {
                        movieList.add(it)
                    }
                }
                onSuccess(movieList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }

        })
    }

    override fun getBookingData(
        bookingCode: String,
        onSuccess: (bookingData: BookingVO) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.child("bookingCodes").child(bookingCode).addValueEventListener( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(BookingVO::class.java)?.let{
                    onSuccess(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    override fun addBookingData(
        movieName: String,
        tickets: String,
        cinema: String,
        bookingDate: String,
        bookingCode: String,
        isBought: Boolean
    ) {
        database.child("bookingCodes").child(bookingCode).setValue(BookingVO(bookingCode.toInt(),isBought,movieName,tickets,cinema,bookingDate))
    }

}