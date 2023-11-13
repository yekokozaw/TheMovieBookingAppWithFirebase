package com.flexath.themoviebookingapp.ui.fragments.movies

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.ticket.TicketInformation
import com.flexath.themoviebookingapp.network.utils.BASE_URL
import com.flexath.themoviebookingapp.network.utils.IMG_BASE_URL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies_ticket_confirmation.*
import kotlinx.android.synthetic.main.layout_ticket_confirmation_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MoviesTicketConfirmationFragment : Fragment() {

    private val args:MoviesTicketConfirmationFragmentArgs by navArgs()
    private val mCinemaModel:CinemaModel = CinemaModelImpl
    private var mRandomCode = 0
    private var mMovieName : String?= null
    private var mCinemaName : String?= null
    private var mBookingDate : String? = null
    private var mBookingCode : String? = null
    private var mTickets : String? = null
    private val bookingTime = System.currentTimeMillis()
    private val numberList = listOf<Int>(21244555,6423212,1233111,4583852,
        3232992,12348473,8900214,8965456,24789076,32242921,424288281,4232122,
        9867043,13624321,2818373,1837322,9823222,1738261,)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies_ticket_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottomNvgViewHome.visibility = View.INVISIBLE
        mTickets = args.argCheckoutTicket?.seat
        mMovieName = args.argTicketCheckout?.movieName ?: ""
        mCinemaName = args.argTicketCheckout?.cinemaInfo?.cinemaName
        mBookingDate = args.argTicketCheckout?.cinemaInfo?.date
        val timeSlotId = args.argTicketCheckout?.cinemaInfo?.cinemaTimeslotId
        val randomIndex = (numberList.indices).random()
        val seatList = args.argTicketCheckout?.seatInfo?.ticketList
        mRandomCode = numberList[randomIndex]
        tvBookingCode.text = mRandomCode.toString()
        mBookingCode = mRandomCode.toString()
        setUpListeners()
        bindData()
        GlobalScope.launch {
            delay(60*1000)
            mCinemaModel.getBookingData(
                mRandomCode.toString(),
                onSuccess = { book ->
                    if (book.isBought == false){
                        if (seatList != null) {
                            for (seat in seatList){
                                mBookingDate?.let {
                                    mCinemaModel.buyingTicket(mMovieName!!,
                                        it,timeSlotId.toString(),seat,"available")
                                }
                            }
                        }
                        mCinemaModel.deleteTicket(mRandomCode ?: 0)
                    }
                },
                onFailure = {}
            )

        }
    }


    private fun bindData() {
        Log.i("TicketConfirm",args.argCheckoutTicket.toString())

        tvCinemaNameMoviesConfirmation.text = args.argCheckoutTicket?.bookingNo ?: ""
        tvNumberOfTicketConfirmation.text = args.argCheckoutTicket?.totalSeat.toString()
        tvTicketNamesConfirmation.text = args.argCheckoutTicket?.seat ?: ""
        tvDateConfirmation.text = args.argCheckoutTicket?.bookingDate ?: ""
        tvTimeConfirmation.text = args.argCheckoutTicket?.timeslot?.start_time ?: ""
        tvAddressConfirmation.text = args.argAddress ?: ""

        val movie = mCinemaModel.getMovieByIdForTicket(args.argCheckoutTicket?.movieId.toString())
        tvMovieTitleConfirmation.text = movie?.originalTitle ?: ""

        Glide.with(requireActivity())
            .load("$IMG_BASE_URL${movie?.posterPath}")
            .into(ivMoviePosterConfirmation)

        Glide.with(requireActivity())
            .load("$BASE_URL/${args.argCheckoutTicket?.qrCode}")
            .into(ivQRCodeMoviesTicketConfirmation)

//        val snackList = args.argCheckoutTicket?.snacks?.size
//        val snackLister = args.argCheckoutTicket?.snacks
//        Log.i("Xinner",snackList.toString())
//        Log.i("Xinner",snackLister.toString())
        Log.i("XinnerSize",args.argTicketCheckout?.snackList?.size.toString())

        mMovieName?.let { mTickets?.let { it1 ->
            mCinemaName?.let { it2 ->
                mBookingDate?.let { it3 ->
                    mBookingCode?.let { it4 ->
                        mCinemaModel.addBookingData(it,
                            it1, it2, it3, it4,false)
                    }
                }
            }
        } }
        mCinemaModel.insertTicket(TicketInformation(args.argCheckoutTicket,args.argTicketCheckout?.snackList,args.argAddress,bookingTime,mRandomCode,movie?.originalTitle,movie?.posterPath))
    }

    private fun setUpListeners() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            ivBookingSuccessMoviesTicketConfirmation?.visibility = View.INVISIBLE
            if(ivBookingSuccessMoviesTicketConfirmation != null) {
                Toast.makeText(requireActivity(),"Done!",Toast.LENGTH_SHORT).show()
            }
        },2000)

        btnDoneMoviesTicketConfirmation.setOnClickListener {
            val navController = Navigation.findNavController(it)
            navController.popBackStack(R.id.moviesHomeFragment, true)
            navController.navigate(R.id.moviesHomeFragment)
        }
    }

    fun revertSeatName(seatNames: String): String {
        val seats = seatNames.split(",").map { seatName ->
            val hyphenIndex = seatName.indexOf("-")
            if (hyphenIndex != -1) {
                val seatPrefix = seatName.substring(0, hyphenIndex + 1)
                val seatNumber = seatName.substring(hyphenIndex + 1)

                // Check if seatNumber is numeric and has only one digit, if so, add a leading zero
                val normalizedSeatNumber = if (seatNumber.toIntOrNull() in 1..9) {
                    "0$seatNumber"
                } else {
                    seatNumber
                }

                seatPrefix + normalizedSeatNumber
            } else {
                seatName.trim() // No hyphen, just trim any leading/trailing whitespaces
            }
        }
        return seats.joinToString(",")

    }

}