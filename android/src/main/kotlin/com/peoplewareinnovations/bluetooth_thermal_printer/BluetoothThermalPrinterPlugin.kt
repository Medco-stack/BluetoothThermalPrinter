// package com.peoplewareinnovations.bluetooth_thermal_printer

// import android.app.Activity
// import android.bluetooth.BluetoothAdapter
// import android.bluetooth.BluetoothDevice
// import android.content.Context
// import android.content.ContextWrapper
// import android.content.Intent
// import android.content.IntentFilter
// import android.os.BatteryManager
// import android.os.Build
// import android.util.Log
// import android.widget.Toast
// import io.flutter.embedding.engine.plugins.FlutterPlugin
// import io.flutter.plugin.common.MethodCall
// import io.flutter.plugin.common.MethodChannel
// import io.flutter.plugin.common.MethodChannel.MethodCallHandler
// import io.flutter.plugin.common.MethodChannel.Result
// import io.flutter.plugin.common.PluginRegistry.Registrar
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.GlobalScope
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.withContext
// import java.io.OutputStream
// import java.util.*
// import androidx.annotation.NonNull


// private const val TAG = "====> mio: "
// private var outputStream: OutputStream? = null
// private lateinit var mac: String
// //val REQUEST_ENABLE_BT = 2

// class BluetoothThermalPrinterPlugin: FlutterPlugin, MethodCallHandler{
//   /// The MethodChannel that will the communication between Flutter and native Android
//   ///
//   /// This local reference serves to register the plugin with the Flutter Engine and unregister it
//   /// when the Flutter Engine is detached from the Activity
//   private lateinit var mContext: Context
//   private lateinit var channel : MethodChannel
//   private lateinit var state:String

//   override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
//     channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth_thermal_printer")
//     channel.setMethodCallHandler(this)
//     this.mContext = flutterPluginBinding.applicationContext
//   }

//   override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
//     if (call.method == "getPlatformVersion") {
//       result.success("Android ${android.os.Build.VERSION.RELEASE}")
//     }else if (call.method == "getBatteryLevel") {
//       val batteryLevel = getBatteryLevel()
//       if (batteryLevel != -1) {
//         result.success(batteryLevel)
//       } else {
//         result.error("UNAVAILABLE", "Battery level not available.", null)
//       }
//     }else if (call.method == "BluetoothStatus") {
//       var state:String = "false"
//       val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//       if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
//         state = "true"
//       }
//       result.success(state)
//     }else if (call.method == "connectionStatus") {

//       if(outputStream != null) {
//         try{
//           outputStream?.run {
//             write(" ".toByteArray())
//             result.success("true")
//             //Log.d(TAG, "step yes connection")
//           }
//         }catch (e: Exception){
//           result.success("false")
//           outputStream = null
//           ShowToast("Device was disconnected, reconnect")
//           //Log.d(TAG, "state print: ${e.message}")
//         }
//       }else{
//         result.success("false")
//         //Log.d(TAG, "no paso es false ")
//       }

//     } else if (call.method == "connectPrinter") {
//       var printerMAC = call.arguments.toString();
//       if(printerMAC.length>0){
//         mac = printerMAC;
//       }else{
//         result.success("false")
//       }
//       GlobalScope.launch(Dispatchers.Main) {
//         if(outputStream == null) {
//           outputStream = connect()?.also {
//             //result.success("true")
//             //Toast.makeText(this@MainActivity, "Connected to printer", Toast.LENGTH_SHORT).show()
//           }.apply {
//             result.success(state)
//             //Log.d(TAG, "finished: Connection state:$state")
//           }
//         }
//       }
//      }else if (call.method == "disconnectPrinter") {
//       GlobalScope.launch(Dispatchers.Main) {
//         if(outputStream != null) {
//           outputStream = disconnect()?.also {
//             //result.success("true")
//             //Toast.makeText(this@MainActivity, "Connected to printer", Toast.LENGTH_SHORT).show()
//           }.apply {
//             result.success("true")
//             //Log.d(TAG, "finished: Connection state:$state")
//           }
//         }
//       }
//      } else if (call.method == "writeBytes") {

//       var lista: List<Int> = call.arguments as List<Int>
//       var bytes: ByteArray = "\n".toByteArray()

//       lista.forEach {
//         bytes += it.toByte() //Log.d(TAG, "foreach: ${it}")
//       }
//       if(outputStream != null) {
//         try{
//           outputStream?.run {
//             write(bytes)
//             result.success("true")
//           }
//         }catch (e: Exception){
//           result.success("false")
//           outputStream = null
//           ShowToast("Device was disconnected, reconnect")
//           // Log.d(TAG, "state print: ${e.message}")
//           /*var ex:String = e.message.toString()
//           if(ex=="Broken pipe"){
//             Log.d(TAG, "Device was disconnected, reconnect")
//             ShowToast("Device was disconnected, reconnect")
//           }*/
//         }
//       }else{
//         result.success("false")
//       }

//     }else if (call.method == "printText") {

//       var stringArrived: String = call.arguments.toString()
//       //var list = stringArrived.split("*")
//       //println("list ${list.toString()}")

//       if(outputStream != null) {
//         try{
//           var size:Int = 0
//           var texto:String = ""
//           var line = stringArrived.split("//")
//           //Log.d(TAG, "list arrived: ${line.size}")
//           if(line.size>1) {
//             size = line[0].toInt()
//             texto = line[1]
//             if (size < 1 || size > 5) size = 2
//           }else{
//             size = 2
//             texto = stringArrived
//             //Log.d(TAG, "list came 2 text: ${texto} size: $size")
//           }

//           outputStream?.run {
//             write(setBytes.size[0])
//             write(setBytes.cancelar_chino)
//             write(setBytes.caracteres_escape)
//             write(setBytes.size[size])
//             write(texto.toByteArray(charset("iso-8859-1")))
//             result.success("true")
//           }
//         }catch (e: Exception){
//           result.success("false")
//           outputStream = null
//           ShowToast("Device was disconnected, reconnect")
//         }
//       }else{
//         result.success("false")
//       }

//     }else if (call.method == "bluetothLinked") {

//       var list:List<String> = getLinkedDevices()

//       result.success(list)

//     }else {
//       result.notImplemented()
//     }
//   }

//   private fun getBatteryLevel(): Int {
//     val batteryLevel: Int
//     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//       val batteryManager = mContext?.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
//       batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
//     } else {
//       val intent = ContextWrapper(mContext?.applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
//       batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//     }

//     return batteryLevel
//   }

//   private suspend fun connect(): OutputStream? {
//     state = "false"
//     return withContext(Dispatchers.IO) {
//       var outputStream: OutputStream? = null
//       val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//       if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
//         try {
//           val bluetoothAddress = mac//"66:02:BD:06:18:7B" // replace with your device's address
//           val bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress)
//           val bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
//                   UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//           )
//           bluetoothAdapter.cancelDiscovery()
//           bluetoothSocket?.connect()
//           if (bluetoothSocket!!.isConnected) {
//             outputStream = bluetoothSocket!!.outputStream
//             state = "true"
//             //outputStream.write("\n".toByteArray())
//           }else{
//             state = "false"
//             Log.d(TAG, "Disconnected: ")
//           }
//           //bluetoothSocket?.close()
//         } catch (e: Exception){
//           state = "false"
//           var code:Int = e.hashCode() //1535159 off //
//           Log.d(TAG, "connect: ${e.message} code $code")
//           outputStream?.close()
//         }
//       }else{
//         state = "false"
//         Log.d(TAG, "Adapter problem")
//       }
//       outputStream
//     }
//   }

//   private suspend fun disconnect(): OutputStream? {
//     state = "false"
//     return withContext(Dispatchers.IO) {
//       var outputStream: OutputStream? = outputStream
//       val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//       if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
//         try {
//           if(mac.length>0) {
//           val bluetoothAddress = mac
//           val bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress)
//           var bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
//                   UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//           )
//           bluetoothSocket.close()
//           //bluetoothSocket=null
//         }
//           Log.d(TAG, "Disconnected: ")
//           outputStream?.close()
//           outputStream=null
//         } catch (e: Exception){
//           state = "false"
//           var code:Int = e.hashCode() //1535159 off //
//           Log.d(TAG, "connect: ${e.message} code $code")
//           outputStream?.close()
//         }
//       }else{
//         state = "false"
//         outputStream=null
//         Log.d(TAG, "Adapter problem")
//       }
//       outputStream
//     }
//   }

//   private fun getLinkedDevices():List<String>{

//     val listItems: MutableList<String> = mutableListOf()

//     val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//     if (bluetoothAdapter == null) {
//       //lblmsj.setText("This application needs a phone with bluetooth")
//     }
//     //if blue tooth is not on
//     if (bluetoothAdapter?.isEnabled == false) {
//       //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//       //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//       //ShowToast("Bluetooth off")
//     }
//     //search bluetooth
//     //Log.d(TAG, "searching for devices: ")
//     val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//     pairedDevices?.forEach { device ->
//       val deviceName = device.name
//       val deviceHardwareAddress = device.address
//       listItems.add("$deviceName#$deviceHardwareAddress")
//       //Log.d(TAG, "device: ${device.name}")
//     }

//     return listItems;
//   }

//   private fun ShowToast(message: String){
//     Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
//   }

//   class setBytes(){
//     companion object {
//       //val info = "This is info"
//       //fun getMoreInfo():String { return "This is more fun" }

//       val enter = "\n".toByteArray()
//       val resetear_impresora = byteArrayOf(0x1b, 0x40, 0x0a)
//       val cancelar_chino = byteArrayOf(0x1C, 0x2E)
//       val caracteres_escape = byteArrayOf(0x1B, 0x74, 0x10)

//       val size = arrayOf(
//               byteArrayOf(0x1d, 0x21, 0x00), // La fuente no se agranda 0
//               byteArrayOf(0x1b, 0x4d, 0x01), // Fuente ASCII comprimida 1
//               byteArrayOf(0x1b, 0x4d, 0x00), //Fuente estándar ASCII    2
//               byteArrayOf(0x1d, 0x21, 0x11), // Altura doblada 3
//               byteArrayOf(0x1d, 0x21, 0x22), // Altura doblada 4
//               byteArrayOf(0x1d, 0x21, 0x33) // Altura doblada 5
//       )


//       //deprecated codes
//       const val HT: Byte = 9
//       const val LF: Byte = 10
//       const val CR: Byte = 13
//       const val ESC: Byte = 27
//       const val DLE: Byte = 16
//       const val GS: Byte = 29
//       const val FS: Byte = 28
//       const val STX: Byte = 2
//       const val US: Byte = 31
//       const val CAN: Byte = 24
//       const val CLR: Byte = 12
//       const val EOT: Byte = 4
//       val INIT = byteArrayOf(27, 64)
//       var FEED_LINE = byteArrayOf(10)
//       var SELECT_FONT_A = byteArrayOf(20, 33, 0)
//       var SET_BAR_CODE_HEIGHT = byteArrayOf(29, 104, 100)
//       var PRINT_BAR_CODE_1 = byteArrayOf(29, 107, 2)
//       var SEND_NULL_BYTE = byteArrayOf(0)
//       var SELECT_PRINT_SHEET = byteArrayOf(27, 99, 48, 2)
//       var FEED_PAPER_AND_CUT = byteArrayOf(29, 86, 66, 0)
//       var SELECT_CYRILLIC_CHARACTER_CODE_TABLE = byteArrayOf(27, 116, 17)
//       var SELECT_BIT_IMAGE_MODE = byteArrayOf(27, 42, 33, -128, 0)
//       var SET_LINE_SPACING_24 = byteArrayOf(27, 51, 24)
//       var SET_LINE_SPACING_30 = byteArrayOf(27, 51, 30)
//       var TRANSMIT_DLE_PRINTER_STATUS = byteArrayOf(16, 4, 1)
//       var TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = byteArrayOf(16, 4, 2)
//       var TRANSMIT_DLE_ERROR_STATUS = byteArrayOf(16, 4, 3)
//       var TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = byteArrayOf(16, 4, 4)
//       val ESC_FONT_COLOR_DEFAULT = byteArrayOf(27, 114, 0)
//       val FS_FONT_ALIGN = byteArrayOf(28, 33, 1, 27, 33, 1)
//       val ESC_ALIGN_LEFT = byteArrayOf(27, 97, 0)
//       val ESC_ALIGN_RIGHT = byteArrayOf(27, 97, 2)
//       val ESC_ALIGN_CENTER = byteArrayOf(27, 97, 1)
//       val ESC_CANCEL_BOLD = byteArrayOf(27, 69, 0)
//       val ESC_HORIZONTAL_CENTERS = byteArrayOf(27, 68, 20, 28, 0)
//       val ESC_CANCLE_HORIZONTAL_CENTERS = byteArrayOf(27, 68, 0)
//       val ESC_ENTER = byteArrayOf(27, 74, 64)
//       val PRINTE_TEST = byteArrayOf(29, 40, 65)
//     }
//   }

//   override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
//     channel.setMethodCallHandler(null)
//   }
// }

//Migration
package com.peoplewareinnovations.bluetooth_thermal_printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
// Removed: import io.flutter.plugin.common.PluginRegistry.Registrar // This import is for V1 embedding and causes the error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.*
import androidx.annotation.NonNull


private const val TAG = "====> mio: "
private var outputStream: OutputStream? = null
private lateinit var mac: String
//val REQUEST_ENABLE_BT = 2

class BluetoothThermalPrinterPlugin: FlutterPlugin, MethodCallHandler{
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var mContext: Context
    private lateinit var channel : MethodChannel
    private lateinit var state:String

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Initialize the MethodChannel with the binary messenger from the Flutter plugin binding
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth_thermal_printer")
        // Set this plugin as the method call handler for the channel
        channel.setMethodCallHandler(this)
        // Store the application context for later use (e.g., Toast messages, system services)
        this.mContext = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        // Handle different method calls from Flutter
        if (call.method == "getPlatformVersion") {
            // Return Android version
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "getBatteryLevel") {
            // Get and return the device's battery level
            val batteryLevel = getBatteryLevel()
            if (batteryLevel != -1) {
                result.success(batteryLevel)
            } else {
                result.error("UNAVAILABLE", "Battery level not available.", null)
            }
        } else if (call.method == "BluetoothStatus") {
            // Check and return Bluetooth status (enabled or disabled)
            var state:String = "false"
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                state = "true"
            }
            result.success(state)
        } else if (call.method == "connectionStatus") {
            // Check the current printer connection status by trying a small write
            if(outputStream != null) {
                try{
                    outputStream?.run {
                        write(" ".toByteArray()) // Attempt to write a single space to check connection
                        result.success("true")
                    }
                }catch (e: Exception){
                    // If write fails, connection is lost
                    result.success("false")
                    outputStream = null
                    ShowToast("Device was disconnected, reconnect")
                }
            }else{
                result.success("false")
            }
        } else if (call.method == "connectPrinter") {
            // Connect to a specified Bluetooth printer
            var printerMAC = call.arguments.toString()
            if(printerMAC.isNotEmpty()){
                mac = printerMAC
            } else {
                result.success("false")
            }
            GlobalScope.launch(Dispatchers.Main) {
                if(outputStream == null) {
                    // Connect in an IO thread and update outputStream
                    outputStream = connect()?.also {
                        // The result is sent in the apply block below
                    }.apply {
                        result.success(state) // 'state' is updated by the connect() function
                    }
                } else {
                    // If already connected, return success without reconnecting
                    result.success("true")
                }
            }
        } else if (call.method == "disconnectPrinter") {
            // Disconnect from the connected Bluetooth printer
            GlobalScope.launch(Dispatchers.Main) {
                if(outputStream != null) {
                    outputStream = disconnect()?.also {
                        // The result is sent in the apply block below
                    }.apply {
                        result.success("true") // Indicate successful disconnection
                    }
                } else {
                    result.success("true") // Already disconnected or no stream
                }
            }
        } else if (call.method == "writeBytes") {
            // Write a list of integer bytes to the printer
            var lista: List<Int> = call.arguments as List<Int>
            var bytes: ByteArray = "\n".toByteArray() // Start with a newline

            lista.forEach {
                bytes += it.toByte() // Append each integer as a byte
            }
            if(outputStream != null) {
                try{
                    outputStream?.run {
                        write(bytes) // Write the byte array to the output stream
                        result.success("true")
                    }
                }catch (e: Exception){
                    // Handle disconnection during write
                    result.success("false")
                    outputStream = null
                    ShowToast("Device was disconnected, reconnect")
                }
            }else{
                result.success("false")
            }
        } else if (call.method == "printText") {
            // Print text to the thermal printer with optional size adjustment
            var stringArrived: String = call.arguments.toString()
            if(outputStream != null) {
                try{
                    var size:Int = 0
                    var texto:String = ""
                    var line = stringArrived.split("//") // Split to get size and text

                    if(line.size > 1) {
                        size = line[0].toInt()
                        texto = line[1]
                        if (size < 1 || size > 5) size = 2 // Validate size, default to 2 if out of range
                    } else {
                        size = 2 // Default size
                        texto = stringArrived // Use entire string as text
                    }

                    outputStream?.run {
                        write(setBytes.size[0]) // Reset font size
                        write(setBytes.cancelar_chino) // Cancel Chinese mode
                        write(setBytes.caracteres_escape) // Set escape characters
                        write(setBytes.size[size]) // Set desired text size
                        write(texto.toByteArray(charset("iso-8859-1"))) // Write text with specific encoding
                        result.success("true")
                    }
                }catch (e: Exception){
                    // Handle disconnection during print
                    result.success("false")
                    outputStream = null
                    ShowToast("Device was disconnected, reconnect")
                }
            }else{
                result.success("false")
            }
        } else if (call.method == "bluetothLinked") {
            // Get a list of paired Bluetooth devices
            var list:List<String> = getLinkedDevices()
            result.success(list)
        } else {
            // Method not implemented
            result.notImplemented()
        }
    }

    /**
     * Retrieves the battery level of the device.
     */
    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For Lollipop and above, use BatteryManager
            val batteryManager = mContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            // For older versions, use IntentFilter
            val intent = ContextWrapper(mContext.applicationContext).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        }
        return batteryLevel
    }

    /**
     * Connects to a Bluetooth printer using the stored MAC address.
     * This operation is performed on an IO dispatcher to avoid blocking the main thread.
     */
    private suspend fun connect(): OutputStream? {
        state = "false" // Default connection state is false
        return withContext(Dispatchers.IO) {
            var currentOutputStream: OutputStream? = null
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                try {
                    val bluetoothAddress = mac // Use the stored MAC address
                    val bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress)
                    // Create an RFCOMM socket using the well-known SPP UUID
                    val bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                    )
                    bluetoothAdapter.cancelDiscovery() // Always cancel discovery before connecting
                    bluetoothSocket.connect() // Attempt to connect
                    if (bluetoothSocket.isConnected) {
                        currentOutputStream = bluetoothSocket.outputStream
                        state = "true" // Connection successful
                        Log.d(TAG, "Connected to printer.")
                    } else {
                        state = "false" // Connection failed
                        Log.d(TAG, "Disconnected or failed to connect.")
                    }
                } catch (e: Exception){
                    state = "false" // Error during connection
                    Log.d(TAG, "Connect error: ${e.message}")
                    currentOutputStream?.close() // Close stream on error
                }
            } else {
                state = "false" // Bluetooth adapter issues
                Log.d(TAG, "Bluetooth Adapter problem (null or disabled).")
            }
            currentOutputStream
        }
    }

    /**
     * Disconnects from the currently connected Bluetooth printer.
     * This operation is performed on an IO dispatcher to avoid blocking the main thread.
     */
    private suspend fun disconnect(): OutputStream? {
        state = "false" // Default state after disconnect is false
        return withContext(Dispatchers.IO) {
            val currentOutputStream = outputStream // Get the current output stream
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                try {
                    if(mac.isNotEmpty()) {
                        val bluetoothAddress = mac
                        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress)
                        val bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                        )
                        // Attempt to close the socket. Note: The outputStream is what we primarily use.
                        // Closing the socket might be handled implicitly by closing the stream.
                        // However, explicitly closing the socket can be good practice.
                        // bluetoothSocket.close() // Re-opening socket for close operation is not typical.
                        // It's usually `outputStream.close()` which also closes the underlying socket.
                    }
                    Log.d(TAG, "Disconnected: Stream closed.")
                    currentOutputStream?.close() // Close the output stream
                    outputStream = null // Clear the global output stream reference
                } catch (e: Exception){
                    state = "false" // Error during disconnection
                    Log.d(TAG, "Disconnect error: ${e.message}")
                    currentOutputStream?.close() // Attempt to close on error
                }
            } else {
                state = "false" // Bluetooth adapter issues
                outputStream = null // Ensure stream is null if adapter is problematic
                Log.d(TAG, "Bluetooth Adapter problem during disconnect.")
            }
            outputStream // Return the (now null) output stream
        }
    }

    /**
     * Retrieves a list of paired Bluetooth devices.
     */
    private fun getLinkedDevices():List<String>{
        val listItems: MutableList<String> = mutableListOf()
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            // Bluetooth not supported on this device
            ShowToast("This device does not support Bluetooth.")
            return listItems
        }

        if (bluetoothAdapter.isEnabled == false) {
            // Bluetooth is off
            ShowToast("Bluetooth is off. Please enable it.")
            // You might want to return an empty list or a specific error indicator here.
            return listItems
        }

        // Get paired devices
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
            listItems.add("$deviceName#$deviceHardwareAddress") // Format as "Name#MAC_Address"
            Log.d(TAG, "Found paired device: ${device.name} (${device.address})")
        }

        return listItems
    }

    /**
     * Displays a short Toast message on the Android device.
     */
    private fun ShowToast(message: String){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Companion object holding byte arrays for printer commands.
     * These are typically ESC/POS commands for thermal printers.
     */
    class setBytes(){
        companion object {
            val enter = "\n".toByteArray()
            val resetear_impresora = byteArrayOf(0x1b, 0x40, 0x0a)
            val cancelar_chino = byteArrayOf(0x1C, 0x2E)
            val caracteres_escape = byteArrayOf(0x1B, 0x74, 0x10)

            val size = arrayOf(
                byteArrayOf(0x1d, 0x21, 0x00), // Normal font size (0)
                byteArrayOf(0x1b, 0x4d, 0x01), // Compressed ASCII font (1)
                byteArrayOf(0x1b, 0x4d, 0x00), // Standard ASCII font (2)
                byteArrayOf(0x1d, 0x21, 0x11), // Double height (3)
                byteArrayOf(0x1d, 0x21, 0x22), // Quadruple height (4)
                byteArrayOf(0x1d, 0x21, 0x33)  // Sextuple height (5)
            )

            // Deprecated codes (as noted in original code) - often for specific printer models or older standards
            const val HT: Byte = 9
            const val LF: Byte = 10
            const val CR: Byte = 13
            const val ESC: Byte = 27
            const val DLE: Byte = 16
            const val GS: Byte = 29
            const val FS: Byte = 28
            const val STX: Byte = 2
            const val US: Byte = 31
            const val CAN: Byte = 24
            const val CLR: Byte = 12
            const val EOT: Byte = 4
            val INIT = byteArrayOf(27, 64)
            var FEED_LINE = byteArrayOf(10)
            var SELECT_FONT_A = byteArrayOf(20, 33, 0)
            var SET_BAR_CODE_HEIGHT = byteArrayOf(29, 104, 100)
            var PRINT_BAR_CODE_1 = byteArrayOf(29, 107, 2)
            var SEND_NULL_BYTE = byteArrayOf(0)
            var SELECT_PRINT_SHEET = byteArrayOf(27, 99, 48, 2)
            var FEED_PAPER_AND_CUT = byteArrayOf(29, 86, 66, 0)
            var SELECT_CYRILLIC_CHARACTER_CODE_TABLE = byteArrayOf(27, 116, 17)
            var SELECT_BIT_IMAGE_MODE = byteArrayOf(27, 42, 33, -128, 0)
            var SET_LINE_SPACING_24 = byteArrayOf(27, 51, 24)
            var SET_LINE_SPACING_30 = byteArrayOf(27, 51, 30)
            var TRANSMIT_DLE_PRINTER_STATUS = byteArrayOf(16, 4, 1)
            var TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = byteArrayOf(16, 4, 2)
            var TRANSMIT_DLE_ERROR_STATUS = byteArrayOf(16, 4, 3)
            var TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = byteArrayOf(16, 4, 4)
            val ESC_FONT_COLOR_DEFAULT = byteArrayOf(27, 114, 0)
            val FS_FONT_ALIGN = byteArrayOf(28, 33, 1, 27, 33, 1)
            val ESC_ALIGN_LEFT = byteArrayOf(27, 97, 0)
            val ESC_ALIGN_RIGHT = byteArrayOf(27, 97, 2)
            val ESC_ALIGN_CENTER = byteArrayOf(27, 97, 1)
            val ESC_CANCEL_BOLD = byteArrayOf(27, 69, 0)
            val ESC_HORIZONTAL_CENTERS = byteArrayOf(27, 68, 20, 28, 0)
            val ESC_CANCLE_HORIZONTAL_CENTERS = byteArrayOf(27, 68, 0)
            val ESC_ENTER = byteArrayOf(27, 74, 64)
            val PRINTE_TEST = byteArrayOf(29, 40, 65)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        // Remove this plugin as the method call handler when the engine is detached
        channel.setMethodCallHandler(null)
    }
}
