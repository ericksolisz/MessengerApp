package com.example.messengerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.security.interfaces.RSAPrivateCrtKey


class MessageAdapter (val context: Context,
                      val messageList: ArrayList<Message>,
                      val currentUserUid: String,
                      val sentMessages: List<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            //inflate recieve
            val view: View = LayoutInflater.from(context).inflate(R.layout.recieve, parent, false)
            return ReceiveViewHolder(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder is SentViewHolder) {
            // Mostrar mensajes enviados locales
            if (currentMessage.senderId == currentUserUid) {
                holder.sentMessage.text = currentMessage.message
            }
        } else if (holder is ReceiveViewHolder) {
            // Obtener la clave privada del usuario actual
            val privateKeyString = KeyStorage.getPrivateKey(context, currentUserUid)
            if (privateKeyString != null) {
                val privateKey = RSAUtils.getPrivateKeyFromString(privateKeyString)

                try {
                    // Desencriptar usando RSA estándar
                    val startStandard = System.nanoTime()
                    val decryptedMessageStandard =
                        RSAEncryptionUtil.decryptMessage(currentMessage.message!!, privateKey)
                    val endStandard = System.nanoTime()
                    val timeStandard = endStandard - startStandard

                    // Registrar el tiempo de descifrado estándar
                    println("RSA Standard decryption time: $timeStandard ns")

                    // Desencriptar usando RSA-CRT
                    if (privateKey is java.security.interfaces.RSAPrivateCrtKey) {
                        val startCRT = System.nanoTime()
                        val decryptedMessageCRT = RSADecryptCRTUtil.decryptMessageWithCRT(
                            currentMessage.message!!,
                            privateKey
                        )
                        val endCRT = System.nanoTime()
                        val timeCRT = endCRT - startCRT

                        // Registrar el tiempo de descifrado CRT
                        println("RSA-CRT decryption time: $timeCRT ns")
                    } else {
                        println("Private key is not RSAPrivateCrtKey, skipping CRT decryption.")
                    }

                    // Mostrar solo el mensaje desencriptado estándar en la app
                    holder.receiveMessage.text = decryptedMessageStandard

                } catch (e: Exception) {
                    // Si hay un error, mostrar el mensaje cifrado como respaldo
                    holder.receiveMessage.text = currentMessage.message
                    println("Error during decryption: ${e.message}")
                }
            } else {
                // Si no se encuentra la clave privada, mostrar el mensaje cifrado
                holder.receiveMessage.text = currentMessage.message
                println("Private key not found for user.")
            }
        }
    }












    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
    }

}