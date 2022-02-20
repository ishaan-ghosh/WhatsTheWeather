package com.example.testapp3

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
 */
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    var city = "tucson,us"//  "dhaka,bd"
    val API: String = "f5979ed4f3b30e4e3dcc9c934eaeaa9f"

    var units = "imperial"
    var deg = "°F"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()

        // Special feature - Polymorphism: converts a TextView to a Button
        val btn_click_me = findViewById<TextView>(R.id.goSearchBut) // as Button // (Button) //findViewById(R.id.changeDegBtn) as Button
        // set on-click listener
        btn_click_me.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
//                TODO("Not yet implemented")
                // your code to perform when the user clicks on the button
//                Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
                city = findViewById<TextView>(R.id.cityString).text.toString()
                weatherTask().execute()
            }
        })

        val changeDegBtn = findViewById<TextView>(R.id.changeDegBtn) // as Button // (Button) //findViewById(R.id.changeDegBtn) as Button
        // set on-click listener
        changeDegBtn.setOnClickListener (object:View.OnClickListener {
            override fun onClick(p0: View?) {
//                TODO("Not yet implemented")
                // your code to perform when the user clicks on the button
//                Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
//                city = findViewById<TextView>(R.id.cityString).text.toString()
                if (units == "metric") {
                    units = "imperial"
                    deg = "°F"
                } else if (units == "imperial") {
                    units = "metric"
                    deg = "°C"
                }

                weatherTask().execute()
            }
        })
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
//            val units = "imperial"
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&units="+units+"&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

//                val minutely = jsonObj.getJSONArray("minutely").getJSONObject(0)
//                val precip = minutely.getString("precipitation")
                val clouds = jsonObj.getJSONObject("clouds")


                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
//                val label = "°F"

                val all = clouds.getString("all")

                val temp = main.getString("temp")+deg
                val feels_like = main.getString("feels_like")+deg
                val tempMin = "Min Temp: " + main.getString("temp_min")+deg
                val tempMax = "Max Temp: " + main.getString("temp_max")+deg
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */

                // visibility?

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp // temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunrise*1000)
                )
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(sunset*1000)
                )
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<TextView>(R.id.clouds).text = all
                findViewById<TextView>(R.id.feels_like).text = feels_like

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}
