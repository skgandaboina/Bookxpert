package com.bookxpert.ui.view

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.bookxpert.R
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById(R.id.pdfView)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val url = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"
        val inputStream = downloadPdfStream(url)

        inputStream?.let {
            pdfView.fromStream(it)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load()
        }
    }

    private fun downloadPdfStream(pdfUrl: String): InputStream? {
        return try {
            val url = URL(pdfUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            BufferedInputStream(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
