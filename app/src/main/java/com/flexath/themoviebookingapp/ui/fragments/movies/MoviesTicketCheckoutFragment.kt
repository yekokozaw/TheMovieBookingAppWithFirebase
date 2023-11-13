package com.flexath.themoviebookingapp.ui.fragments.movies

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.movie.SnackVO
import com.flexath.themoviebookingapp.ui.adapters.movies.SnackTicketCheckoutAdapter
import com.flexath.themoviebookingapp.ui.delegates.SnackTicketCheckoutViewHolderDelegate
import com.flexath.themoviebookingapp.ui.utils.Ticket
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies_ticket_checkout.*
import kotlinx.android.synthetic.main.fragment_movies_ticket_confirmation.tvBookingCode
import kotlinx.android.synthetic.main.layout_app_bar_movies_ticket_checkout.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_dialog_movies_checkout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Scope
import kotlin.concurrent.timer
import kotlin.coroutines.coroutineContext


class MoviesTicketCheckoutFragment : Fragment(), SnackTicketCheckoutViewHolderDelegate {

    private lateinit var viewModel: CountDownViewModel
    private lateinit var mOrderedFoodAdapter: SnackTicketCheckoutAdapter
    private var isVisibleRecyclerView: Boolean = true
    private val mCinemaModel: CinemaModel = CinemaModelImpl

    private var mMovieName: String? = null
    private var mCinemaName: String? = null
    private var mTicketDate: String? = null
    private var mTicketTime: String? = null
    private var mCinemaAddress: String? = null
    private var mNumberOfTicket: Int? = null
    private var mTicketTotalPrice: Long? = 0L
    private var mSnackTotalPrice: Long = 0L
    private var mTicketList: MutableList<String> = mutableListOf()
    private lateinit var mCheckoutSnackList: MutableList<SnackVO>

    private val args: MoviesTicketCheckoutFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_ticket_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottomNvgViewHome.visibility = View.INVISIBLE
        viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)
        mMovieName = args.argTicket?.movieName
        mCinemaName = args.argTicket?.cinemaInfo?.cinemaName
        mTicketDate = args.argTicket?.cinemaInfo?.date
        mTicketTime = args.argTicket?.cinemaInfo?.time
        mCinemaAddress = args.argTicket?.cinemaInfo?.address
        mNumberOfTicket = args.argTicket?.seatInfo?.numberOfTicket
        mTicketTotalPrice = args.argTicket?.seatInfo?.ticketTotalPrice
        mSnackTotalPrice = args.argTicket?.snackTotalPrice ?: 0
        mTicketList = args.argTicket?.seatInfo?.ticketList ?: mutableListOf()

        mCheckoutSnackList = mutableListOf()

        setUpOrderedFoodListRecyclerView()
        setUpListeners()
        hasItemInRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        viewModel.timer?.cancel()
    }
    private fun bindTicketData() {
        tvMovieNameTicket.text = mMovieName
        tvCinemaNameTicket.text = mCinemaName
        tvDateTicket.text = mTicketDate
        tvTimeTicket.text = mTicketTime
        tvAddressTicket.text = mCinemaAddress ?: "Mandalay"
        tvNumberOfTicket.text = mNumberOfTicket.toString()
        tvSnackTotalPrice.text = mSnackTotalPrice.toString()

        val ticketTotalPrice = "${mTicketTotalPrice}Ks"
        tvTicketTotalPrice.text = ticketTotalPrice

        tvTicketNamesTicket.text = getTicketList()

        val total = mTicketTotalPrice?.plus(mSnackTotalPrice) ?: 0
        val totalMoney = "${(total * 0.03 + total ?: 0)} Ks"
        tvTotalMoney.text = totalMoney
        val conveniencefee = total * 0.03
        tvConvenienceFee.text = "$conveniencefee KS"
        mOrderedFoodAdapter.bindNewData(setUpSnackList(), "Checkout")
    }

    private fun bindTicketCancellationData() {

        tvMovieNameTicket.text = args.argCheckoutTicketCancel?.movieName ?: ""
        tvCinemaNameTicket.text = args.argCheckoutTicketCancel?.ticketCheckout?.bookingNo ?: ""
        tvDateTicket.text = args.argCheckoutTicketCancel?.ticketCheckout?.bookingDate ?: ""
        tvTimeTicket.text = args.argCheckoutTicketCancel?.ticketCheckout?.timeslot?.start_time ?: ""
        tvAddressTicket.text = args.argCheckoutTicketCancel?.address ?: ""
        tvNumberOfTicket.text = args.argCheckoutTicketCancel?.ticketCheckout?.totalSeat.toString()

        tvTicketNamesTicket.text = args.argCheckoutTicketCancel?.ticketCheckout?.seat?.let { revertSeatName(it) }
        llTimer.visibility = View.VISIBLE
        val snackTotalPrice = mSnackTotalPrice.toString() + "Ks"
        tvSnackTotalPrice.text = snackTotalPrice

        val ticketTotalPrice =
            args.argCheckoutTicketCancel?.ticketCheckout?.totalSeat?.times(4500) ?: 0
        val ticketTotalPriceStr = "${ticketTotalPrice}Ks"
        tvTicketTotalPrice.text = ticketTotalPriceStr

        val totalMoney = ticketTotalPrice + getSnackTotalPriceForCancellation().toInt()
        val total = "${(totalMoney * 0.03 + totalMoney ?: 0)} Ks"
        val convienceFee = totalMoney * 0.03
        val tvAmountFee = convienceFee.toString() + "KS"
        tvConvenienceFee.text = tvAmountFee
        val totalMoneyStr = total
        tvTotalMoney.text = totalMoneyStr

        tvCancelBookingCode.text = "Booking Code : ${args.argCheckoutTicketCancel?.qrcode.toString()}"

        args.argCheckoutTicketCancel?.snackList?.let { mOrderedFoodAdapter.bindNewData(it, "Cancel") }

//            GlobalScope.launch {
//                delay(1*60*1000)
//                val movieName = args.argCheckoutTicketCancel?.movieName
//                val timeSlotId = args.argCheckoutTicketCancel?.ticketCheckout?.timeslot?.cinemaDayTimeslotId
//                val ticketDate = args.argCheckoutTicketCancel?.ticketCheckout?.bookingDate
//                val seat = args.argCheckoutTicketCancel?.ticketCheckout?.seat?.let { revertSeatName(it) }
//                val seatList = seat?.let { separateStringToList(it) }
//                if (seatList != null) {
//                    for (seat in seatList){
//                        movieName?.let { ticketDate?.let { it1 ->
//                            mCinemaModel.buyingTicket(it,
//                                it1,timeSlotId.toString(),seat,"available")
//                        } }
//                    }
//                }
//                mCinemaModel.deleteTicket(args.argCheckoutTicketCancel?.qrcode ?: 0)
//            }
    }

    private fun getSnackTotalPriceForCancellation(): String {
        var snackTotalPrice = 0
        args.argCheckoutTicketCancel?.snackList?.forEach {
            snackTotalPrice += it.price?.times(it.quantity) ?: 0
        }
        return snackTotalPrice.toString()
    }

    private fun setUpSnackList(): List<SnackVO> {
        for (snack in args.argTicket?.snackList!!) {
            if (snack.quantity > 0) {
                mCheckoutSnackList.add(snack)
            }
        }
        return mCheckoutSnackList
    }

    private fun getTicketList(): String {
        var ticket = ""
        if (mTicketList.isNotEmpty()) {
            mTicketList.forEach {
                ticket += "$it,"

            }
            ticket = StringBuilder(ticket).also {
                it.deleteCharAt(it.lastIndex)
            }.toString()
        }
        return ticket
    }

    private fun addTimer(){
        val currentTime = System.currentTimeMillis()
        val eventStartTime = args?.argCheckoutTicketCancel?.bookingTime?.plus((1*60*1000))
        val remainingTime = eventStartTime?.minus(currentTime)

        viewModel.timer = object : CountDownTimer(remainingTime!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = (millisUntilFinished / (1000 * 60 * 60)).toInt()
                val minutes = ((millisUntilFinished / (1000 * 60)) % 60).toInt()
                val seconds = ((millisUntilFinished / 1000) % 60).toInt()

                val remainingTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                tvTimer.text = "Remaining Time: $remainingTimeString"
            }

            override fun onFinish() {
                val movieName = args.argCheckoutTicketCancel?.movieName
                val timeSlotId = args.argCheckoutTicketCancel?.ticketCheckout?.timeslot?.cinemaDayTimeslotId
                val ticketDate = args.argCheckoutTicketCancel?.ticketCheckout?.bookingDate
                val seat = args.argCheckoutTicketCancel?.ticketCheckout?.seat?.let { revertSeatName(it) }
                val seatList = seat?.let { separateStringToList(it) }
                if (seatList != null) {
                    for (seat in seatList){
                        movieName?.let { ticketDate?.let { it1 ->
                            mCinemaModel.buyingTicket(it,
                                it1,timeSlotId.toString(),seat,"available")
                        } }
                    }
                }
                //mCinemaModel.deleteTicket(args.argCheckoutTicketCancel?.qrcode ?: 0)
                findNavController().popBackStack()
            }
        }
        viewModel.timer?.start()
    }
    @SuppressLint("InflateParams")
    private fun setUpListeners() {

        if (args.argCheckoutOrCancel == "Checkout") {

            bindTicketData()
            tvCancelBookingCode.visibility = View.GONE
            rlTicketCancellationMoviesCheckout.visibility = View.GONE
            btnContinueMoviesTicketCheckout.visibility = View.VISIBLE
            btnTicketCancellationMoviesTicketCheckout.setImageResource(R.drawable.ticket_cancellation_button_policy)

            btnContinueMoviesTicketCheckout.setOnClickListener {
                val action =
                    MoviesTicketCheckoutFragmentDirections.actionMoviesTicketCheckoutToProfilePayment()
                args.argTicket?.snackList = mCheckoutSnackList
                val ticket = Ticket(
                    args.argTicket?.movieId,
                    args.argTicket?.movieName,
                    args.argTicket?.cinemaInfo,
                    args.argTicket?.seatInfo,
                    args.argTicket?.snackTotalPrice,
                    mCheckoutSnackList
                )

                Log.i("Ticketer",ticket.toString())
                action.argTicketCheckout = ticket
                action.argCheckoutBodySnackPayment = args.argCheckoutBodySnackCheckout
                it.findNavController().navigate(action)
            }
            btnBackMoviesTicketCheckout.setOnClickListener {
                findNavController().popBackStack()
            }
        } else {
            bindTicketCancellationData()
            mCinemaModel.getBookingData(
                args.argCheckoutTicketCancel?.qrcode.toString(),
                onSuccess = {
                    if (it.isBought == true){
                        btnCancelButtonMoviesCancel.visibility = View.GONE
                        btnPayment.visibility = View.GONE
                        tvTimer.visibility = View.GONE
                        viewModel.timer?.cancel()
                    }
                    else{
                        addTimer()
                    }
                },
                onFailure = {}
            )
            rlTicketCancellationMoviesCheckout.visibility = View.VISIBLE
            btnContinueMoviesTicketCheckout.visibility = View.GONE
            val ticketDetailsTitle = "Ticket Details"
            tvTicketTitleMoviesTicketCheckout.text = ticketDetailsTitle
            btnBackMoviesTicketCheckout.setOnClickListener {
                findNavController().popBackStack()
            }

            btnCancelButtonMoviesCancel.setOnClickListener {
                ticketCancelDialog()
            }
        }

        //Payment
        btnPayment.setOnClickListener {
            btnCancelButtonMoviesCancel.visibility = View.GONE
            tvTimer.visibility = View.GONE
            btnPayment.visibility = View.GONE
            val movieName = args.argCheckoutTicketCancel?.movieName
            val cinemaName = args.argCheckoutTicketCancel?.ticketCheckout?.bookingNo ?: ""
            val ticketDate = args.argCheckoutTicketCancel?.ticketCheckout?.bookingDate
            val tickets = getTicketList()
            movieName?.let { it1 -> cinemaName?.let { it2 ->
                ticketDate?.let { it3 ->
                    mCinemaModel.addBookingData(it1,tickets,
                        it2, it3,args.argCheckoutTicketCancel?.qrcode.toString(),true)
                }
            } }
            Toast.makeText(requireActivity(),"Payment Success",Toast.LENGTH_LONG).show()
        }
    }

    private fun ticketCancelDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.RoundedAlertDialog)
            .setTitle("Ticket Cancellation !")
            .setMessage("Are you sure to cancel ?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->

                val movieName = args.argCheckoutTicketCancel?.movieName
                val timeSlotId = args.argCheckoutTicketCancel?.ticketCheckout?.timeslot?.cinemaDayTimeslotId
                val ticketDate = args.argCheckoutTicketCancel?.ticketCheckout?.bookingDate
                val seat = args.argCheckoutTicketCancel?.ticketCheckout?.seat?.let { revertSeatName(it) }
                Log.d("Testing", movieName+","+ticketDate+timeSlotId+","+seat)
                val seatList = seat?.let { separateStringToList(it) }
                if (seatList != null) {
                    for (seat in seatList){
                        movieName?.let { ticketDate?.let { it1 ->
                            mCinemaModel.buyingTicket(it,
                                it1,timeSlotId.toString(),seat,"available")
                        } }
                    }
                }
                Toast.makeText(requireContext(), "Ticket's cancelled", Toast.LENGTH_SHORT).show()
                mCinemaModel.deleteTicket(args.argCheckoutTicketCancel?.qrcode ?: 0)
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
            .create()
        dialog.show()
    }

    private fun hasItemInRecyclerView() {
        if (mOrderedFoodAdapter.itemCount == 0) {
            llFoodMoviesTicketCheckout.visibility = View.GONE
            Log.i("HninNuHas","Here")
        } else {
            llFoodMoviesTicketCheckout.visibility = View.VISIBLE
            btnFoodAndBeverageMoviesTicketCheckout.setOnClickListener {
                if (isVisibleRecyclerView) {
                    rvSnackTicketCheckout.visibility = View.GONE
                    btnFoodAndBeverageMoviesTicketCheckout.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_white_24dp)
                    isVisibleRecyclerView = false
                } else {
                    rvSnackTicketCheckout.visibility = View.VISIBLE
                    btnFoodAndBeverageMoviesTicketCheckout.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_white_24dp)
                    isVisibleRecyclerView = true
                }
            }
        }
    }

    private fun setUpOrderedFoodListRecyclerView() {
        mOrderedFoodAdapter = SnackTicketCheckoutAdapter(this)
        rvSnackTicketCheckout.adapter = mOrderedFoodAdapter
        rvSnackTicketCheckout.layoutManager = LinearLayoutManager(requireActivity())
    }

    //remove
    @SuppressLint("SetTextI18n")
    override fun onTapSnack(snackId: Int) {
        for (snack in mCheckoutSnackList) {
            if (snack.id == snackId) {
                mCheckoutSnackList.remove(snack)
                mSnackTotalPrice -= (snack.price?.times(snack.quantity)!!)
                tvSnackTotalPrice.text = mSnackTotalPrice.toString()

                tvTotalMoney.text =
                    (((mTicketTotalPrice?.plus(mSnackTotalPrice) ?: 0) + 500).toString())

                Toast.makeText(requireActivity(), "${snack.name} is removed", Toast.LENGTH_SHORT)
                    .show()
                break
            }
        }
        mOrderedFoodAdapter.bindNewData(mCheckoutSnackList, "Checkout")
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

    fun separateStringToList(input: String): List<String> {
        return input.split(",").map { it.trim() }
    }


}