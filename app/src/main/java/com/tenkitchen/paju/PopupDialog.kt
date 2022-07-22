package com.tenkitchen.paju

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.tenkitchen.interfaces.IPopupDialog
import com.tenkitchen.objects.Constants.TAG

class PopupDialog(context: Context, Interface: IPopupDialog, setNum: Int): Dialog(context) {

    // 액티비티에서 인터페이스를 받아옴
    private var iPopupDialog: IPopupDialog = Interface
    private var setNum: Int = setNum

    private lateinit var confirmButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_detail)

        Log.d(TAG, "onCreate: setNum ${setNum}")

        confirmButton = findViewById(R.id.btnConfirm)

        // 배경을 투명하게 함
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Confirm 버튼 클릭 시 onConfirmButtonClicked 호출 후 종료
        confirmButton.setOnClickListener {
            iPopupDialog.onConfirmButtonClicked()
            dismiss()
        }
    }
}