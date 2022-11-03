package weiss.kotlin.wordleexperiment

import androidx.appcompat.app.AppCompatActivity

//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import kotlinx.android.synthetic.main.activity_history.*
//import kotlinx.android.synthetic.main.activity_home_page.playButton
//import kotlinx.android.synthetic.main.activity_main.winsButton
//
//
////This page is no longer in use. I'm holding on to it for now in case we move to a system that tracks
////games in order, like traditional Wordle.
//
//
class HistoryActivity : AppCompatActivity() {
//    private lateinit var sharedPref: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_history)
//
//        playButton.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            // start your next activity
//            startActivity(intent)
//        }
//
//        winsButton.setOnClickListener {
//            val intent = Intent(this, HomePage::class.java)
//            // start your next activity
//            startActivity(intent)
//        }
//        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
//        var wins = sharedPref.getInt("wins", 0)
//        var losses = sharedPref.getInt("losses", 0)
//        var total = wins + losses
//        historyView.text = "Wins: $wins. Losses: $losses. Games played: $total."
//
//
//    }
//
//
//
}