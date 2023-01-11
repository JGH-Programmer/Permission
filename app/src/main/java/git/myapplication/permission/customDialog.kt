package git.myapplication.permission
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import git.myapplication.permission.databinding.DialogLayoutBinding

class customDialog(val finishApp: () -> Unit): DialogFragment() {
    private var _binding: DialogLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogLayoutBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.dialogBtn.setOnClickListener {
            dismiss()
            openSettings()
            Log.d("Check","openSettings 실행")
            Toast.makeText(this.context, "権限を許可してください。", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openSettings(){
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }.run(::startActivity)
    }


}



