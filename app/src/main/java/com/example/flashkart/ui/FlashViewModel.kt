package com.example.flashkart.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashkart.Data.InternetItem
import com.example.flashkart.network.FlashApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashViewModel:ViewModel() {
    private val _uiState = MutableStateFlow(FlashUiState())
    val uiState: StateFlow<FlashUiState> = _uiState.asStateFlow()

    private val _isVisible = MutableStateFlow(true)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    private val _user=MutableStateFlow<FirebaseUser?>(null)
    val user: MutableStateFlow<FirebaseUser?> get() = _user

    private val _phoneNumber=MutableStateFlow("")
    val phoneNumber: MutableStateFlow<String> get() = _phoneNumber

    private val _cartItems= MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> get() = _cartItems.asStateFlow()

    private val _otp=MutableStateFlow("")
    val otp: MutableStateFlow<String> get() = _otp

    private val _verificationId=MutableStateFlow("")
    val verificationId: MutableStateFlow<String> get() = _verificationId

    private val _ticks=MutableStateFlow(60L)
    val ticks: MutableStateFlow<Long> get() = _ticks

    private val database = Firebase.database
    private val myRef = database.getReference("users/${auth.currentUser?.uid}/cart")

    private lateinit var timerJob: Job

    private val _logoutClicked=MutableStateFlow(false)
    val logoutClicked:MutableStateFlow<Boolean> get() =_logoutClicked

    private var screenJob: Job? = null
    private var internetJob: Job? = null

    sealed interface ItemUiState {
        data object Error : ItemUiState
        data class Success(val items: List<InternetItem>) : ItemUiState
        data object Loading : ItemUiState
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value= phoneNumber
    }

    fun setOTP(otp: String) {
        _otp.value= otp
    }

    fun setVerificationId(verificationId: String) {
        _verificationId.value= verificationId
    }

    fun setUser(user: FirebaseUser){
        _user.value= user
    }
    fun clearData(){
        _user.value= null
        _phoneNumber.value= ""
        _otp.value= ""
        _verificationId.value= ""
        resetTimer()
    }

    fun runTimer(){
        timerJob=viewModelScope.launch {
            while (_ticks.value>0)
            {
                delay(1000)
                _ticks.value-=1
            }
            }
    }

    fun resetTimer(){
        try{
            timerJob.cancel()
        }
        catch (_: Exception){

        }finally {
            _ticks.value=60L
        }
    }

    fun setLogoutStatus(logoutStatus:Boolean){
        _logoutClicked.value=logoutStatus
    }

    fun addToCart(item: InternetItem) {
        _cartItems.value += item
    }

    fun addToDataBase(item: InternetItem){
       myRef.push().setValue(item)
    }

    private fun fillCartItem(){
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _cartItems.value= emptyList()
                for(childSnapshot in dataSnapshot.children){
                    val item=childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        val newItem=it
                        addToCart(newItem)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun removeFromCart(oldItem: InternetItem) {
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(childSnapshot in dataSnapshot.children){
                    var itemRemoved=false
                    val item=childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        if(oldItem.itemName==it.itemName && oldItem.itemPrice==it.itemPrice){
                            childSnapshot.ref.removeValue()
                            itemRemoved=true
                        }
                    }
                    if(itemRemoved){
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun updateClickedText(updatedText: String) {
        _uiState.update {
            it.copy(
                clickStatus = updatedText
            )
        }
    }

    fun updateSelectedCategory(updatedCategory: Int) {
        _uiState.update {
            it.copy(
                selectedCategory = updatedCategory
            )
        }
    }

    private fun hideOfferScreen() {
        _isVisible.value = false
    }

    fun getFlashItems() {
        internetJob = viewModelScope.launch {
            try {
                val listResult = FlashApi.retrofitService.getItems()
                itemUiState = ItemUiState.Success(listResult)
            } catch (exception: Exception) {
                itemUiState = ItemUiState.Error
                toggleVisibility()
                screenJob?.cancel()
            }
        }
    }


    private fun toggleVisibility() {
        _isVisible.value = !_isVisible.value
    }

    init {
        screenJob = viewModelScope.launch(Dispatchers.Default) {
            delay(3000)
            hideOfferScreen()
        }
        getFlashItems()
        fillCartItem()
    }

    override fun onCleared() {
        screenJob?.cancel()
        internetJob?.cancel()
        super.onCleared()
    }
}