package com.flexath.themoviebookingapp.ui.viewholders.movies.seats

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.flexath.themoviebookingapp.R
import com.flexath.themoviebookingapp.data.vos.movie.SeatVO
import com.flexath.themoviebookingapp.ui.delegates.SeatViewHolderDelegate
import kotlinx.android.synthetic.main.view_holder_movies_seat_list.view.*

class MoviesSeatViewHolder(itemView: View,private val delegate: SeatViewHolderDelegate) : RecyclerView.ViewHolder(itemView) {

    private val numberOfColumn = 18
    private var mSeatVO:SeatVO? = null
    private var isAvailable:Boolean = true

    init {
        itemView.ivSeatCinemaSeat.setOnClickListener {
            if(mSeatVO?.isSelected == false) {
                delegate.onTapSeat(mSeatVO?.seat_name ?: "",mSeatVO?.isSelected)
            } else {
                delegate.onTapSeat(mSeatVO?.seat_name ?: "",mSeatVO?.isSelected)
            }
        }
    }

    fun bindSeatData(seat: SeatVO) {

//        val row = position / numberOfColumn
//        val column = position % numberOfColumn

        mSeatVO = seat

        if(mSeatVO?.isSelected == true) itemView.ivSeatCinemaSeat.setImageResource(R.drawable.seat_selectedd)
        else itemView.ivSeatCinemaSeat.setImageResource(R.drawable.chair_available)

        when(seat.type) {
            "text" -> {
                itemView.tvSeatTypeCinemaSeat.visibility = View.VISIBLE
                itemView.ivSeatCinemaSeat.visibility = View.INVISIBLE
                itemView.tvSeatTypeCinemaSeat.text = seat.symbol
            }
            "blank","path" -> {
                itemView.tvSeatTypeCinemaSeat.visibility = View.INVISIBLE
                itemView.ivSeatCinemaSeat.visibility = View.INVISIBLE
            }
            "taken" -> {
                    itemView.tvSeatTypeCinemaSeat.visibility = View.INVISIBLE
                    itemView.ivSeatCinemaSeat.visibility = View.VISIBLE
                    itemView.ivSeatCinemaSeat.setImageResource(R.drawable.chair_taken)
                }
            "available" -> {
                itemView.tvSeatTypeCinemaSeat.visibility = View.INVISIBLE
                itemView.ivSeatCinemaSeat.visibility = View.VISIBLE
            }
        }
    }
}