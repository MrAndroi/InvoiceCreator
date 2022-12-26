package com.example.invoicecreator

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.invoicecreator.databinding.ActivityMainBinding
import com.example.invoicecreator.utils.InvoiceModel

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNext.setOnClickListener {
            val subTotal = binding.editTextSubTotal.text.toString().toFloatOrNull()
            val total = binding.editTextTotal.text.toString().toFloatOrNull()
            if(subTotal == null || total == null) {
                Toast.makeText(this, "Please enter valid amounts", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, InvoiceSummaryActivity::class.java)
                val invoiceModel = InvoiceModel(
                    subTotal = subTotal,
                    deliveryFees = 2f
                )
                intent.putExtra(INVOICE_MODEL, invoiceModel)
                startActivity(intent)
            }

        }
    }


    companion object {
        const val INVOICE_MODEL = "invoiceModel"
    }
}