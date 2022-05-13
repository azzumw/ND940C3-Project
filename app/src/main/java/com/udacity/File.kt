package com.udacity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class File(val filename:String,val status:Boolean): Parcelable