package com.feasycom.feasyblue.dialog

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.activity.AgreementActivity
import kotlinx.android.synthetic.main.first_dialog.*


class FirstDialogFragment : DialogFragment(){

    var onAgree: (()-> Unit)? = null
    var onRefuse: (()-> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(false);
        dialog?.setOnKeyListener(object: DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                requireActivity().finish()
                return keyCode == KeyEvent.KEYCODE_BACK
            }
        });

        return inflater.inflate(R.layout.first_dialog, null)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userSpan = SpannableString(getString(R.string.user_agreement)).apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(requireContext(), AgreementActivity::class.java)
                    intent.putExtra("type", 1)
                    requireContext().startActivity(intent)
                }
            }, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            setSpan(
                ForegroundColorSpan(Color.parseColor("#D1894A")),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val privacySpan = SpannableString(getString(R.string.privacy_agreement)).apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(requireContext(), AgreementActivity::class.java)
                    intent.putExtra("type", 2)
                    requireContext().startActivity(intent)
                }
            }, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            setSpan(
                ForegroundColorSpan(Color.parseColor("#D1894A")),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        val stringBuilder = SpannableStringBuilder(getString(R.string.userAndPrivacyAgreementPrefix)).apply {
            append("《").append(userSpan).append("》")
            append("《").append(privacySpan).append("》")
            append(getString(R.string.userAndPrivacyAgreementSuffix))
        }
        with(contentTextView){
            movementMethod = LinkMovementMethod.getInstance()
            text = stringBuilder
            highlightColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        }
        agreeTextView.setOnClickListener {
            onAgree?.invoke()
        }

        refuseTextView.setOnClickListener {
            onRefuse?.invoke()
        }
    }




    override fun onResume() {
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        /**
         * remove the system dialog title and border background
         */
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        super.onResume()
    }

    companion object{
        val TAG = "FirstDialogFragment"
    }
}