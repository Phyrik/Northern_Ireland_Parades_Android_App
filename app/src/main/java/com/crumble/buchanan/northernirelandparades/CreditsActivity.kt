package com.crumble.buchanan.northernirelandparades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CreditsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        title = getString(R.string.credits_action_button_text)
    }
}