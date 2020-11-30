package com.amuyu.groutingbolivia.liveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.amuyu.groutingbolivia.model.Cliente
import com.google.firebase.firestore.*

private const val TAG = "ClientesLiveData"

class ClientesLiveData(ref: CollectionReference): LiveData<List<Cliente>>(),
    EventListener<QuerySnapshot> {
    private val mProductoListTemp = arrayListOf<Cliente>()

    private var mListenerReg: ListenerRegistration = ListenerRegistration {  }
    private val mReference = ref

    override fun onActive() {
        mListenerReg = mReference.addSnapshotListener(this)
        super.onActive()
    }

    override fun onInactive() {
        mListenerReg.remove()
        super.onInactive()
    }

    override fun onEvent(data: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if(data != null && !data.isEmpty){
            mProductoListTemp.clear()
            for (s in data.documents){
                mProductoListTemp.add(s.toObject(Cliente::class.java)!!.apply { id = s.id })
            }
            this.value = mProductoListTemp
        }else{
            Log.e(TAG, "Error: " + e?.localizedMessage, e)
            e?.printStackTrace()
        }
    }

}