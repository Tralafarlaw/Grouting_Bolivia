package com.amuyu.groutingbolivia.liveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.amuyu.groutingbolivia.model.Credito
import com.amuyu.groutingbolivia.model.Perfil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

private const val TAG = "ProductosLiveData"
class ProfileLiveData(): LiveData<Perfil>(),
    EventListener<DocumentSnapshot> {

    private var mListenerReg: ListenerRegistration = ListenerRegistration {  }
    private val mReference = FirebaseFirestore.getInstance().collection("USUARIOS").document(FirebaseAuth.getInstance().uid!!)

    override fun onActive() {
        mListenerReg = mReference.addSnapshotListener(this)
        super.onActive()
    }

    override fun onInactive() {
        mListenerReg.remove()
        super.onInactive()
    }

    override fun onEvent(data: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if(data != null && data.exists()){
            this.value = data.toObject(Perfil::class.java)
        }else{
            Log.e(TAG, "Error: " + e?.localizedMessage, e)
            e?.printStackTrace()
        }
    }

}