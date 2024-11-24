package com.example.messengerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter (val context: Context, val messageList: ArrayList<Message>):
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

        if (holder.javaClass == SentViewHolder::class.java) {
            // Lógica para mensajes enviados
            val viewHolder = holder as SentViewHolder

            // Descifrar mensaje antes de mostrarlo
            val privateKeyString = KeyStorage.getPrivateKey(context) // Recuperar clave privada localmente
            if (privateKeyString != null) {
                val privateKey = RSAUtils.getPrivateKeyFromString(privateKeyString)
                try {
                    val decryptedMessage = RSAEncryptionUtil.decryptMessage(currentMessage.message!!, privateKey)
                    viewHolder.sentMessage.text = decryptedMessage
                } catch (e: Exception) {
                    // En caso de error al descifrar, muestra el mensaje cifrado
                    viewHolder.sentMessage.text = currentMessage.message
                }
            } else {
                // Si no se encuentra la clave privada, muestra el mensaje cifrado
                viewHolder.sentMessage.text = currentMessage.message
            }

        } else {
            // Lógica para mensajes recibidos
            val viewHolder = holder as ReceiveViewHolder

            // Descifrar mensaje antes de mostrarlo
            val privateKeyString = KeyStorage.getPrivateKey(context) // Recuperar clave privada localmente
            if (privateKeyString != null) {
                val privateKey = RSAUtils.getPrivateKeyFromString(privateKeyString)
                try {
                    val decryptedMessage = RSAEncryptionUtil.decryptMessage(currentMessage.message!!, privateKey)
                    viewHolder.receiveMessage.text = decryptedMessage
                } catch (e: Exception) {
                    // En caso de error al descifrar, muestra el mensaje cifrado
                    viewHolder.receiveMessage.text = currentMessage.message
                }
            } else {
                // Si no se encuentra la clave privada, muestra el mensaje cifrado
                viewHolder.receiveMessage.text = currentMessage.message
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