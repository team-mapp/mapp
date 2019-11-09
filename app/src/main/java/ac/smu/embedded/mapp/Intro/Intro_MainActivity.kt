package ac.smu.embedded.mapp.Intro

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.main.MainActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_intro__main.*

class Intro_MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro__main)

        textView4.setOnClickListener{
            var intent = Intent(this,MainActivity::class.java )

            startActivity(intent)
        }
    }



}
