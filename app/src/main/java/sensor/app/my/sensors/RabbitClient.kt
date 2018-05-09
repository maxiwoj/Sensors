package sensor.app.my.sensors

import android.util.Log
import com.rabbitmq.client.ConnectionFactory
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.LinkedBlockingDeque

/**
 * Created by maksymilian on 09/05/2018.
 */

class RabbitClient {

    private val queue = LinkedBlockingDeque<String>()
    private var publishThread : Thread? = null
    internal var factory = ConnectionFactory()

    fun publishMessage(message: String) {
        try {
            queue.putLast(message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    fun destroy() {
        publishThread!!.interrupt()
    }



    private fun setupConnectionFactory() {
        try {
            factory.isAutomaticRecoveryEnabled = false
            factory.host = ""
        } catch (e1: KeyManagementException) {
            e1.printStackTrace()
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        } catch (e1: URISyntaxException) {
            e1.printStackTrace()
        }

    }

    fun publishToAMQP() {
        publishThread = Thread(Runnable {
            while (true) {
                try {
                    val connection = factory.newConnection()
                    val ch = connection.createChannel()
                    ch.confirmSelect()

                    while (true) {
                        val message = queue.takeFirst()
                        try {
                            ch.basicPublish("amq.fanout", "chat", null, message.toByteArray())
                            Log.d("", "[s] $message")
                            ch.waitForConfirmsOrDie()
                        } catch (e: Exception) {
                            Log.d("", "[f] $message")
                            queue.putFirst(message)
                            throw e
                        }
                    }
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    Log.d("", "Connection broken: " + e.javaClass.name)
                    try {
                        Thread.sleep(5000) //sleep and then try again
                    } catch (e1: InterruptedException) {
                        break
                    }

                }

            }
        })
        publishThread!!.start()
    }

    init {
        setupConnectionFactory()
        publishToAMQP()
    }
}
