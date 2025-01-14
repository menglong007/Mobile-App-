package kh.edu.rupp.ite.viewmodelv3.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.activity.LoginActivity
import kh.edu.rupp.ite.viewmodelv3.databinding.LogOutBinding
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper.clearProfile

class LogOutDialogFragment : DialogFragment() {

    private lateinit var binding: LogOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedDialog) // Ensure the custom rounded style is set
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogOutBinding.inflate(inflater, container, false)

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvLogout.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.remove("TOKEN")
            editor.apply()

            clearProfile(requireContext())
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Make the background transparent to apply the rounded corners
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_rounded_dialog, null))
    }

}


