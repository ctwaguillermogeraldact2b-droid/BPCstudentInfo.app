package ph.edu.bpc.sis

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Locale

/** Clears stale validation errors as soon as the person starts fixing the field. */
fun EditText.doOnTextChange(action: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = action()
        override fun afterTextChanged(s: Editable?) = Unit
    })
}

/** 1.75 -> "1.75"; null -> "—" */
fun Double?.asGrade(): String = if (this == null) "—" else String.format(Locale.US, "%.2f", this)
