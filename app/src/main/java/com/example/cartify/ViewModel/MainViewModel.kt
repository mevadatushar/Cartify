package com.example.cartify.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cartify.Model.BrandModel
import com.example.cartify.Model.ItemsModel
import com.example.cartify.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(): ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _brand = MutableLiveData<MutableList<BrandModel>>()
    private val _popular = MutableLiveData<MutableList<ItemsModel>>()

      val banners: LiveData<List<SliderModel>> = _banner
      val brands: LiveData<MutableList<BrandModel>> = _brand
      val popular: LiveData<MutableList<ItemsModel>> = _popular


    fun loadBanners(){
        val Ref = firebaseDatabase.getReference("Banner")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf <SliderModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) {
                        lists.add(list)
                        // Log the URL of the loaded banner
                        Log.d("MainViewModel", "Loaded banner URL: ${list.url}")
                    }
                }
                _banner.value = lists
                // Log the number of banners loaded
                Log.d("MainViewModel", "Total banners loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
               // Handle the error and log it
                        Log.e("MainViewModel", "Failed to load banners: ${error.message}")
            }

        })
    }

    fun loadBrands(){
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(BrandModel::class.java)
                    if (list != null) {
                        // Log the URL of the loaded banner
                        Log.d("MainViewModel", "Loaded brand URL: ${list.picUrl}")
                        lists.add(list)
                    }
                }
                    _brand.value = lists
                // Log the number of banners loaded
                Log.d("MainViewModel", "Total brand loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error and log it
                Log.e("MainViewModel", "Failed to load brand: ${error.message}")
            }
        })

        }

    fun loadPopular(){
        val Ref = firebaseDatabase.getReference("Items")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        // Log the URL of the loaded banner
                        Log.d("MainViewModel", "Loaded brand URL: ${list.picUrl}")
                        lists.add(list)
                    }
                }
                _popular.value = lists
                // Log the number of banners loaded
                Log.d("MainViewModel", "Total brand loaded: ${lists.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error and log it
                Log.e("MainViewModel", "Failed to load brand: ${error.message}")
            }
        })

    }

}