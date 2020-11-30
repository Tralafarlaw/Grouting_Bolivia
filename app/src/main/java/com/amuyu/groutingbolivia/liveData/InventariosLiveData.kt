package com.amuyu.movil_inv.liveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.amuyu.groutingbolivia.model.Inventario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*

private const val TAG = "InventariosLiveData"
class InventariosLiveData(ref: FirebaseDatabase): LiveData<List<Map<String, Int>>>(),
    ValueEventListener {
    private val mInventariosListTemp = arrayListOf<Map<String, Int>>()
    private val mReference = ref.reference

    override fun onActive() {
        mReference.addValueEventListener(this)
        super.onActive()
    }

    override fun onInactive() {
        mReference.removeEventListener(this)
        super.onInactive()
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        if(snapshot.exists()) {
            mInventariosListTemp.clear()
            mInventariosListTemp.addAll(listOf(
                (snapshot.child("al1").value as Map<String, Int>?)?:(mapOf()),
                (snapshot.child("al2").value as Map<String, Int>?)?:(mapOf()),
                (snapshot.child("al3").value as Map<String, Int>?)?:(mapOf())
            ))
            Log.d(TAG, mInventariosListTemp.toString())
            this.value = mInventariosListTemp

        }
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, error.message, error.toException())
    }

}