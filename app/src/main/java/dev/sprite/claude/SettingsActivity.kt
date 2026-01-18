package dev.sprite.claude

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var spriteNameInput: TextInputEditText
    private lateinit var tokenInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferencesManager = PreferencesManager(this)

        initViews()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        spriteNameInput = findViewById(R.id.spriteNameInput)
        tokenInput = findViewById(R.id.tokenInput)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val spriteName = preferencesManager.spriteNameFlow.first()
            val token = preferencesManager.tokenFlow.first()

            spriteNameInput.setText(spriteName ?: "")
            tokenInput.setText(token ?: "")
        }
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val spriteName = spriteNameInput.text.toString().trim()
        val token = tokenInput.text.toString().trim()

        if (spriteName.isBlank() || token.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            preferencesManager.saveSpriteName(spriteName)
            preferencesManager.saveToken(token)

            Toast.makeText(this@SettingsActivity, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
