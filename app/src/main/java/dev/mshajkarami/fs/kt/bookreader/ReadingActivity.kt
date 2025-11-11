package dev.mshajkarami.fs.kt.bookreader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer
import java.io.File

class ReadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reading)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        copyPdfFromAssets()
        val file = File(filesDir, "book.pdf")
        val pdfRenderer = PdfRenderer(
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        )

        val adapter = object : PagerAdapter() {
            override fun getCount() = pdfRenderer.pageCount
            override fun isViewFromObject(view: View, obj: Any) = view == obj

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(container.context)
                val page = pdfRenderer.openPage(position)
                val bitmap = Bitmap.createBitmap(
                    page.width, page.height, Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
                container.addView(imageView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                page.close()
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }
        }

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter

        val transformer = BookFlipPageTransformer()
        transformer.setEnableScale(true)
        transformer.setScaleAmountPercent(10f)
        viewPager.setPageTransformer(true, transformer)
    }

    private fun copyPdfFromAssets() {
        val file = File(filesDir, "book.pdf")
        if (!file.exists()) {
            assets.open("book.pdf").use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }


}