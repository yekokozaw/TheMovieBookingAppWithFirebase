package com.flexath.themoviebookingapp.network.utils

import com.flexath.themoviebookingapp.data.vos.movie.cinema.TimeslotVO

// API_KEY
const val YOUTUBE_API_KEY = "AIzaSyBuQOtSzg4Y01m610Qxd4ByrrbYnjTOvfI"
const val API_KEY = "ad1e17e27becb3abedf8eaccc61b8d5d"

const val BASE_URL = "https://api.themoviedb.org"
const val IMG_BASE_URL = "https://image.tmdb.org/t/p/w400"
const val PARAM_API_KEY = "api_key"
const val MOVIE_API_KEY =  "264acf938e91a16fdfee6f870edb4a4a"
const val PARAM_PAGE = "page"

// GET URLs
const val API_GET_CITIES = "/api/v2/cities"
const val API_GET_BANNERS = "/api/v2/banners"

const val API_GET_NOW_PLAYING = "/3/movie/now_playing"
const val API_GET_COMING_SOON = "/3/movie/upcoming"

const val API_GET_MOVIE_DETAILS = "/3/movie"
const val API_GET_CINEMA_TIMESLOTS = "/api/v2/cinema-day-timeslots"
const val API_GET_CINEMA_CONFIG = "/api/v2/configurations"
const val API_GET_CINEMA_INFO = "/api/v2/cinemas"
const val API_GET_SEAT_PLAN = "/api/v2/seat-plan"
const val API_GET_SNACK_CATEGORY = "/api/v2/snack-categories"
const val API_GET_SNACK = "/api/v2/snacks"
const val API_GET_PAYMENT = "/api/v2/payment-types"

// For Trailer Video
const val BASE_URL_TMDB = "https://api.themoviedb.org"
const val API_GET_VIDEO = "/3/movie"

// POST URLs
const val API_POST_OTP = "/api/v2/get-otp"
const val API_POST_SIGN_IN_WITH_OTP = "/api/v2/check-otp"
const val API_POST_CHECK_OUT = "/api/v2/checkout"
const val API_POST_LOG_OUT = "/api/v1/logout"

// PARAM QUERIES
const val PARAM_STATUS = "status"
const val PARAM_DATE = "date"
const val PARAM_LATEST_TIME = "latest_time"
const val PARAM_DAY_TIME_SLOT_ID = "cinema_day_timeslot_id"
const val PARAM_BOOKING_DATE = "booking_date"
const val PARAM_SNACK_CATEGORY = "category_id"

// HEADERS
const val HEADER_AUTH = "Authorization"