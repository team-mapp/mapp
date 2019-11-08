package ac.smu.embedded.mapp.splash

import ac.smu.embedded.mapp.Intro.Intro_MainActivity
import ac.smu.embedded.mapp.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Splash_MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var intent = Intent(this,Intro_MainActivity::class.java)

            startActivity(intent)

            finish()

    }
}
