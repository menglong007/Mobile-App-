package kh.edu.rupp.ite.viewmodelv3.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.style.*
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.databinding.FormBinding

class FormActivity : AppCompatActivity() {

    private lateinit var binding: FormBinding
    private lateinit var editText: EditText
    private var isBoldActive = false
    private var isItalicsActive = false
    private var isHeaderActive = false
    private var isBulletedActive = false
    private var isCodeActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editText = binding.contentInput

        binding.titleInput.setText(intent.getStringExtra("TITLE"))
        binding.contentInput.setText(intent.getStringExtra("CONTENT"))
        val id = intent.getStringExtra("ID").toString()
        Log.d("intentIDFOREDIT","got $id")
        binding.bold.setOnClickListener { toggleBold() }
        binding.italics.setOnClickListener { toggleItalics() }
        binding.onHeader.setOnClickListener { toggleHeader() }
        binding.noFormat.setOnClickListener { clearFormatting() }
        binding.addBulleted.setOnClickListener { toggleBulleted() }
        binding.onCode.setOnClickListener { toggleCode() }

        setupTextWatcher()

        binding.onPost.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val content = binding.contentInput.text.toString()
            val intent = Intent(this, DetailActivity::class.java)
            if (title.isNotBlank() && content.isNotBlank()) {
                intent.putExtra("TITLE", title)
                intent.putExtra("CONTENT", content)
                if (id.isNotBlank()){
                    intent.putExtra("IDFOREDIT",id)

                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Title or Content cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }

        binding.backItem.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleBold() {
        val editTextContent = editText.text
        if (editTextContent.isNullOrEmpty()) return

        val start = editText.selectionStart
        val end = editText.selectionEnd

        if (start < 0 || end <= start) {
            Toast.makeText(this, "Please select text to apply bold", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedText = editTextContent.subSequence(start, end).toString()

        val processedText = if (selectedText.startsWith("**") && selectedText.endsWith("**")) {
            selectedText.substring(2, selectedText.length - 2)
        } else {
            "**$selectedText**"
        }

        val updatedText = SpannableStringBuilder(editTextContent)
        updatedText.replace(start, end, processedText)

        editText.text = updatedText
        editText.setSelection(start, start + processedText.length)

        isBoldActive = !isBoldActive
        updateButtonStyle(binding.bold, isBoldActive)
    }

    private fun toggleItalics() {
        val editTextContent = editText.text
        if (editTextContent.isNullOrEmpty()) return

        val start = editText.selectionStart
        val end = editText.selectionEnd

        if (start < 0 || end <= start) {
            // Show toast if no text is selected
            Toast.makeText(this, "Please select text to apply italics", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedText = editTextContent.subSequence(start, end).toString()

        val processedText = if (selectedText.startsWith("_") && selectedText.endsWith("_")) {
            selectedText.substring(1, selectedText.length - 1)
        } else {
            "_${selectedText}_"
        }

        val updatedText = SpannableStringBuilder(editTextContent)
        updatedText.replace(start, end, processedText)

        editText.text = updatedText
        editText.setSelection(start, start + processedText.length)

        isItalicsActive = !isItalicsActive
        updateButtonStyle(binding.italics, isItalicsActive)
    }

    private fun toggleHeader() {
        val editTextContent = editText.text
        if (editTextContent.isNullOrEmpty()) return

        val start = editText.selectionStart
        val end = editText.selectionEnd

        if (start < 0 || end <= start) {
            // Show toast if no text is selected
            Toast.makeText(this, "Please select text to apply header", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedText = editTextContent.subSequence(start, end).toString()

        val processedText = selectedText.lines().joinToString("\n") { line ->
            if (line.startsWith("#")) {
                line.substring(2)
            } else {
                "#$line"
            }
        }

        val updatedText = SpannableStringBuilder(editTextContent)
        updatedText.replace(start, end, processedText)

        editText.text = updatedText
        editText.setSelection(start, start + processedText.length)

        isHeaderActive = !isHeaderActive
        updateButtonStyle(binding.onHeader, isHeaderActive)
    }

    private fun toggleBulleted() {
        val editTextContent = editText.text
        if (editTextContent.isNullOrEmpty()) return

        val start = editText.selectionStart
        val end = editText.selectionEnd

        if (start < 0 || end <= start) {
            // Show toast if no text is selected
            Toast.makeText(this, "Please select text to apply bulleted list", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedText = editTextContent.subSequence(start, end).toString()

        val processedText = selectedText.lines().joinToString("\n") { line ->
            if (line.startsWith("- ")) {
                line.substring(2)
            } else {
                "- $line"
            }
        }

        val updatedText = SpannableStringBuilder(editTextContent)
        updatedText.replace(start, end, processedText)

        editText.text = updatedText
        editText.setSelection(start, start + processedText.length)

        isBulletedActive = !isBulletedActive
    }

    private fun toggleCode() {
        val editTextContent = editText.text
        if (editTextContent.isNullOrEmpty()) return

        val start = editText.selectionStart
        val end = editText.selectionEnd

        if (start < 0 || end <= start) {
            // Show toast if no text is selected
            Toast.makeText(this, "Please select text to apply code", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedText = editTextContent.subSequence(start, end).toString()

        val processedText = if (selectedText.startsWith("```") && selectedText.endsWith("```")) {
            selectedText.substring(3, selectedText.length - 3)
        } else {
            "```$selectedText```"
        }

        val updatedText = SpannableStringBuilder(editTextContent)
        updatedText.replace(start, end, processedText)

        editText.text = updatedText
        editText.setSelection(start, start + processedText.length)

        isCodeActive = !isCodeActive
    }

    private fun setupTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Ensure styles are applied to any newly typed text
                applyStylesToNewText(s)
            }
        })
    }

    private fun applyStylesToNewText(editable: Editable?) {
        editable?.let {
            val len = it.length
            if (len > 0) {
                val start = len - 1
                if (isBoldActive) {
                    it.setSpan(StyleSpan(Typeface.BOLD), start, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (isItalicsActive) {
                    it.setSpan(StyleSpan(Typeface.ITALIC), start, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    private fun updateButtonStyle(button: TextView, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundColor(Color.BLACK)
            button.setTextColor(Color.WHITE)
        } else {
            button.setBackgroundColor(Color.TRANSPARENT)
            button.setTextColor(Color.BLACK)
        }
    }

    private fun clearFormatting() {
        isBoldActive = false
        isItalicsActive = false
        isHeaderActive = false
        isBulletedActive = false
        isCodeActive = false

        updateButtonStyle(binding.bold, isBoldActive)
        updateButtonStyle(binding.italics, isItalicsActive)
        updateButtonStyle(binding.onHeader, isHeaderActive)
    }
}
