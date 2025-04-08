package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class SimpleCalculatorActivity : AppCompatActivity() {

    // Flagi sterujące stanem kalkulatora
    private var isOperatorPossible = false
    private var isDotPossible = true
    private var isResult = false
    private var isNumberPossible = true
    private var isDotDetected = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputTextView = findViewById<TextView>(R.id.inputTextView)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        outState.putString("inputText", inputTextView.text.toString())
        outState.putString("resultText", resultTextView.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_simple_calculator)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeCalculator()
        savedInstanceState?.let {
            val inputTextView = findViewById<TextView>(R.id.inputTextView)
            val resultTextView = findViewById<TextView>(R.id.resultTextView)
            inputTextView.text = it.getString("inputText", "")
            resultTextView.text = it.getString("resultText", "")
        }
    }

    private fun initializeCalculator() {
        val inputTextView = findViewById<TextView>(R.id.inputTextView)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Inicjalizacja przycisków cyfr
        val button0 = findViewById<Button>(R.id.zeroButton)
        val button1 = findViewById<Button>(R.id.oneButton)
        val button2 = findViewById<Button>(R.id.twoButton)
        val button3 = findViewById<Button>(R.id.threeButton)
        val button4 = findViewById<Button>(R.id.fourButton)
        val button5 = findViewById<Button>(R.id.fiveButton)
        val button6 = findViewById<Button>(R.id.sixButton)
        val button7 = findViewById<Button>(R.id.sevenButton)
        val button8 = findViewById<Button>(R.id.eightButton)
        val button9 = findViewById<Button>(R.id.nineButton)

        // Inicjalizacja przycisków operatorów i innych
        val buttonDot = findViewById<Button>(R.id.decimalButton)
        val buttonPlus = findViewById<Button>(R.id.plusButton)
        val buttonMinus = findViewById<Button>(R.id.minusButton)
        val buttonMultiply = findViewById<Button>(R.id.multiplyButton)
        val buttonDivide = findViewById<Button>(R.id.divideButton)
        val buttonCalculate = findViewById<Button>(R.id.calculateButton)
        val buttonClearAll = findViewById<Button>(R.id.clearAllButton)
        val clearBack = findViewById<Button>(R.id.clearButton)
        val changeSymbol = findViewById<Button>(R.id.changeSymbolButton)

        fun evaluateExpression(expression: String): String? {
            return try {
                val exp: Expression = ExpressionBuilder(expression).build()
                val result = exp.evaluate()
                result.toString()
            } catch (e: Exception) {
                null
            }
        }

        fun evaluateResultTextView() {
            val input = inputTextView.text.toString()
            val result = evaluateExpression(input)
            resultTextView.text = result?.toString() ?: ""
        }

        fun handleNumberClick(button: Button) {
            inputTextView.append(button.text)
            inputTextView.text = inputTextView.text // odświeżanie textView, żeby input zmieniał wielkość czcionki w zależności od jego długości
            isOperatorPossible = true
            isNumberPossible = true
            evaluateResultTextView()
        }

        fun handleOperatorClick(button: Button) {
            val input = inputTextView.text.toString()
            if (input.isEmpty()) {
                if(button.text == "-") {
                    inputTextView.append(button.text)
                    isOperatorPossible = false
                    isNumberPossible = true
                    return
                }
                Toast.makeText(this, "Niepoprawna operacja: brak liczby", Toast.LENGTH_SHORT).show()
                return
            }
            if (input.last() in arrayOf('+', '-', '*', '/', '.')) {
                Toast.makeText(this, "Niepoprawna operacja: dwa operatory obok", Toast.LENGTH_SHORT).show()
                return
            }
            inputTextView.append(button.text)
            inputTextView.text = inputTextView.text // odświeżanie textView, żeby input zmieniał wielkość czcionki w zależności od jego długości
            isOperatorPossible = false
            isDotDetected = false
            isDotPossible = true
        }

        fun handleDotClick() {
            val input = inputTextView.text.toString()
            // Jeśli ciąg jest pusty lub ostatni znak to operator, wstaw "0."
            if (input.isEmpty() || input.last() in arrayOf('+', '-', '*', '/')) {
                inputTextView.append("0.")
                isDotDetected = true
                isOperatorPossible = false
                isDotPossible = false
            } else if (!isDotDetected) {
                inputTextView.append(".")
                isDotDetected = true
                isOperatorPossible = false
                isDotPossible = false
            } else {
                Toast.makeText(this, "Niepoprawna operacja: wielokrotna kropka", Toast.LENGTH_SHORT).show()
            }
        }

        // Ustawienie listenerów dla przycisków cyfr
        button0.setOnClickListener { handleNumberClick(button0) }
        button1.setOnClickListener { handleNumberClick(button1) }
        button2.setOnClickListener { handleNumberClick(button2) }
        button3.setOnClickListener { handleNumberClick(button3) }
        button4.setOnClickListener { handleNumberClick(button4) }
        button5.setOnClickListener { handleNumberClick(button5) }
        button6.setOnClickListener { handleNumberClick(button6) }
        button7.setOnClickListener { handleNumberClick(button7) }
        button8.setOnClickListener { handleNumberClick(button8) }
        button9.setOnClickListener { handleNumberClick(button9) }

        buttonDot.setOnClickListener { handleDotClick() }
        buttonPlus.setOnClickListener { handleOperatorClick(buttonPlus) }
        buttonMinus.setOnClickListener { handleOperatorClick(buttonMinus) }
        buttonDivide.setOnClickListener { handleOperatorClick(buttonDivide) }
        buttonMultiply.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty() || input.last() in arrayOf('+', '-', '*', '/', '.')) {
                Toast.makeText(this, "Niepoprawna operacja: nie można dodać operatora", Toast.LENGTH_SHORT).show()
            } else {
                inputTextView.append("*")
                isOperatorPossible = false
                isDotDetected = false
                isDotPossible = true
            }
        }

        buttonCalculate.setOnClickListener {
            val expression = inputTextView.text.toString()
            val result = evaluateExpression(expression)
            if (result != null) {
                inputTextView.text = result.toString()
                resultTextView.text = ""
                isResult = true
                isOperatorPossible = true
                isDotPossible = true
                isDotDetected = false
            } else {
                Toast.makeText(this, "Błąd: Niepoprawne wyrażenie", Toast.LENGTH_SHORT).show()
            }
        }

        buttonClearAll.setOnClickListener {
            inputTextView.text = ""
            resultTextView.text = ""
            isOperatorPossible = false
            isDotPossible = true
            isDotDetected = false
            isResult = false
            isNumberPossible = true
        }

        clearBack.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isNotEmpty()) {
                inputTextView.text = input.substring(0, input.length - 1)
                if (inputTextView.text.isNotEmpty()) {
                    val lastChar = inputTextView.text.last()
                    isOperatorPossible = lastChar !in arrayOf('+', '-', '*', '/', '.')
                    if (!inputTextView.text.contains(".")) {
                        isDotDetected = false
                        isDotPossible = true
                    }
                    evaluateResultTextView()
                } else {
                    resultTextView.text = ""
                }
            }
        }

        // Zmiana znaku ostatniej liczby w równaniu
        changeSymbol.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Brak liczby do zmiany znaku", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Szukamy początku ostatniej liczby (uwzględniając możliwość jej ujemności)
            var i = input.length - 1
            while (i >= 0 && (input[i].isDigit() || input[i] == '.')) {
                i--
            }
            if (i >= 0 && input[i] == '-' && (i == 0 || input[i - 1] in arrayOf('+', '-', '*', '/'))) {
                i--
            }
            val startIndex = i + 1
            if (startIndex >= input.length) return@setOnClickListener

            val lastNumber = input.substring(startIndex)
            val newLastNumber = if (lastNumber.startsWith("-")) {
                lastNumber.substring(1)
            } else {
                "-$lastNumber"
            }
            val newExpression = input.substring(0, startIndex) + newLastNumber
            inputTextView.text = newExpression
            evaluateResultTextView()
        }
    }
}
