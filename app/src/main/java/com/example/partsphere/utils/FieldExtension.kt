package com.example.partsphere.utils

import com.example.partsphere.R

// Extension function to map Field enum to string resource IDs
fun Field.toErrorMessage(): Int = when (this) {
    Field.FULL_NAME -> R.string.error_full_name_required
    Field.EMAIL -> R.string.error_email_invalid
    Field.PHONE -> R.string.error_phone_invalid
    Field.COMPANY_NAME -> R.string.error_company_name_required
    Field.GST_ID -> R.string.error_gst_invalid
    Field.ADDRESS -> R.string.error_address_short
    Field.CITY -> R.string.error_city_required
    Field.STATE -> R.string.error_state_required
    Field.PINCODE -> R.string.error_pincode_invalid
    Field.PASSWORD -> R.string.error_password_invalid
    Field.CONFIRM_PASSWORD -> R.string.error_confirm_password_mismatch
}