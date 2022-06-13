package com.mqtt

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MainViewModel(application: Application): AndroidViewModel(application)
{
    private val context: Context = application.applicationContext
    // MQTT :
    private lateinit var mqttAndroidClient:MqttAndroidClient

    // LiveData:
    // MutableLiveData<ArrayList<Message>>, where Message is a Dataclass")
    private var messages:MutableLiveData<ArrayList<Message>> = MutableLiveData(ArrayList())

    // Variables for encryption
    private var cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    private lateinit var keySpec:SecretKeySpec
    private lateinit var ivSpec:IvParameterSpec


    // init a Client
    fun initClient(serverUri:String, clientID:String) {

        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientID)

    }

    // Method to connect the Client to the Broker
    fun connectClient(username:String, pwd:String, callback:(status:Boolean)->Unit) {

        mqttAndroidClient.setCallback(object:MqttCallbackExtended {

            override fun connectionLost(cause: Throwable?) {
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {

                //Decryption
                //val temp = message?.payload
                //cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
                //val decrypted = cipher.doFinal(temp)
                //message?.payload = decrypted

                addNewMessageToList(message.toString(),topic!!, getStringFromDate())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            }

        })

        val options = MqttConnectOptions()
        options.password = pwd.toCharArray()
        options.userName = username

        try {

            mqttAndroidClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    callback(true)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    exception?.printStackTrace()
                    callback(false)
                }

            })

        } catch (e:MqttException) {
            e.printStackTrace()
            callback(false)
        }

        //Inizializzazione della crittografia
        //initCryptography(pwd)

    }

    private fun initCryptography(pwd: String) {

        val random = SecureRandom()
        val salt = ByteArray(256)
        random.nextBytes(salt)

        val pbKeySpec = PBEKeySpec(pwd.toCharArray(), salt, 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        keySpec = SecretKeySpec(keyBytes, "AES")
        val ivRandom = SecureRandom()

        val iv = ByteArray(16)
        ivRandom.nextBytes(iv)
        ivSpec = IvParameterSpec(iv)

    }

    // Method to subscribe to a topic"
    fun subscribe(topic:String, qos:Int = 1, callback: (status: Boolean) -> Unit) {

        try {

            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    callback(true)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    exception?.printStackTrace()
                    callback(false)
                }

            })

        } catch (e:MqttException) {
            e.printStackTrace()
        }

    }

    // Method to publish a Message to a topic
    fun publish(topic:String, msg:String, qos:Int = 1, callback: (status: Boolean) -> Unit) {

        try {
            val message = MqttMessage()

            //No payload encryption
            message.payload = msg.toByteArray()

            //Payload encryption
            //cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            //message.payload = cipher.doFinal(msg.toByteArray())

            message.qos = qos
            message.isRetained = false
            mqttAndroidClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    callback(true)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    callback(false)
                }

            })
        } catch (e:MqttException) {
            e.printStackTrace()
        }

    }

    // Method to add a received Message to the MutableliveData<ArrayList<Message>>
    private fun addNewMessageToList(msg:String, topic:String, time:String) {

        val temp = Message(msg, topic, time)
        val tempData = messages.value!!
        tempData.add(0,temp)
        messages.value = tempData
    }

    // Getter Method for the MutableliveData<ArrayList<Message>>
    fun getLiveMessages():LiveData<ArrayList<Message>> = messages

}


