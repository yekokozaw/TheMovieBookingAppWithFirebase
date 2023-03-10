package com.flexath.themoviebookingapp.ui.fragments.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.movie.SeatVO
import com.flexath.themoviebookingapp.ui.adapters.movies.SeatsMoviesSeatAdapter
import com.flexath.themoviebookingapp.ui.delegates.SeatViewHolderDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies_seat.*

class MoviesSeatFragment : Fragment(), SeatViewHolderDelegate {

    private lateinit var mSeatsAdapter: SeatsMoviesSeatAdapter
    private val mCinemaModel: CinemaModel = CinemaModelImpl
    private val args: MoviesSeatFragmentArgs by navArgs()
    private var dayTimeSlotId: Int = 0
    private var bookingDate: String? = null

    private var mSeatDoubleList: MutableLiveData<MutableList<MutableList<SeatVO>>> = MutableLiveData<MutableList<MutableList<SeatVO>>>()

    companion object {
        private val seatVO = SeatVO(null,null,null,null,"path",false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_seat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottomNvgViewHome.visibility = View.INVISIBLE

        dayTimeSlotId = args.argDayTimeslotId
        bookingDate = args.argBookingDate

        setUpListeners()

        setUpSeatsRecyclerView()
        requestData()
    }

    private fun requestData() {
        bookingDate?.let {
            mCinemaModel.getSeatPlan(
                "Bearer ${mCinemaModel.getOtp(201)?.token}", dayTimeSlotId, it,
                onSuccess = { seatDoubleList ->
                    Toast.makeText(requireActivity(), "Seating Call succeeded - ${seatDoubleList.size}", Toast.LENGTH_SHORT).show()
                    mSeatDoubleList.value = seatDoubleList

                    val seatList = addCinemaPath(seatDoubleList)
                    mSeatsAdapter.bindNewData(seatList)
                },
                onFailure = {
                    Toast.makeText(requireActivity(), "Seating Call fails", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    private fun setUpSeatsRecyclerView() {
        mSeatsAdapter = SeatsMoviesSeatAdapter(this)
        rvSeatsMoviesSeat.adapter = mSeatsAdapter
        rvSeatsMoviesSeat.layoutManager =
            GridLayoutManager(requireContext(), 18, GridLayoutManager.VERTICAL, false)
    }

    private fun addCinemaPath(seatDoubleList: MutableList<MutableList<SeatVO>>) :MutableList<MutableList<SeatVO>>  {
        val newSeatDoubleList:MutableList<MutableList<SeatVO>> = mutableListOf()
        for (i in seatDoubleList.indices) {
            seatDoubleList[i].add(5, seatVO)
            seatDoubleList[i].add(6, seatVO)
            seatDoubleList[i].add(11, seatVO)
            seatDoubleList[i].add(12, seatVO)
            val newSeatSingleList:MutableList<SeatVO> = mutableListOf()
            for (ii in seatDoubleList[i].indices) {
                if(ii == 5 || ii == 6 || ii == 11 || ii == 12) {
                    newSeatSingleList.add(ii, seatVO)
                }else{
                    newSeatSingleList.add(ii,seatDoubleList[i][ii])
                }
            }
            newSeatDoubleList.add(newSeatSingleList)
        }
        return newSeatDoubleList
    }

    private fun setUpListeners() {
        btnBuyButtonMoviesSeat.setOnClickListener {
            val action = MoviesSeatFragmentDirections.actionMoviesSeatToMoviesFood()
            it.findNavController().navigate(action)
        }

//        seekbarSeat.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                val zoomLevel = (progress / 100f) * (zoomLayoutSeat.getMaxZoom() - zoomLayoutSeat.getMinZoom()) + zoomLayoutSeat.getMinZoom()
//                zoomLayoutSeat.zoomTo(zoomLevel, true)
//                zoomLayoutSeat.zoomBy(10F,true)
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                // No action needed
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                // No action needed
//            }
//        })
    }

    override fun onTapSeat(seatName: String) {
        mSeatDoubleList.observe(this) { seatDoubleList ->
            outer@ for (seatSingleList in seatDoubleList) {
                for (seatVO in seatSingleList) {
                    if (seatVO.seatName == seatName) {
                        seatVO.isSelected = true
                        Toast.makeText(requireActivity(), seatVO.seatName, Toast.LENGTH_SHORT).show()
                        break@outer
                    }
                }
            }
            mSeatsAdapter.bindNewData(seatDoubleList)
        }
    }
}