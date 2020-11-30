package com.amuyu.groutingbolivia.liveData

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amuyu.groutingbolivia.model.Venta
import com.google.firebase.firestore.*

private const val TAG = "RegistrosLiveData"
class RegistrosLiveData(ref: Query) : MutableLiveData<List<Venta>>(), EventListener<QuerySnapshot> {
    private val mRegistrosTmp = arrayListOf<Venta>()
    private var mListenerReg: ListenerRegistration = ListenerRegistration {  }
    private val mReference = ref



    override fun onActive() {
        //mListenerReg = mReference.addSnapshotListener(this)
        super.onActive()
    }
    fun call() {
        mListenerReg = mReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        mListenerReg.remove()
        super.onInactive()
    }

    override fun onEvent(data: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if(data != null && !data.isEmpty){
            mRegistrosTmp.clear()
            mRegistrosTmp.addAll(data.toObjects(Venta::class.java))
            this.value = mRegistrosTmp
            mListenerReg.remove()
        }else{
            Log.e(TAG, "Error: " + e?.localizedMessage, e)
            e?.printStackTrace()
        }
    }

}