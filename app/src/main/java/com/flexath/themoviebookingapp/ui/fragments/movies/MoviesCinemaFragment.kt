package com.flexath.themoviebookingapp.ui.fragments.movies

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.movie.cinema.CinemaVO
import com.flexath.themoviebookingapp.ui.adapters.movies.DateCardMoviesCinemaAdapter
import com.flexath.themoviebookingapp.ui.delegates.CinemaListViewHolderDelegate
import com.flexath.themoviebookingapp.ui.utils.CinemaData
import com.flexath.themoviebookingapp.ui.utils.TimeSlotUtil
import com.flexath.themoviebookingapp.ui.viewpods.CinemaListViewPod
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies_cinema.*
import kotlinx.android.synthetic.main.layout_app_bar_movies_cinema.*

class MoviesCinemaFragment : Fragment(), CinemaListViewHolderDelegate {

    private lateinit var mDateCardsAdapter: DateCardMoviesCinemaAdapter
    private lateinit var timeSlotUtil: TimeSlotUtil

    private lateinit var cinemaListViewPod: CinemaListViewPod
    private val mCinemaModel: CinemaModel = CinemaModelImpl
    private var mBookingDate:String? = null
    private var mCinemaList:List<CinemaVO> = listOf()

    private val args:MoviesCinemaFragmentArgs by navArgs()

    // For Ticket
    private var mMovieName:String? = null
    private var mMovieId:String? = null
    private var mCinemaName:String? = null
    private var mCinemaLocation:String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        timeSlotUtil = TimeSlotUtil()
        timeSlotUtil.addTimeSlotToDateList()
        return inflater.inflate(R.layout.fragment_movies_cinema, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).bottomNvgViewHome.visibility = View.INVISIBLE

        mMovieName = args.argMovieName
        mMovieId = args.argMovieId

        setUpDateCardsRecyclerView()
        bindTimeSlotData()

        setUpViewPod()
        setUpListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        timeSlotUtil.clearDateList()
    }

    private fun setUpListeners() {

        tvCityNameExtraHomeCinema.text = args.argCityName

        btnBackMoviesCinema.setOnClickListener {
            findNavController().popBackStack()
        }

        btnSearchMoviesCinema.setOnClickListener {
            llLocationMoviesCinema.visibility = View.GONE
            btnSearchMoviesCinema.visibility = View.GONE
            searchViewMoviesCinema.visibility = View.VISIBLE

            searchViewMoviesCinema.setOnQueryTextListener(object : OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchCinema(newText)
                    return true
                }

            })
        }

        btnFilterMoviesCinema.setOnClickListener {
            llLocationMoviesCinema.visibility = View.VISIBLE
            btnSearchMoviesCinema.visibility = View.VISIBLE
            searchViewMoviesCinema.visibility = View.GONE
        }
    }

    private fun searchCinema(newText: String?) {
        val newCinemaList = mutableListOf<CinemaVO>()
        for(cinema in mCinemaList) {
            if(cinema.cinema?.contains(newText!!) == true) {
                newCinemaList.add(cinema)
            }
        }
        cinemaListViewPod.setNewData(newCinemaList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindTimeSlotData() {
        mDateCardsAdapter.bindTimeSlotData(timeSlotUtil.dateList)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpDateCardsRecyclerView() {
        mDateCardsAdapter = DateCardMoviesCinemaAdapter(timeSlotUtil.dateListTimeSLot, this)
        rvDatesCardsMoviesCinema.adapter = mDateCardsAdapter
        rvDatesCardsMoviesCinema.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpViewPod() {
        cinemaListViewPod = vpMoviesCinema as CinemaListViewPod
        cinemaListViewPod.setUpMovieListViewPod(this)
    }

    private fun requestData(date: String) {
        mMovieName?.let {
            mCinemaModel.getCinemaTimeSlots(
                it,
                "Bearer 18112|dp0oclqVIL57JvmormSokXYXsuEndQX0bhIVMswr",
                date,
                onSuccess = { cinemaList ->
                    mCinemaList = cinemaList
                    cinemaListViewPod.setNewData(cinemaList)
                    if (cinemaList.isEmpty()){
                        Toast.makeText(requireContext(), "ပြသ‌နေသောပွဲများ မရှိသေးပါ", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Network call error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onClickCinemaSeeDetails(cinemaId: Int) {

        val action = MoviesCinemaFragmentDirections.actionChooseCinemaToCinemaInfo()
        action.argCinemaId = cinemaId
        findNavController().navigate(action)
    }

    override fun onClickCinemaTimes(dayTimeslotId:Int,cinemaTime:String?) {
        val action = MoviesCinemaFragmentDirections.actionChooseCinemaToMoviesSeat()
        action.argDayTimeslotId = dayTimeslotId
        action.argBookingDate = mBookingDate

        action.argMovieName = mMovieName
        action.argMovieId = mMovieId
        val mCinemaInfo = CinemaData(mCinemaName,mBookingDate,cinemaTime,mCinemaLocation,dayTimeslotId)
        action.argCinemaInfo = mCinemaInfo
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClickTimeSlot(date: String) {
        mBookingDate = date
        requestData(date)
    }

    override fun getCinemaName(cinemaName: String?) {
        mCinemaName = cinemaName
    }

    override fun getCinemaId(cinemaId: Int?) {
        cinemaId?.let { id ->
            mCinemaModel.getCinemaInfo(
                id,
                onSuccess = {
                    mCinemaLocation = it.address
            }, onFailure = {

            })
        }
    }

    override fun onClickCinema(cinemaId: Int) {
        mCinemaList.forEach {
            it.isClicked = cinemaId == it.cinemaId
        }
        cinemaListViewPod.setNewData(mCinemaList)
    }
}