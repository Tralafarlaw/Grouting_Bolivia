package com.amuyu.groutingbolivia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amuyu.groutingbolivia.liveData.ProfileLiveData
import com.amuyu.groutingbolivia.model.*
import com.amuyu.movil_inv.repositories.FirestoreRepo
import java.util.*
import kotlin.collections.HashMap

class MainViewModel: ViewModel() {
    private val repo = FirestoreRepo()
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _productos by lazy { repo.getProducts() }
    private val _inventarios by lazy { repo.getInventarios() }
    private val _creditos by lazy { repo.getCreditos() }
    private val _ventas by lazy { repo.getVentas() }
    private val _clientes by lazy { repo.getClientes() }

    private val _cartFotos = MutableLiveData<HashMap<String, String>>(hashMapOf())
    private val _cartNombres = MutableLiveData<HashMap<String, String>>(hashMapOf())
    private val _cartPrecios = MutableLiveData<HashMap<String, Double>>(hashMapOf())
    private val _cartSize = MutableLiveData<Int>(0)
    val _clientType = MutableLiveData<TipoVenta>(TipoVenta.FERRETERIA)

    /**
     * @param n tipo de Cliente
     * 1 -> Ferreteria
     * 2 -> Obras
     * 3 -> Oficina
     */
    fun setClienteType(n: Int){
        _clientType.value = when(n){
            1 -> TipoVenta.FERRETERIA
            2 -> TipoVenta.OBRA
            else -> TipoVenta.OFICINA
        }
    }
    fun getClienteType() = (_clientType.value ?: TipoVenta.OFICINA).i

    val _cartProducts = MutableLiveData<HashMap<String, Int>>(hashMapOf())
    val _cartDescuentos = MutableLiveData<HashMap<String, Double>>(hashMapOf())
    val _cartDesc = MutableLiveData<Double>(0.0)

    val _VentaSelected by lazy { MutableLiveData<Venta>(null) }

    fun addtoCart(pro: Producto){
        val p = pro.mProductoID
        _cartProducts.value!![p] = (_cartProducts.value!![p]?:0) + 1
        when (_clientType.value?: TipoVenta.OBRA){
            TipoVenta.FERRETERIA -> {
                _cartPrecios.value!![p] = pro.precioFerreteria
                _cartDescuentos.value!![p] = pro.descuentoFerreteria
            }
            TipoVenta.OFICINA -> {
                _cartPrecios.value!![p] = pro.precioOficina
                _cartDescuentos.value!![p] = pro.descuentoOficina
            }
            TipoVenta.OBRA -> {
                _cartPrecios.value!![p] = pro.precioObras
                _cartDescuentos.value!![p] = pro.descuentoObras
            }
        }
        _cartFotos.value!![p] = pro.photourl
        _cartNombres.value!![p] = pro.nombre
        _cartSize.value=_cartProducts.value!!.size
    }
    fun setDesc(desc: Double){
        _cartDesc.postValue(desc)
    }
    fun getDesc(): Double{
        return _cartDesc.value ?: 0.0
    }

    fun setinCart(p: String, cant: Int){
        _cartProducts.value!![p] = cant
        _cartProducts.postValue(_cartProducts.value)
    }
    fun setDiscount(p: String, cant: Double){
        _cartDescuentos.value!![p] = cant
        _cartDescuentos.postValue(_cartDescuentos.value)
    }
    fun pullFromCart(p:String){
        _cartDescuentos.value!!.remove(p)
        _cartProducts.value!!.remove(p)
        _cartPrecios.value!!.remove(p)
        _cartFotos.value!!.remove(p)
        _cartNombres.value!!.remove(p)
        _cartSize.value=_cartProducts.value!!.size
    }

    fun cleanCart(){
        _cartProducts.value = hashMapOf()
        _cartDescuentos.value = hashMapOf()
        _cartPrecios.value = hashMapOf()
        _cartFotos.value = hashMapOf()
        _cartNombres.value = hashMapOf()
        _cartSize.value=_cartProducts.value!!.size
        _cartDesc.value=0.0
    }
    fun getCartTotal(): Double{
        var total = 0.0
        _cartProducts.value!!.forEach { (key, cantidad) ->
            val precio = _cartPrecios.value!![key] ?: 0.0
            val descuento = _cartDescuentos.value!![key] ?: 0.0
            total += ((precio * cantidad) - descuento)
        }
        return total
    }

    fun getCart(): List<Map<String, Any>>{
        if (_cartProducts.value!!.size == 0) return listOf()
        val list = arrayListOf<Map<String, Any>>()
        for (data in _cartProducts.value?: hashMapOf()){
            list.add(
                mapOf(
                    "id" to data.key,
                    "cantidad" to data.value,
                    "nombre" to _cartNombres.value!![data.key]!!,
                    "descuento" to _cartDescuentos.value!![data.key]!!,
                    "url" to _cartFotos.value!![data.key]!!,
                    "precio" to _cartPrecios.value?.get(data.key)!!
                )
            )
        }
        return list
    }



    fun getCartItems(): List<ItemVenta>{
        if (_cartProducts.value!!.size == 0) return listOf()
        val list = arrayListOf<ItemVenta>()
        for (data in _cartProducts.value?: hashMapOf()){
            list.add(
                ItemVenta(data.key, cantidad = data.value, precio = _cartPrecios.value?.get(data.key)!!, descuento = _cartDescuentos.value!![data.key]?:0.0, nombre = _productos.value!!.filter { producto -> producto.mProductoID == data.key }[0].nombre)
            )
        }
        return list
    }

    fun getRegistroVenta(num: String, cl: String): Venta{
        val aux = arrayListOf<ItemVenta>()
        val descAux = _cartDescuentos.value?: hashMapOf()
        val precAux = _cartPrecios.value?: hashMapOf()
        for (dat in _cartProducts.value?: hashMapOf()){
            aux.add(ItemVenta(producto = dat.key,
                cantidad =  dat.value,
                precio = (precAux[dat.key]?:0.0),
                descuento = (descAux[dat.key]?:0.0),
                nombre = _productos.value!!.filter { p -> p.mProductoID == dat.key }[0].nombre),)
        }
        return Venta(numero = num, cliente = cl, items = aux)
    }
    val profile by lazy { ProfileLiveData() }
    val text: LiveData<String> = _text
    val cartSize: LiveData<Int> = _cartSize
    val productos: LiveData<List<Producto>> by lazy { _productos }
    val inventarios: LiveData<List<Map<String, Int>>> by lazy { _inventarios }
    val creditos: LiveData<List<Credito>> by lazy { _creditos }
    val ventas: LiveData<List<Venta>> by lazy { _ventas }
    val clientes: LiveData<List<Cliente>> by lazy { _clientes }
    enum class TipoVenta (val i: Int){
        FERRETERIA(1),
        OFICINA(2),
        OBRA(3)
    }
    //////////////////////////////////////////////////////////////////
    ////////////REPORTES//////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    val desde = MutableLiveData<Date>()
    val hasta = MutableLiveData<Date>()

}