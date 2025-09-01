package com.leo.paleorecipes.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.google.android.material.textfield.TextInputEditText

/**
 * A simple extension of TextInputEditText that uses the default Material theming
 * and ensures proper text color theming.
 */
class BlackTextInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle,
) : TextInputEditText(context, attrs, defStyleAttr)
