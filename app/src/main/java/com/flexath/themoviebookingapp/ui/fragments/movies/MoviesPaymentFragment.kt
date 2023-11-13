package com.flexath.themoviebookingapp.ui.fragments.movies

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.movie.cinema.TimeslotVO
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.CheckoutBody
import com.flexath.themoviebookingapp.data.vos.movie.confirmation.TicketCheckoutVO
import com.flexath.themoviebookingapp.ui.adapters.movies.MoviesPaymentAdapter
import com.flexath.themoviebookingapp.ui.delegates.PaymentViewHolderDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies_payment.*

class MoviesPaymentFragment : Fragment(),PaymentViewHolderDelegate {

    private var mTicketTotalPrice: Long? = 0L
    private var mSnackTotalPrice: Long = 0L
    private var mNumberOfTicket: Int? = null
    private var mTicketDate: String? = null
    private var mTicketTime: String? = null
    private var mMovieName: String? = null
    private lateinit var mPaymentAdapter:MoviesPaymentAdapter
    private val mCinemaModel:CinemaModel = CinemaModelImpl

    private val argst: MoviesTicketCheckoutFragmentArgs by navArgs()
    private val args: MoviesPaymentFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottomNvgViewHome.visibility = View.INVISIBLE

        mMovieName = args.argTicketCheckout?.movieName
        mNumberOfTicket = argst.argTicket?.seatInfo?.numberOfTicket
        mTicketTotalPrice = argst.argTicket?.seatInfo?.ticketTotalPrice
        mTicketDate = args.argTicketCheckout?.cinemaInfo?.date
        mTicketTime = argst.argTicket?.cinemaInfo?.time
        mSnackTotalPrice = argst.argTicket?.snackTotalPrice ?: 0
        Toast.makeText(requireActivity(),"$mTicketDate",Toast.LENGTH_LONG).show()
        Log.i("Ticketering",args.argTicketCheckout.toString())
        Log.i("TicketeringSnack",args.argTicketCheckout?.snackList.toString())
        Log.i("TicketeringSnackSize",args.argTicketCheckout?.snackList?.size.toString())

        setUpPaymentRecyclerVIew()
        requestData()
    }

    private fun requestData() {
        mCinemaModel.getPaymentTypes(
            "Bearer 18112|dp0oclqVIL57JvmormSokXYXsuEndQX0bhIVMswr",
            onSuccess = {
                mPaymentAdapter.setNewData(it)
            },
            onFailure = {
                Toast.makeText(requireActivity(),"Payment call fails",Toast.LENGTH_SHORT).show()
            }
        )
    }

     private fun getTicketList() : String {
        var seatNumberString = ""
        for (seatNumber in args.argTicketCheckout?.seatInfo?.ticketList ?: mutableListOf()) {
            val normalizedSeatName = normalizeSeatName(seatNumber)
            seatNumberString += "$normalizedSeatName,"
        }

        if(seatNumberString.isNotEmpty()){
            seatNumberString = StringBuilder(seatNumberString).also {
                it.deleteCharAt(it.lastIndex)
            }.toString()
        }
        return seatNumberString
    }

    private fun getTicketCheckoutBody(paymentId: Int): CheckoutBody {
        return CheckoutBody(
            args.argTicketCheckout?.movieId?.toInt(),
            args.argTicketCheckout?.cinemaInfo?.date,
            args.argTicketCheckout?.cinemaInfo?.cinemaTimeslotId,
            getTicketList(),
            args.argCheckoutBodySnackPayment?.snackList,
            paymentId
        )
    }

    private fun setUpPaymentRecyclerVIew() {
        mPaymentAdapter = MoviesPaymentAdapter(this)
        rvPaymentMovies.adapter = mPaymentAdapter
        rvPaymentMovies.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onTapPayment(paymentId: Int) {
        getTicketList()
        var timeslotVO = TimeslotVO(args.argTicketCheckout?.cinemaInfo?.cinemaTimeslotId,args.argTicketCheckout?.cinemaInfo?.time,args.argTicketCheckout?.cinemaInfo?.date,1)
        val totalMoney = "${((mTicketTotalPrice?.plus(mSnackTotalPrice) ?: 0) + 500)}"

        var ticketCheckout = TicketCheckoutVO(1,
            "",
            args.argTicketCheckout?.cinemaInfo?.date,
            "null",
            getTicketList(),
            args.argTicketCheckout?.seatInfo?.numberOfTicket,
            totalMoney,0,0,"null",timeslotVO,"dfa32432")

        val action1 = MoviesPaymentFragmentDirections.actionProfilePaymentToMoviesTicketConfirmation()

        action1.argCheckoutTicket = ticketCheckout
        action1.argTicketCheckout = args.argTicketCheckout

        action1.argAddress = args.argTicketCheckout?.cinemaInfo?.address ?: "Mandalay"
        findNavController().navigate(action1)
        val tickets : String = getTicketList()
        val seatList = args.argTicketCheckout?.seatInfo?.ticketList
        val timeSlotId : Int? = args.argTicketCheckout?.cinemaInfo?.cinemaTimeslotId

        if (seatList != null) {
            for (seat in seatList){
                mMovieName?.let { mTicketDate?.let { it1 ->
                    if (seat != null) {
                        mCinemaModel.buyingTicket(it,
                            it1,timeSlotId.toString(),seat,"taken")
                    }
                } }
            }
        }
    }

    fun normalizeSeatName(seatName: String?): String? {
        if (seatName.isNullOrEmpty()) {
            return null
        }

        val hyphenIndex = seatName.indexOf("-")
        if (hyphenIndex != -1) {
            // If hyphen is present, handle digits after the hyphen
            val seatPrefix = seatName.substring(0, hyphenIndex + 1)
            val seatNumber = seatName.substring(hyphenIndex + 1)
            val normalizedSeatNumber = seatNumber.replaceFirst("^0+".toRegex(), "")

            return seatPrefix + normalizedSeatNumber
        } else {
            // No hyphen, simply remove leading zeros
            return seatName.replaceFirst("^0+".toRegex(), "")
        }
    }

}