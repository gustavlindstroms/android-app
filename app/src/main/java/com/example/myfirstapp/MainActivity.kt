package com.example.myfirstapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout

var xVelocity = 0.0f;
var yVelocity = 0.0f;
var screenWidth = 0;
var screenHeight = 0;
var diameter = 125.0f;

private lateinit var mSensorManager: SensorManager
private var mGyroscope: Sensor? = null

class MainActivity : AppCompatActivity(), SensorEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var layout = findViewById<ConstraintLayout>(R.id.layout)
        layout.doOnLayout {
            screenWidth = it.measuredWidth
            screenHeight = it.measuredHeight
        }

        val imageView = findViewById<ImageView>(R.id.imageView);
        imageView.setImageBitmap(
                drawCircle(Color.TRANSPARENT, Color.GREEN, 125, 125)
        )
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    fun start(view: View) {
        vibratePhone();
        val button = findViewById<Button>(R.id.button);
        button.visibility = View.GONE;
        val mainHandler = Handler(Looper.getMainLooper())
        val imageView = findViewById<ImageView>(R.id.imageView);
        mainHandler.post(object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 17)
                move(imageView)
            }
        })
    }

    private fun move(imageView: ImageView) {
        var x = imageView.x;
        var y = imageView.y;
        when {
            x + xVelocity < 0 -> {
                x += x / -1
            }
            x + xVelocity + diameter > screenWidth -> {
                x = screenWidth - diameter
                xVelocity = 0.0f;
            }
            else -> {
                x += xVelocity
            }
        }

        when {
            y + yVelocity < 0 -> {
                y += y / -1
            }
            y + yVelocity + diameter > screenHeight -> {
                y = screenHeight - diameter
                yVelocity = 0.0f
            }
            else -> {
                y += yVelocity
            }
        }

        imageView.x = x;
        imageView.y = y;
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            xVelocity = (event.values[0] * 10) / -1;
            yVelocity = (event.values[1] * 10);
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    private fun drawCircle(bgColor: Int = Color.TRANSPARENT,
                           circleColor: Int = Color.WHITE, width: Int = 125, height: Int = 125): Bitmap {

        val bitmap: Bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        canvas.drawColor(bgColor)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = circleColor
        paint.isAntiAlias = true
        val radius = Math.min(canvas.width, canvas.height / 2)

        canvas.drawCircle(
                (canvas.width / 2).toFloat(),
                (canvas.height / 2).toFloat(),
                radius.toFloat(),
                paint
        )

        return bitmap
    }

    fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(50)
        }
    }
}