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
import net.objecthunter.exp4j.function.Function

class AdvancedCalculatorActivity : AppCompatActivity() {
    private var isOperatorPossible = false
    private var isDotPossible = true
    private var isResult = false
    private var isNumberPossible = true
    private var isDotDetected = false
    private var isAdvancedOperator = false
    private var advancedOperatorsStack = ArrayDeque<String>()

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

        setContentView(R.layout.activity_advanced_calculator)

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

        // Standardowe przyciski
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
        val buttonDot = findViewById<Button>(R.id.decimalButton)
        val buttonPlus = findViewById<Button>(R.id.plusButton)
        val buttonMinus = findViewById<Button>(R.id.minusButton)
        val buttonMultiply = findViewById<Button>(R.id.multiplyButton)
        val buttonDivide = findViewById<Button>(R.id.divideButton)
        val buttonCalculate = findViewById<Button>(R.id.calculateButton)
        val buttonClearAll = findViewById<Button>(R.id.clearAllButton)
        val clearBack = findViewById<Button>(R.id.clearButton)
        val changeSymbol = findViewById<Button>(R.id.changeSymbolButton)

        // Przyciski funkcji zaawansowanych
        val buttonSin = findViewById<Button>(R.id.sinusButton)
        val buttonCos = findViewById<Button>(R.id.cosinusButton)
        val buttonTan = findViewById<Button>(R.id.tangensButton)
        val buttonPercentage = findViewById<Button>(R.id.percentageButton)
        val buttonSquare = findViewById<Button>(R.id.squareRootButton)
        val buttonPower = findViewById<Button>(R.id.anyPowerButton)
        val buttonPowerTo2 = findViewById<Button>(R.id.power2Button)
        val buttonLogarithm = findViewById<Button>(R.id.logarithmButton)
        val buttonNaturalLogarithm = findViewById<Button>(R.id.naturalLogarithmButton)

        // Funkcja oceniająca wyrażenie z funkcjami logarytmicznymi
        fun evaluateExpression(exprString: String): String? {
            return try {
                // Funkcja logarytmu dziesiętnego
                val logFunction = object : Function("log", 1) {
                    override fun apply(vararg args: Double): Double {
                        return Math.log10(args[0])
                    }
                }
                // Funkcja logarytmu naturalnego
                val lnFunction = object : Function("ln", 1) {
                    override fun apply(vararg args: Double): Double {
                        return Math.log(args[0])
                    }
                }
                val exp: Expression = ExpressionBuilder(exprString)
                    .function(logFunction)
                    .function(lnFunction)
                    .build()
                val result = exp.evaluate()
                result.toString()
            } catch (e: Exception) {
                null
            }
        }

        fun evaluateResultTextView() {
            val input = inputTextView.text.toString()
            val result = evaluateExpression(input)
            if(result == "NaN") {
                resultTextView.text = ""
                return
            }
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
            if (input.last() in charArrayOf('+', '-', '*', '/', '.', '^')) {
                Toast.makeText(this, "Niepoprawna operacja: dwa operatory obok", Toast.LENGTH_SHORT)
                    .show()
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
            if (input.isEmpty() || input.last() in charArrayOf('+', '-', '*', '/', '^')) {
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
                Toast.makeText(this, "Niepoprawna operacja: wielokrotna kropka", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Pomocnicza funkcja, która wyszukuje ostatni numer w wyrażeniu
        // Wzorzec rozpoznaje opcjonalny znak minus, cyfry oraz opcjonalną kropkę z częścią dziesiętną
        fun applyUnaryOperation(op: String) {
            val expr = inputTextView.text.toString()
            val regex = Regex("(-?\\d+(\\.\\d+)?)$")
            val matchResult = regex.find(expr)
            if (matchResult != null) {
                val number = matchResult.value
                val prefix = expr.substring(0, matchResult.range.first)
                inputTextView.text = prefix + op + "(" + number + ")"
                isAdvancedOperator = true
                evaluateResultTextView()
            } else {
                Toast.makeText(this, "Brak liczby do operacji", Toast.LENGTH_SHORT).show()
            }
        }

        // Obsługa przycisków cyfr
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

        // Obsługa kropki dziesiętnej
        buttonDot.setOnClickListener { handleDotClick() }
        // Podstawowe operatory
        buttonPlus.setOnClickListener { handleOperatorClick(buttonPlus) }
        buttonMinus.setOnClickListener { handleOperatorClick(buttonMinus) }
        buttonDivide.setOnClickListener { handleOperatorClick(buttonDivide) }
        buttonMultiply.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty() || input.last() in charArrayOf('+', '-', '*', '/', '.', '^')) {
                Toast.makeText(
                    this,
                    "Niepoprawna operacja: nie można dodać operatora",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                inputTextView.append("*")
                isOperatorPossible = false
                isDotDetected = false
                isDotPossible = true
            }
        }

        // Funkcje zaawansowane – działają na ostatnim wpisanym numerze

        // Funkcje trygonometryczne
        buttonSin.setOnClickListener {
            applyUnaryOperation("sin")
            advancedOperatorsStack.addLast("sin(")
        }
        buttonCos.setOnClickListener {
            applyUnaryOperation("cos")
            advancedOperatorsStack.addLast("cos(")
        }
        buttonTan.setOnClickListener {
            applyUnaryOperation("tan")
            advancedOperatorsStack.addLast("tan(")
        }

        // Logarytmy
        buttonLogarithm.setOnClickListener {
            applyUnaryOperation("log")
            advancedOperatorsStack.addLast("log(")
        }
        buttonNaturalLogarithm.setOnClickListener {
            applyUnaryOperation("ln")
            advancedOperatorsStack.addLast("ln(")
        }

        // Pierwiastek
        buttonSquare.setOnClickListener {
            applyUnaryOperation("sqrt")
            advancedOperatorsStack.addLast("sqrt(")
        }

        // Procent – traktowany jako dzielenie przez 100 (działa jak wcześniej)
        buttonPercentage.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty() || input.last() in charArrayOf('+', '-', '*', '/', '.', '^')) {
                Toast.makeText(
                    this,
                    "Niepoprawna operacja: brak liczby do procenta",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                inputTextView.append("/100")
                isOperatorPossible = true
            }
        }

        // Potęga – użytkownik wpisuje dowolny wykładnik

        buttonPower.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty() || input.last() in charArrayOf('+', '-', '*', '/', '.', '^', '(')) {
                Toast.makeText(
                    this,
                    "Niepoprawna operacja: brak podstawy potęgi",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                inputTextView.append("^")
                isOperatorPossible = false
            }
        }
        // Potęga do kwadratu – dodaje automatycznie "^2"
        buttonPowerTo2.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isEmpty() || input.last() in charArrayOf('+', '-', '*', '/', '.', '^', '(')) {
                Toast.makeText(
                    this,
                    "Niepoprawna operacja: brak liczby do potęgowania",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                inputTextView.append("^2")
                isOperatorPossible = true
            }
        }

        // Przycisk obliczania wyniku – automatycznie uzupełnia brakujące nawiasy
        buttonCalculate.setOnClickListener {
            var expression = inputTextView.text.toString()
            val openCount = expression.count { it == '(' }
            val closeCount = expression.count { it == ')' }
            if (openCount > closeCount) {
                repeat(openCount - closeCount) {
                    expression += ")"
                }
            }
            val result = evaluateExpression(expression)
            if(result == "NaN") {
                Toast.makeText(this, "Błąd: Niepoprawne wyrażenie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

        // Czyszczenie całego wyrażenia
        buttonClearAll.setOnClickListener {
            inputTextView.text = ""
            resultTextView.text = ""
            isOperatorPossible = false
            isDotPossible = true
            isDotDetected = false
            isResult = false
            isNumberPossible = true
        }

        // Usuwanie ostatniego znaku
        clearBack.setOnClickListener {
            val input = inputTextView.text.toString()
            if (input.isNotEmpty()) {
                var lastChar = inputTextView.text.get(inputTextView.text.toString().length - 1)
                inputTextView.text = input.substring(0, input.length - 1)
                if (inputTextView.text.isNotEmpty()) {
                    if (isAdvancedOperator) {
                        if (lastChar.equals(')')) {
                            val lastAdvancedOperator = advancedOperatorsStack.last()
                            var index = inputTextView.text.toString().length
                            var char = inputTextView.text.toString().get(index - 1)
                            while (char != lastAdvancedOperator.get(0)) {
                                index--
                                char = inputTextView.text.toString().get(index - 1)
                            }
                            val tempString = inputTextView.text.toString()
                                .subSequence(index - 1, inputTextView.text.length)
                            if (lastAdvancedOperator in tempString) {
                                advancedOperatorsStack.removeLast()
                            }
                            inputTextView.text = inputTextView.text.subSequence(0, index - 1)
                        }
                    }
                    val lastChar = inputTextView.text.last()
                    isOperatorPossible = lastChar !in charArrayOf('+', '-', '*', '/', '.', '^')
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

        // Zmiana znaku
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
            if (i >= 0 && input[i] == '-' && (i == 0 || input[i - 1] in arrayOf(
                    '+',
                    '-',
                    '*',
                    '/'
                ))
            ) {
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
