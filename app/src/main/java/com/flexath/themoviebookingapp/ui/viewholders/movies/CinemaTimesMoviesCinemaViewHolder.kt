package com.flexath.themoviebookingapp.ui.viewholders.movies

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.model.CinemaModel
import com.flexath.themoviebookingapp.data.model.CinemaModelImpl
import com.flexath.themoviebookingapp.data.vos.movie.cinema.TimeslotColorVO
import com.flexath.themoviebookingapp.data.vos.movie.cinema.TimeslotVO
import com.flexath.themoviebookingapp.ui.delegates.CinemaListViewHolderDelegate
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.view_holder_movies_cinema_cinema_times_list.view.*
import java.util.Calendar
import java.util.TimeZone

class CinemaTimesMoviesCinemaViewHolder(
    itemView: View,
    private val delegate: CinemaListViewHolderDelegate
) : RecyclerView.ViewHolder(itemView) {

    private var isSelectedShowTime = false
    private var mCinemaModel: CinemaModel = CinemaModelImpl
    private var mTimeslot: TimeslotVO? = null

    private var mTimeDifferentInMills : Long? = null
    init {
        //setUpOnClickListener()
        setUpOnLongClickListener()
    }

    private fun changeToListTimeslotColorVO(): ArrayList<TimeslotColorVO> {
        val oldConfigList = mCinemaModel.getCinemaConfig("cinema_timeslot_status")?.value as ArrayList<*>
        val newConfigList = arrayListOf<TimeslotColorVO>()
        for(oldConfig in oldConfigList) {
            val gson = Gson()
            val linkedTreeMap: LinkedTreeMap<*, *> = oldConfig as LinkedTreeMap<*, *>
            val timeslotColorVO: TimeslotColorVO = gson.fromJson(gson.toJsonTree(linkedTreeMap), TimeslotColorVO::class.java)
            newConfigList.add(timeslotColorVO)
        }
        return newConfigList
    }

    private fun cinemaAvailable(){
        var cinemaStartTime = mTimeslot?.start_time

        val timezone = TimeZone.getTimeZone("Asia/Yangon")
        val parts = cinemaStartTime?.split(":")
        val hours = parts?.get(0)?.toInt()
        val minutes = parts?.get(1)?.toInt()

        val currentTime = Calendar.getInstance()
        val cinemaStartTimeCalendar = Calendar.getInstance()
        if (hours != null) {
            cinemaStartTimeCalendar.set(Calendar.HOUR_OF_DAY, hours)
        }
        if (minutes != null) {
            cinemaStartTimeCalendar.set(Calendar.MINUTE, minutes)
        }
        cinemaStartTimeCalendar.set(Calendar.SECOND, 0)
        if (hours != null) {
            cinemaStartTimeCalendar.set(Calendar.HOUR_OF_DAY, hours)
        }
        if (minutes != null) {
            cinemaStartTimeCalendar.set(Calendar.MINUTE, minutes)
        }
        cinemaStartTimeCalendar.set(Calendar.SECOND, 0)

        val timeDifferenceInMillis = cinemaStartTimeCalendar.timeInMillis - currentTime.timeInMillis
        mTimeDifferentInMills = timeDifferenceInMillis
    }

    private fun getTimeslotColor(timeslot: TimeslotVO) : String {
        var color = ""
        val configList = changeToListTimeslotColorVO()
        for(config in configList) {
            if(config.id == timeslot.status) {
                color = config.color ?:""
            }
        }
        return color
//        itemView.itemCinemaTimes.setBackgroundColor(Color.parseColor(color))
    }

    private fun setUpOnClickListener() {
        itemView.setOnClickListener {
            if (isSelectedShowTime) {
                itemView.itemCinemaTimes.setBackgroundResource(R.drawable.movies_show_times_background)
                isSelectedShowTime = false
            } else {
                itemView.itemCinemaTimes.setBackgroundResource(R.drawable.item_cinema_times_orange_background)
                Toast.makeText(itemView.context, "Selected", Toast.LENGTH_SHORT).show()
                isSelectedShowTime = true
            }
        }
    }

    private fun setUpOnLongClickListener() {
        itemView.setOnLongClickListener {
            if (mTimeslot!!.start_date.equals("tomorrow")){
                delegate.onClickCinemaTimes(mTimeslot?.cinemaDayTimeslotId ?: 0, mTimeslot?.start_time)
                true
            }else{
                if(mTimeDifferentInMills!! <1*60*1000){
                    Toast.makeText(itemView.context,"Booking is not available.Try another one",Toast.LENGTH_SHORT).show()
                    false
                }else{
                    delegate.onClickCinemaTimes(mTimeslot?.cinemaDayTimeslotId ?: 0, mTimeslot?.start_time)
                    true
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun bindData(timeslot: TimeslotVO) {
        mTimeslot = timeslot
        if(mTimeslot!!.start_date.equals("tomorrow")){

        }else{
            cinemaAvailable()
        }

        Log.d("cinema", mTimeslot!!.start_time.toString())
        itemView.tvStartTimeMovieCinemaTimeslot.text = timeslot.start_time
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(itemView.resources.getColor(R.color.colorPrimary,null))
        shape.cornerRadius = itemView.resources.getDimension(R.dimen.margin_small)

        if (mTimeslot!!.start_date.equals("tomorrow")){
            shape.setStroke(1,Color.parseColor("#00FFA3"))
            itemView.itemCinemaTimes.background = shape
        }else{
            if(mTimeDifferentInMills!! < 1*60*1000){
                shape.setStroke(1,Color.parseColor("#ed092f"))
                itemView.itemCinemaTimes.background = shape
            }
            else{
                shape.setStroke(1,Color.parseColor("#00FFA3"))
                itemView.itemCinemaTimes.background = shape
            }
        }
    }
}