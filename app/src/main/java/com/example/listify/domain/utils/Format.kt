package com.example.listify.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun formatToDateOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatToTimeOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatToDateAndTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}