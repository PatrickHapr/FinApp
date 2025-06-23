package com.example.fintech.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fintech.R
import com.example.fintech.data.dao.TransactionDAO
import com.example.fintech.data.model.Transaction

class ExtratoActivity : AppCompatActivity() {
    private lateinit var btnTodas: Button
    private lateinit var btnCreditos: Button
    private lateinit var btnDebitos: Button
    private lateinit var tvSaldo: TextView
    private lateinit var lvTransactions: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var btnVoltarExtrato: Button
    private lateinit var transactionDAO: TransactionDAO
    private lateinit var adapter: TransactionAdapter
    private var currentTransactions = listOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extrato)

        btnTodas = findViewById(R.id.btnTodas)
        btnCreditos = findViewById(R.id.btnCreditos)
        btnDebitos = findViewById(R.id.btnDebitos)
        tvSaldo = findViewById(R.id.tvSaldo)
        lvTransactions = findViewById(R.id.lvTransactions)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnVoltarExtrato = findViewById(R.id.btnVoltarExtrato)

        transactionDAO = TransactionDAO(this)
        adapter = TransactionAdapter()
        lvTransactions.adapter = adapter

        btnTodas.setOnClickListener { loadAllTransactions() }
        btnCreditos.setOnClickListener { loadTransactionsByType("CREDITO") }
        btnDebitos.setOnClickListener { loadTransactionsByType("DEBITO") }
        btnVoltarExtrato.setOnClickListener { finish() }

        loadAllTransactions()
        updateBalance()
    }

    private fun loadAllTransactions() {
        currentTransactions = transactionDAO.getAllTransactions()
        adapter.updateTransactions(currentTransactions)
        updateVisibility()
    }

    private fun loadTransactionsByType(type: String) {
        currentTransactions = transactionDAO.getTransactionsByType(type)
        adapter.updateTransactions(currentTransactions)
        updateVisibility()
    }

    private fun updateBalance() {
        val balance = transactionDAO.calculateBalance()
        val balanceColor = if (balance >= 0) "#4CAF50" else "#F44336"
        tvSaldo.text = "Saldo: R$ ${"%.2f".format(balance)}"
        tvSaldo.setTextColor(android.graphics.Color.parseColor(balanceColor))
    }

    private fun updateVisibility() {
        if (currentTransactions.isEmpty()) {
            lvTransactions.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            lvTransactions.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }
    }

    inner class TransactionAdapter : BaseAdapter() {
        private var transactions = listOf<Transaction>()

        fun updateTransactions(newTransactions: List<Transaction>) {
            transactions = newTransactions
            notifyDataSetChanged()
        }

        override fun getCount(): Int = transactions.size

        override fun getItem(position: Int): Transaction = transactions[position]

        override fun getItemId(position: Int): Long = transactions[position].id.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@ExtratoActivity)
                .inflate(R.layout.item_transaction, parent, false)

            val transaction = transactions[position]
            val ivType = view.findViewById<ImageView>(R.id.ivType)
            val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
            val tvType = view.findViewById<TextView>(R.id.tvType)
            val tvValue = view.findViewById<TextView>(R.id.tvValue)

            tvDescription.text = transaction.description
            tvType.text = transaction.type

            if (transaction.type == "CREDITO") {
                ivType.setImageResource(R.drawable.ic_credito)
                tvValue.text = "+R$ ${"%.2f".format(transaction.value)}"
                tvValue.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            } else {
                ivType.setImageResource(R.drawable.ic_debito)
                tvValue.text = "-R$ ${"%.2f".format(transaction.value)}"
                tvValue.setTextColor(android.graphics.Color.parseColor("#F44336"))
            }

            return view
        }
    }
}