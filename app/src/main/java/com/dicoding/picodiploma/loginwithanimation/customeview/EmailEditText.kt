package com.dicoding.picodiploma.loginwithanimation.customeview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()
                if (s != null && !validEmail) {
                    error = "Format email salah"
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()
                if (s != null && !validEmail) {
                    error = "Format email salah"
                } else {
                    error = null
                }
            }
        })

    }
}