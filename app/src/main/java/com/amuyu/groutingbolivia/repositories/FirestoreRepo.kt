package com.amuyu.movil_inv.repositories

import android.util.Log
import com.amuyu.groutingbolivia.liveData.*
import com.amuyu.groutingbolivia.model.*
import com.amuyu.movil_inv.liveData.InventariosLiveData
import com.amuyu.movil_inv.model.Const
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "FirestoreRepo"
class FirestoreRepo {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseRealtime: FirebaseDatabase = FirebaseDatabase.getInstance()
    fun getProducts(): ProductosLiveData {
        /*[doc] es una referncia a la coleccion de Productos*/
        val doc = firebaseFirestore.collection(Const.Productos)
        return ProductosLiveData(doc)
    }
    fun getInventarios(): InventariosLiveData {
        return InventariosLiveData(firebaseRealtime)
    }
    fun getClientes(): ClientesLiveData {
        val ref = firebaseFirestore.collection(Const.Clientes)
        return ClientesLiveData(ref)
    }

    fun getCreditos(): CreditosLiveData {
        val ref = firebaseFirestore.collection(Const.Creditos)
        return CreditosLiveData(ref)
    }

    fun getVentas(): VentasLiveData {
        val ref = firebaseFirestore.collection(Const.RegistrosT).whereEqualTo("asesor", FirebaseAuth.getInstance().uid!!)
        return VentasLiveData(ref)
    }

    fun getMyTransactions(): RegistrosLiveData {
        val mUid = FirebaseAuth.getInstance().uid!!
        val ref  = firebaseFirestore.collection(Const.RegistrosT).whereEqualTo("mUserId", mUid)
        return RegistrosLiveData(ref)
    }
    companion object {
        // aca van las funciones static que usan las Clases Util
        private val mReference = FirebaseFirestore.getInstance()
        private val mRealtimeRef = FirebaseDatabase.getInstance().reference

        fun registrarCredito (v: Credito){
            v.apply {
                if (v.saldo != 0.0){
                    v.historial.add(Pagos(cantidad = v.saldo))
                }
            }
            mReference.collection(Const.Creditos).add(v).addOnSuccessListener {
                Log.d(TAG, "Subido con exito")
            }.addOnFailureListener {e ->
                Log.e(TAG, "Error: " + e.localizedMessage, e)
                e.printStackTrace()
            }
        }

        fun descontaralAlmacen(Cantidad: String, InventarioId: String) {
            mReference.collection(Const.Inventario).document(InventarioId).update("mCantidad", FieldValue.increment(-(Cantidad.toLong()))).addOnFailureListener { e ->
                e.printStackTrace()
                Log.e(TAG, "Error: ${e.localizedMessage}", e)
            }.addOnSuccessListener {
                Log.d(TAG, "Descontago Con exito")
            }
        }
        fun uploadProduct (p: Producto){
            mReference.collection(Const.Productos).add(p).addOnSuccessListener {
                Log.d(TAG, "Producto REgistrado con Exito")
            }.addOnFailureListener {e ->
                Log.e(TAG, "Error: ${e.localizedMessage}", e)
                e.printStackTrace()
            }
        }
        fun registerCliente( c: Cliente){
            val prefix = when (c.zona){
                ZONAS.ORURO -> "700"
                ZONAS.CENTRO -> "300"
                ZONAS.EL_ALTO -> "900"
                ZONAS.OTROS -> "100"
                ZONAS.SUR -> "500"
            }
            mRealtimeRef.child("clientes").child(c.zona.toString()).runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val p = mutableData.getValue(Int::class.java)?:-1
                    mutableData.value = p+1
                    return Transaction.success(mutableData)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    // Transaction completed
                    if(databaseError == null) {
                        val nextid = (currentData?.value as Long?) ?: 0L
                        val id = prefix + "%04d".format(nextid.toInt())
                        mReference.collection(Const.Clientes).document(id).set(c)
                    }else {
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError!!)
                    }
                }
            })

          /*  val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nextid = (dataSnapshot.getValue() as Int?)?:0
                    val id = prefix+"%04d".format(nextid)
                    mReference.collection(Const.Clientes).document(id).set(c)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadId:onCancelled", databaseError.toException())
                }
            }
           // mRealtimeRef.child("clientes").child(c.zona.toString()).addListenerForSingleValueEvent(postListener)*/
        }
        fun updateCliente( c: Cliente, id: String){
            mReference.collection(Const.Clientes).document(id).update(c.toMap() as Map<String, Any>)
        }
        fun completeCredito(id: String, cant: Double){
            mReference.collection(Const.Creditos).document(id).update("saldo", cant)
            mReference.collection(Const.Creditos).document(id).update("historial", FieldValue.arrayUnion(Pagos(cantidad = cant)))
        }
    }

}