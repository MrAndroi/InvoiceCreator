package com.example.invoicecreator

import android.device.PrinterManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.invoicecreator.MainActivity.Companion.INVOICE_MODEL
import com.example.invoicecreator.databinding.ActivityInvoiceSummaryBinding
import com.example.invoicecreator.utils.ConvertInvoiceToBitmapUseCase
import com.example.invoicecreator.utils.InvoiceModel
import kotlinx.coroutines.runBlocking


class InvoiceSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvoiceSummaryBinding
    var mPrinterManager: PrinterManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invoiceModel = intent.getParcelableExtra<InvoiceModel>(INVOICE_MODEL)
        val bitmap = ConvertInvoiceToBitmapUseCase.invoke(this, invoiceModel!!)
        binding.imageViewInvoiceSummary.setImageBitmap(bitmap)

        binding.btnPrintInvoice.setOnClickListener {
            runBlocking {
                doPrint(printerManager = getPrinterManager(), bitmap = bitmap)
            }
        }

    }

    /**
     * Execution printing
     * To print data with this class, use the following steps:
     * Obtain an instance of Printer with PrinterManager printer = new PrinterManager().
     * Call setupPage(int, int) to initialize the page size.
     * If necessary, append a line in the current page with drawLine(int , int , int , int , int ).
     * If necessary, append text in the current page with drawTextEx(String , int, int , int , int , String ,int , int , int , int ).
     * If necessary, append barcode data in the current page with drawBarcode(String , int , int , int ,int , int , int ).
     * If necessary, append picture data in the current page with drawBitmap(Bitmap , int , int ).
     * To begin print the current page session, call printPage(int).
     *
     * @param printerManager printerManager
     * @param type           PRINT_TEXT PRINT_BITMAP PRINT_BARCOD PRINT_FORWARD
     * @param content        content
     */
    private fun doPrint(printerManager: PrinterManager?, bitmap: Bitmap?) {
        var ret = printerManager?.status //Get printer status
        if (ret == PrinterManager.PRNSTS_OK) {
            printerManager?.setupPage(384, -1) //Set paper size
            if (bitmap != null) {
                printerManager?.drawBitmap(bitmap, 30, 0) //print pictures
            } else {
                Toast.makeText(this, "Picture is null", Toast.LENGTH_SHORT).show()
            }
            ret = printerManager?.printPage(0) //Execution printing
            printerManager?.paperFeed(16) //paper feed
        }
        updatePrintStatus(ret!!)
    }

    //Update printer status, toast reminder in case of exception
    private fun updatePrintStatus(status: Int) {
        runOnUiThread {
            when (status) {
                PrinterManager.PRNSTS_OUT_OF_PAPER -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_paper",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrinterManager.PRNSTS_OVER_HEAT -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_temperature",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrinterManager.PRNSTS_UNDER_VOLTAGE -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_voltage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrinterManager.PRNSTS_BUSY -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_busy",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrinterManager.PRNSTS_ERR -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                PrinterManager.PRNSTS_ERR_DRIVER -> {
                    Toast.makeText(
                        this,
                        "R.string.tst_info_driver_error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    //Instantiate printerManager
    private fun getPrinterManager(): PrinterManager? {
        if (mPrinterManager == null) {
            mPrinterManager = PrinterManager()
            mPrinterManager!!.open()
        }
        return mPrinterManager
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPrinterManager != null) {
            mPrinterManager!!.close()
        }
    }

    companion object {
        //Printer gray value 0-4
        private const val DEF_PRINTER_HUE_VALUE = 0
        private const val MIN_PRINTER_HUE_VALUE = 0
        private const val MAX_PRINTER_HUE_VALUE = 4

        //Print speed value 0-9
        private const val DEF_PRINTER_SPEED_VALUE = 9
        private const val MIN_PRINTER_SPEED_VALUE = 0
        private const val MAX_PRINTER_SPEED_VALUE = 9

        // Printer status
        private const val PRNSTS_OK = 0 //OK
        private const val PRNSTS_OUT_OF_PAPER = -1 //Out of paper
        private const val PRNSTS_OVER_HEAT = -2 //Over heat
        private const val PRNSTS_UNDER_VOLTAGE = -3 //under voltage
        private const val PRNSTS_BUSY = -4 //Device is busy
        private const val PRNSTS_ERR = -256 //Common error
        private const val PRNSTS_ERR_DRIVER = -257 //Printer Driver error


        private const val PRINT_TEXT = 0 //Printed text
        private const val PRINT_BITMAP = 1 //print pictures
        private const val PRINT_BARCOD = 2 //Print bar code
        private const val PRINT_FORWARD = 3 //Forward (paper feed)

    }

}