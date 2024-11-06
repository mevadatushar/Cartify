package com.example.cartify.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.cartify.Adapter.BrandAdapter
import com.example.cartify.Adapter.PopularAdapter
import com.example.cartify.Adapter.SliderAdapter
import com.example.cartify.Model.SliderModel
import com.example.cartify.R
import com.example.cartify.ViewModel.MainViewModel
import com.example.cartify.databinding.ActivityMainBinding

class MainActivity : BaseActivity ( ) {
    private val viewModel = MainViewModel()
    lateinit var binding: ActivityMainBinding
    private var lastBackPressTime: Long = 0
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {
        initBanner()

        initBrand()

        initPopular()

        updateUIWithUserData()



        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }



    private fun updateUIWithUserData() {
        // Retrieve user data from SharedPreferences
        sharedPreferences = getSharedPreferences("MYPREFS", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")



        binding.txtUsername.setText("Hi, $username")





    }

    // Handle back button press to close the navigation drawer if it's open
    override fun onBackPressed() {

            // Implement double back press to exit functionality
            if (System.currentTimeMillis() - lastBackPressTime < 2000) {
                super.onBackPressed()
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                lastBackPressTime = System.currentTimeMillis()
            }

    }
    private fun initBanner() {
        with(binding) {
            progressBarbBanner.visibility = View.VISIBLE
            viewModel.banners.observe(this@MainActivity, Observer { items ->
                    banners(items)
                progressBarbBanner.visibility = View.GONE
            })
            viewModel.loadBanners()
        }
    }

    private fun banners(images: List<SliderModel>) {
        with(binding) {
            viewpagerSlider.adapter = SliderAdapter(images, viewpagerSlider)
            viewpagerSlider.clipToPadding = false
            viewpagerSlider.clipChildren = false
            viewpagerSlider.offscreenPageLimit = 3
            viewpagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer().apply {
                addTransformer(MarginPageTransformer(40))
            }
            viewpagerSlider.setPageTransformer(compositePageTransformer)
            if (images.size > 1) {
                dotIndicator.visibility = View.VISIBLE
                dotIndicator.attachTo(viewpagerSlider)
            }

        }
    }

    private fun initBrand() {
        binding.progressbarBrad.visibility = View.VISIBLE
        viewModel.brands.observe ( this, Observer {
            binding.viewBrand. layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager. HORIZONTAL, false)
            binding.viewBrand. adapter = BrandAdapter (it)
            binding.progressbarBrad.visibility = View.GONE
        })
        viewModel.loadBrands()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.popular.observe ( this, Observer {
            binding.viewPopular. layoutManager =
                GridLayoutManager(this@MainActivity,2)
            binding.viewPopular. adapter = PopularAdapter (it)
            binding.progressBarPopular.visibility = View.GONE
        })
        viewModel.loadPopular()
    }


}