package com.amuyu.groutingbolivia.liveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.amuyu.groutingbolivia.model.Producto
import com.google.firebase.firestore.*

private const val TAG = "ProductosLiveData"

class ProductosLiveData(ref: CollectionReference): LiveData<List<Producto>>(), EventListener<QuerySnapshot> {
    private val mProductoListTemp = arrayListOf<Producto>()

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
                try{mProductoListTemp.add(s.toObject(Producto::class.java)!!.apply { mProductoID = s.id })}
                catch (e: Exception){
                    Log.e(TAG, s.data.toString(), e)
                }
            }
            this.value = mProductoListTemp.sortedBy { producto -> producto.nombre }
        }else{
            Log.e(TAG, "Error: " + e?.localizedMessage, e)
            e?.printStackTrace()
        }
    }
}