package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.PublicKey

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

            val name = intent.getStringExtra("name")
            val receiverUid =  intent.getStringExtra("uid")

            val senderUid = FirebaseAuth.getInstance().currentUser?.uid



            mDbRef = FirebaseDatabase.getInstance().getReference()

            senderRoom = receiverUid + senderUid
            receiverRoom =  senderUid + receiverUid

            supportActionBar?.title = name

            chatRecyclerView = findViewById(R.id.chatRecyclerView)
            messageBox =  findViewById(R.id.messageBox)
            sendButton = findViewById(R.id.sendButton)
            messageList = ArrayList()

            val sentMessages = MessageStorage.getMessages(this, senderUid)


        messageAdapter = MessageAdapter(this, messageList, FirebaseAuth.getInstance().currentUser?.uid!!, sentMessages)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
            chatRecyclerView.adapter = messageAdapter

            // logic for adding data to recyclerView
            mDbRef.child("chats").child(senderRoom!!).child("messages")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        messageList.clear()

                        for(postSnapshot in snapshot.children){

                            val message = postSnapshot.getValue(Message::class.java)
                            messageList.add(message!!)

                        }
                        messageAdapter.notifyDataSetChanged()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })


            //adding the message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()

            // Guardar mensaje enviado localmente
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            val sentMessages = MessageStorage.getMessages(this, uid).toMutableList()
            sentMessages.add(message)
            MessageStorage.saveMessages(this, uid, sentMessages)

            // Recuperar clave pública del receptor y cifrar el mensaje
            getPublicKeyOfReceiver(receiverUid!!) { publicKey ->
                val encryptedMessage = RSAEncryptionUtil.encryptMessage(message, publicKey)

                // Crear el objeto de mensaje cifrado
                val messageObject = Message(encryptedMessage, senderUid)

                // Guardar el mensaje cifrado en Firebase
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")
            }
        }


    }

    private fun getPublicKeyOfReceiver(receiverUid: String, onSuccess: (PublicKey) -> Unit) {
        mDbRef.child("user").child(receiverUid).child("publicKey")
            .get().addOnSuccessListener { snapshot ->
                val publicKeyString = snapshot.getValue(String::class.java)
                if (publicKeyString != null) {
                    val keyBytes = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT)
                    val keySpec = java.security.spec.X509EncodedKeySpec(keyBytes)
                    val publicKey = java.security.KeyFactory.getInstance("RSA").generatePublic(keySpec)
                    onSuccess(publicKey)
                }
            }
    }

}
