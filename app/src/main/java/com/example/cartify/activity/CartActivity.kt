package com.example.cartify.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartify.Adapter.CartAdapter
import com.example.cartify.Helper.ChangeNumberItemsListener
import com.example.cartify.Helper.ManagmentCart
import com.example.cartify.R
import com.example.cartify.databinding.ActivityCartBinding

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        managmentCart = ManagmentCart(this)
        setVariable()
        initCartList()
        calculateCart()
    }

    private fun initCartList() {
        binding.viewcart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewcart.adapter = CartAdapter(managmentCart.getListCart(), this, object : ChangeNumberItemsListener {
            override fun onChanged() {
                calculateCart()
                updateCartVisibility()
            }
        })

        updateCartVisibility()

        with(binding) {
            txtEmpty.visibility = if (managmentCart.getListCart().isEmpty()) View.VISIBLE else View.GONE
            scrollView.visibility = if (managmentCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun updateCartVisibility() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.txtEmpty.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            binding.clCarIsEmpty.visibility = View.GONE
        } else {
            binding.txtEmpty.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            binding.clCarIsEmpty.visibility = View.VISIBLE
        }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 10.0
        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100

        with(binding) {
            txtTotalFee.text = "$ $itemTotal"
            txtTax.text = "$ $tax"
            txtDelivery.text = "$ $delivery"
            txtTotal.text = "$ $total"
        }
    }

    private fun setVariable() {
        binding.btnBack.setOnClickListener { finish() }
    }
}
