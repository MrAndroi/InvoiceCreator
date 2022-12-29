package com.example.invoicecreator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.device.ScanManager
import android.device.ScanManager.ACTION_DECODE
import android.device.scanner.configuration.PropertyID
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.invoicecreator.databinding.ActivityScanBarcodeBinding

class ScanBarcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBarcodeBinding

    private var scanManager: ScanManager? = null

    private val scanImage = MutableLiveData<Bitmap?>(null)
    private val scanString = MutableLiveData<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanManager = ScanManager()
        setUpObservers()

        binding.buttonStartScanner.setOnClickListener {
            scanManager?.openScanner()
        }
    }

    private fun setUpObservers() {
        scanImage.observe(this) {
            it?.let {
                binding.imageViewScanResult.setImageBitmap(it)
            }
        }
        scanString.observe(this) {
            it?.let {
                binding.textViewScanResult.text =
                    String.format(getString(R.string.bar_code_string, it))
            }
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            scanManager?.closeScanner()
            if (ACTION_CAPTURE_IMAGE == action) {
                val imageData = intent.getByteArrayExtra(DECODE_CAPTURE_IMAGE_KEY)
                if (imageData != null && imageData.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    scanImage.postValue(bitmap)
                } else {
                    //LogI("onReceive , ignore imagedata:$imagedata")
                }
            } else {
                // Get scan results, including string and byte data etc.
                //val barcode = intent.getByteArrayExtra(DECODE_DATA_TAG)
                //val barcodeLen = intent.getIntExtra(BARCODE_LENGTH_TAG, 0)
                //var scanResult = String(barcode!!, 0, barcodeLen)
                val barcodeStr = intent.getStringExtra(BARCODE_STRING_TAG)
                scanString.postValue(barcodeStr)

            }
        }
    }

    private fun registerReceiver(register: Boolean) {
        if (register && scanManager != null) {
            val filter = IntentFilter()
            val idBuf = intArrayOf(
                PropertyID.WEDGE_INTENT_ACTION_NAME,
                PropertyID.WEDGE_INTENT_DATA_STRING_TAG
            )
            val valueBuf: Array<String?> = scanManager!!.getParameterString(idBuf)
            if (valueBuf[0] != null && valueBuf[0] != "") {
                filter.addAction(valueBuf[0])
            } else {
                filter.addAction(ACTION_DECODE)
            }
            filter.addAction(ACTION_CAPTURE_IMAGE)
            registerReceiver(mReceiver, filter)
        } else if (scanManager != null) {
            scanManager!!.stopDecode()
            unregisterReceiver(mReceiver)
        }
    }

    override fun onPause() {
        super.onPause()
        registerReceiver(false)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(true)

    }

    companion object {
        private const val ACTION_CAPTURE_IMAGE = "scanner_capture_image_result"
        private const val BARCODE_STRING_TAG = ScanManager.BARCODE_STRING_TAG
        private const val DECODE_CAPTURE_IMAGE_KEY = "bitmapBytes"
    }

}