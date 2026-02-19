package com.aiagent.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aiagent.R
import com.aiagent.databinding.ActivityMainBinding
import com.aiagent.ui.chat.ChatActivity
import com.aiagent.ui.generate.GenerateFragment
import com.aiagent.ui.history.ProjectHistoryFragment
import com.aiagent.ui.home.HomeFragment
import com.aiagent.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
        
        binding.fabChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_generate -> {
                    loadFragment(GenerateFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(ProjectHistoryFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
