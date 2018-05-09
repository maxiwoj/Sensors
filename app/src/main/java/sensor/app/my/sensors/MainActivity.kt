package sensor.app.my.sensors

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast


class MainActivity : Activity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var view: View? = null
    private var lastUpdate: Long = 0
    private var rabbitClient : RabbitClient = RabbitClient()



    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        view = findViewById(R.id.textView)
        view!!.setBackgroundColor(Color.GREEN)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
//            rabbitClient.publishMessage(getLocalIpAddress() + "#LIGHT#" + event.values[0])
            Toast.makeText(applicationContext, getLocalIpAddress() + "#LIGHT#" + event.values[0], Toast.LENGTH_SHORT).show()
        } else if (event.sensor.type == Sensor.TYPE_PRESSURE) {
//            rabbitClient.publishMessage(getLocalIpAddress() + "#PRESSURE#" + event.values[0])
            Toast.makeText(applicationContext, getLocalIpAddress() + "#PRESSURE#" + event.values[0], Toast.LENGTH_SHORT).show()

        }

    }

    fun getLocalIpAddress(): String? {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.ipAddress.toString()
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        // unregister listener
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        rabbitClient.destroy()
    }
}