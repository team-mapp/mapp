package ac.smu.embedded.mapp.splash

import ac.smu.embedded.mapp.Intro.Intro_MainActivity
import ac.smu.embedded.mapp.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class Splash_MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({

        var intent = Intent(this,Intro_MainActivity::class.java)

            startActivity(intent)

            finish()
        },2000)
    }
}
