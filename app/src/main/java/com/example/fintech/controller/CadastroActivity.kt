package com.example.fintech.controller

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fintech.R
import com.example.fintech.data.dao.TransactionDAO
import com.example.fintech.data.model.Transaction

class CadastroActivity : AppCompatActivity() {
    private lateinit var rgType: RadioGroup
    private lateinit var rbCredito: RadioButton
    private lateinit var rbDebito: RadioButton
    private lateinit var etDescription: EditText
    private lateinit var etValue: EditText
    private lateinit var btnSave: Button
    private lateinit var btnVoltar: Button
    private lateinit var transactionDAO: TransactionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        rgType = findViewById(R.id.rgType)
        rbCredito = findViewById(R.id.rbCredito)
        rbDebito = findViewById(R.id.rbDebito)
        etDescription = findViewById(R.id.etDescription)
        etValue = findViewById(R.id.etValue)
        btnSave = findViewById(R.id.btnSave)
        btnVoltar = findViewById(R.id.btnVoltar)

        transactionDAO = TransactionDAO(this)

        btnSave.setOnClickListener {
            saveTransaction()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun saveTransaction() {
        val description = etDescription.text.toString().trim()
        val valueText = etValue.text.toString().trim()

        if (description.isEmpty()) {
            Toast.makeText(this, "Por favor, digite uma descrição", Toast.LENGTH_SHORT).show()
            return
        }

        if (valueText.isEmpty()) {
            Toast.makeText(this, "Por favor, digite um valor", Toast.LENGTH_SHORT).show()
            return
        }

        val value = try {
            valueText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor, digite um valor válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (value <= 0) {
            Toast.makeText(this, "O valor deve ser maior que zero", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (rbCredito.isChecked) "CREDITO" else "DEBITO"
        val transaction = Transaction(
            type = type,
            description = description,
            value = value
        )

        val result = transactionDAO.insert(transaction)
        if (result != -1L) {
            Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show()
            clearFields()
        } else {
            Toast.makeText(this, "Erro ao salvar transação", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        etDescription.setText("")
        etValue.setText("")
        rbCredito.isChecked = true
    }
}