package com.amuyu.groutingbolivia.liveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.amuyu.groutingbolivia.model.Credito
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

private const val TAG = "CreditosLiveData"
class CreditosLiveData(ref: CollectionReference): LiveData<List<Credito>>(),
    EventListener<QuerySnapshot> {
    private val mProductoListTemp = arrayListOf<Credito>()

    private var mListenerReg: ListenerRegistration = ListenerRegistration {  }
    private val mReference = ref

    override fun onActive() {
        mListenerReg = mReference.whereEqualTo("asesor", FirebaseAuth.getInstance().uid ?:"").addSnapshotListener(this)
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
                mProductoListTemp.add(s.toObject(Credito::class.java)!!.apply { id = s.id })
            }
            this.value = mProductoListTemp.sortedByDescending { credito -> credito.fecha }
        }else{
            Log.e(TAG, "Error: " + e?.localizedMessage, e)
            e?.printStackTrace()
        }
    }

}