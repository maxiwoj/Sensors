package sensor.app.my.sensors

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast


class MainActivity : Activity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var editText: EditText? = null
    private var lastUpdate: Long = 0
    private var rabbitClient: RabbitClient = RabbitClient()


    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        editText = findViewById(R.id.text)
        editText!!.setText(getString(R.string.DefaultID))

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            rabbitClient.publishMessage(getId() + "#LIGHT#" + event.values[0])
            Toast.makeText(applicationContext, getId() + "#LIGHT#" + event.values[0], Toast.LENGTH_SHORT).show()
        } else if (event.sensor.type == Sensor.TYPE_PRESSURE) {
            rabbitClient.publishMessage(getId() + "#PRESSURE#" + event.values[0])
            Toast.makeText(applicationContext, getId() + "#PRESSURE#" + event.values[0], Toast.LENGTH_SHORT).show()

        }

    }

    fun getId(): String? {
        if (editText!!.text.isNotEmpty()) {
            return editText!!.text.toString()
        } else {
            val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = manager.connectionInfo
            return info.ipAddress.toString()
        }
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