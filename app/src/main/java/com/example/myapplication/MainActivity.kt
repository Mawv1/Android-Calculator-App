package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val simpleCalculatorButton = findViewById<Button>(R.id.simpleCalculatorButton)
        simpleCalculatorButton.setOnClickListener {
            startActivity(Intent(this, SimpleCalculatorActivity::class.java))
        }

        val aboutButton = findViewById<Button>(R.id.aboutButton)
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        val exitButton = findViewById<Button>(R.id.exitApplicationButton)
        exitButton.setOnClickListener {
            finish()
        }

        val advancedCalculatorButton = findViewById<Button>(R.id.advancedCalculatorButton)
        advancedCalculatorButton.setOnClickListener{
            startActivity(Intent(this, AdvancedCalculatorActivity::class.java))
        }
    }
}