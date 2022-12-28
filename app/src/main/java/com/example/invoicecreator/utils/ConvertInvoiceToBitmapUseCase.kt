package com.example.invoicecreator.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import com.example.invoicecreator.databinding.LayoutInvoiceBinding

object ConvertInvoiceToBitmapUseCase {

    @RequiresApi(Build.VERSION_CODES.M)
    operator fun invoke(context: Context, invoiceModel: InvoiceModel): Bitmap {
        val inflater = context.getSystemService(LayoutInflater::class.java) as LayoutInflater
        val invoice = LayoutInvoiceBinding.inflate(inflater)
        invoice.textViewSubTotal.text = invoiceModel.subTotal.toString()
        invoice.textViewTotal.text = invoiceModel.total.toString()
        invoice.textViewDeliveryFees.text = invoiceModel.deliveryFees.toString()

        var displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            displayMetrics = Resources.getSystem().displayMetrics
        }
        invoice.root.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )

        invoice.root.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            invoice.root.measuredWidth,
            invoice.root.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        invoice.root.draw(canvas)

        return Bitmap.createScaledBitmap(bitmap, 385, 600, true)
    }
}