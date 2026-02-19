package com.aiagent.ui.codeeditor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.aiagent.R
import com.aiagent.databinding.ActivityCodeEditorBinding
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource

class CodeEditorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCodeEditorBinding
    
    private var fileName: String = ""
    private var codeContent: String = ""
    private var language: String = "text"
    
    companion object {
        private const val EXTRA_FILE_NAME = "file_name"
        private const val EXTRA_CODE = "code"
        private const val EXTRA_LANGUAGE = "language"
        
        fun newIntent(context: Context, fileName: String, code: String, language: String): Intent {
            return Intent(context, CodeEditorActivity::class.java).apply {
                putExtra(EXTRA_FILE_NAME, fileName)
                putExtra(EXTRA_CODE, code)
                putExtra(EXTRA_LANGUAGE, language)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodeEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: "untitled"
        codeContent = intent.getStringExtra(EXTRA_CODE) ?: ""
        language = intent.getStringExtra(EXTRA_LANGUAGE) ?: "text"
        
        setupToolbar()
        setupEditor()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = fileName
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun setupEditor() {
        binding.codeEditor.apply {
            // Set text
            setText(codeContent)
            
            // Set language
            val lang = when (language) {
                "kotlin" -> TextMateLanguage.create("source.kotlin", true)
                "java" -> TextMateLanguage.create("source.java", true)
                "python" -> TextMateLanguage.create("source.python", true)
                "javascript" -> TextMateLanguage.create("source.js", true)
                "xml" -> TextMateLanguage.create("text.xml", true)
                "json" -> TextMateLanguage.create("source.json", true)
                else -> null
            }
            
            lang?.let { setEditorLanguage(it) }
            
            // Configure editor
            isWordwrap = true
            isLineNumberEnabled = true
            isNonPrintablePaintingFlags = 0
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_code_editor, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_copy -> {
                copyToClipboard()
                true
            }
            R.id.action_share -> {
                shareCode()
                true
            }
            R.id.action_save -> {
                saveFile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun copyToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText(fileName, binding.codeEditor.text.toString())
        clipboard.setPrimaryClip(clip)
        // Show toast
    }
    
    private fun shareCode() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            putExtra(Intent.EXTRA_TEXT, binding.codeEditor.text.toString())
        }
        startActivity(Intent.createChooser(shareIntent, "Share Code"))
    }
    
    private fun saveFile() {
        // Save to downloads
        val content = binding.codeEditor.text.toString()
        // Use FileUtils to save
    }
}
