package com.example.invoicecreator.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceModel(
    val id: Int = (0..1000).random(),
    val subTotal: Float,
    val deliveryFees: Float,
    val total: Float = subTotal + deliveryFees,
): Parcelable
