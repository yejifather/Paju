package com.tenkitchen.paju

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.tenkitchen.classes.RetrofitManager
import com.tenkitchen.interfaces.IPopupDialog
import com.tenkitchen.objects.CommonUtil
import com.tenkitchen.objects.Constants.TAG
import com.tenkitchen.objects.RESPONSE_STATE
import kotlinx.android.synthetic.main.popup_detail.*
import org.json.JSONArray
import org.json.JSONObject


class PopupDialog(context: Context, Interface: IPopupDialog, setNum: Int): Dialog(context) {

    // 액티비티에서 인터페이스를 받아옴
    private var iPopupDialog: IPopupDialog = Interface
    private var m_setNum: Int = setNum

    private lateinit var confirmButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_detail)

        confirmButton = findViewById(R.id.btnConfirm)

        // 배경을 투명하게 함
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Confirm 버튼 클릭 시 onConfirmButtonClicked 호출 후 종료
        confirmButton.setOnClickListener {
            iPopupDialog.onConfirmButtonClicked()
            dismiss()
        }
        
        getSettlementDetails()
    }
    
    private fun getSettlementDetails() {
        RetrofitManager.instance.getSettlementDetails(set_num = m_setNum, completion = {
                responseState, responseBody ->

            when(responseState) {
                RESPONSE_STATE.OK -> {
                    Log.d(TAG, "api 호출 성공 : $responseBody")

                    val typeface = ResourcesCompat.getFont(context, R.font.app_main_font)

                    var json = JSONObject(responseBody)
                    var regDate: String = json.get("regDate").toString()
                    var memHp: String = json.get("memHp").toString()
                    var memNum: Int = json.get("memNum").toString().toInt();

                    var productList: JSONArray = JSONArray(json.get("productList").toString())
                    var memberList: JSONArray = JSONArray(json.get("memberList").toString())

                    txtBuyDate.text = regDate
                    txtMemHp.text = memHp

                    if ( memberList.length() > 0 ) {
                        llCustomerBox.visibility = View.VISIBLE
                    }

                    if ( productList.length() > 0 ) {
                        // 리니어레이아웃 생성 - 부모 llCustomerList
                        val llGroup = LinearLayout(context)
                        llGroup.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        llGroup.orientation = LinearLayout.HORIZONTAL
                        llGroup.weightSum = 1f
                        llGroup.setBackgroundColor(context.resources.getColor(R.color.column_gray))

                        // 제품명 LinearLayout - 부모 llGroup
                        val llProduct = LinearLayout(context)
                        llProduct.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.7f
                        )

                        // 제품명 텍스트뷰 - 부모 llProduct
                        val txtProName = TextView(context)
                        txtProName.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.7f
                        )
                        txtProName.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtProName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtProName.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtProName.text = "제품명"
                        txtProName.typeface = typeface

                        llGroup.addView(txtProName, llProduct.layoutParams)

                        // 소비자가 LinearLayout - 부모 llGroup
                        var llOriPrice = LinearLayout(context)
                        llOriPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )

                        // 소비자가 텍스트뷰 - 부모 llOriPrice
                        val txtOriPrice = TextView(context)
                        txtOriPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )
                        txtOriPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtOriPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtOriPrice.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtOriPrice.text = "소비자가"
                        txtOriPrice.typeface = typeface
                        txtOriPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                        llGroup.addView(txtOriPrice, llOriPrice.layoutParams)

                        // 할인 LinearLayout - 부모 llGroup
                        var llDiscountRate = LinearLayout(context)
                        llDiscountRate.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )

                        // 할인 텍스트뷰 - 부모 llDiscountRate
                        val txtDiscountRate = TextView(context)
                        txtDiscountRate.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )
                        txtDiscountRate.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtDiscountRate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtDiscountRate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtDiscountRate.text = "할인"
                        txtDiscountRate.typeface = typeface
                        txtDiscountRate.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                        llGroup.addView(txtDiscountRate, llDiscountRate.layoutParams)

                        // 판매가 LinearLayout - 부모 llGroup
                        var llSetdPrice = LinearLayout(context)
                        llSetdPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )

                        // 판매가 텍스트뷰 - 부모 llDiscountRate
                        val txtSetdPrice = TextView(context)
                        txtSetdPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.1f
                        )
                        txtSetdPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtSetdPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtSetdPrice.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtSetdPrice.text = "판매가"
                        txtSetdPrice.typeface = typeface
                        txtSetdPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                        llGroup.addView(txtSetdPrice, llSetdPrice.layoutParams)

                        llProductList.addView(llGroup)
                        
                        for ( i in 0 until productList.length() ) {
                            val info: JSONObject = productList.getJSONObject(i)

                            // 리니어레이아웃 생성 - 부모 llCustomerList
                            val llGroup = LinearLayout(context)
                            llGroup.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            llGroup.orientation = LinearLayout.HORIZONTAL
                            llGroup.weightSum = 1f

                            // 제품명 LinearLayout - 부모 llGroup
                            val llProduct = LinearLayout(context)
                            llProduct.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.7f
                            )

                            // 제품명 텍스트뷰 - 부모 llProduct
                            val txtProName = TextView(context)
                            txtProName.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.7f
                            )
                            txtProName.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtProName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtProName.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtProName.text = info.get("proName").toString()
                            txtProName.typeface = typeface

                            llGroup.addView(txtProName, llProduct.layoutParams)

                            // 소비자가 LinearLayout - 부모 llGroup
                            var llOriPrice = LinearLayout(context)
                            llOriPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )

                            // 소비자가 텍스트뷰 - 부모 llOriPrice
                            val txtOriPrice = TextView(context)
                            txtOriPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )
                            txtOriPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtOriPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtOriPrice.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtOriPrice.text = CommonUtil.digit2Comma(info.get("oriPrice").toString().toInt()) + "원"
                            txtOriPrice.typeface = typeface
                            txtOriPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                            llGroup.addView(txtOriPrice, llOriPrice.layoutParams)

                            // 할인 LinearLayout - 부모 llGroup
                            var llDiscountRate = LinearLayout(context)
                            llDiscountRate.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )

                            // 할인 텍스트뷰 - 부모 llDiscountRate
                            val txtDiscountRate = TextView(context)
                            txtDiscountRate.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )
                            txtDiscountRate.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtDiscountRate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtDiscountRate.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtDiscountRate.text = info.get("discountRate").toString() + "%"
                            txtDiscountRate.typeface = typeface
                            txtDiscountRate.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                            llGroup.addView(txtDiscountRate, llDiscountRate.layoutParams)

                            // 판매가 LinearLayout - 부모 llGroup
                            var llSetdPrice = LinearLayout(context)
                            llSetdPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )

                            // 판매가 텍스트뷰 - 부모 llDiscountRate
                            val txtSetdPrice = TextView(context)
                            txtSetdPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0.1f
                            )
                            txtSetdPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtSetdPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtSetdPrice.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtSetdPrice.text = CommonUtil.digit2Comma(info.get("setdPrice").toString().toInt()) + "원"
                            txtSetdPrice.typeface = typeface
                            txtSetdPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                            llGroup.addView(txtSetdPrice, llSetdPrice.layoutParams)

                            llProductList.addView(llGroup)
                        }
                    }

                    // 구매자 정보
                    if ( memberList.length() > 0 ) {
                        // 헤더 부분 먼저 생성
                        // 리니어레이아웃 생성 - 부모 llProductList
                        val llGroup = LinearLayout(context)
                        llGroup.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        llGroup.orientation = LinearLayout.HORIZONTAL
                        llGroup.weightSum = 1f
                        llGroup.setBackgroundColor(context.resources.getColor(R.color.column_gray))

                        // 구매일 LinearLayout - 부모 llGroup
                        val llBuyDate = LinearLayout(context)
                        llBuyDate.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )

                        // 구매일 텍스트뷰 - 부모 llProduct
                        val txtBuyDate = TextView(context)
                        txtBuyDate.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        txtBuyDate.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtBuyDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtBuyDate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtBuyDate.text = "구매일"
                        txtBuyDate.typeface = typeface
                        txtBuyDate.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                        llGroup.addView(txtBuyDate, llBuyDate.layoutParams)

                        // 제품 LinearLayout - 부모 llGroup
                        var llProName = LinearLayout(context)
                        llProName.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )

                        // 제품 텍스트뷰 - 부모 llOriPrice
                        val txtProName = TextView(context)
                        txtProName.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        txtProName.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtProName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtProName.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtProName.text = "제품"
                        txtProName.typeface = typeface
                        txtProName.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                        llGroup.addView(txtProName, llProName.layoutParams)

                        // 결제금액 LinearLayout - 부모 llGroup
                        var llSetdPrice = LinearLayout(context)
                        llSetdPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )

                        // 결제금액 텍스트뷰 - 부모 llOriPrice
                        val txtSetdPrice = TextView(context)
                        txtSetdPrice.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        txtSetdPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                        txtSetdPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                        txtSetdPrice.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                        txtSetdPrice.text = "결제금액"
                        txtSetdPrice.typeface = typeface
                        txtSetdPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                        llGroup.addView(txtSetdPrice, llSetdPrice.layoutParams)

                        llCustomerList.addView(llGroup)
                        
                        var date: String = "";

                        for (i in 0 until memberList.length()) {
                            val info: JSONObject = memberList.getJSONObject(i)

                            if ( i > 0 && date != info.get("regDate") ) {
                                val llLine = LinearLayout(context);
                                // 리니어레이아웃 가로세로 사이즈 주기(px)
                                val params: LinearLayout.LayoutParams =
                                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                                llLine.layoutParams = params
                                llLine.setBackgroundColor(context.resources.getColor(R.color.column_gray))
                                llCustomerList.addView(llLine)
                            }

                            // 리니어레이아웃 생성 - 부모 llProductList
                            val llGroup = LinearLayout(context)
                            llGroup.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            llGroup.orientation = LinearLayout.HORIZONTAL
                            llGroup.weightSum = 1f

                            // 구매일 LinearLayout - 부모 llGroup
                            val llBuyDate = LinearLayout(context)
                            llBuyDate.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )

                            // 구매일 텍스트뷰 - 부모 llProduct
                            val txtBuyDate = TextView(context)
                            txtBuyDate.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                            txtBuyDate.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtBuyDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtBuyDate.setTextColor(ContextCompat.getColor(context!!, R.color.black))

                            if (date == info.get("regDate").toString()) {
                                txtBuyDate.setTextColor(Color.TRANSPARENT)
                            } else {
                                date = info.get("regDate").toString()
                            }

                            txtBuyDate.text = info.get("regDate").toString()
                            txtBuyDate.typeface = typeface
                            txtBuyDate.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                            llGroup.addView(txtBuyDate, llBuyDate.layoutParams)

                            // 제품 LinearLayout - 부모 llGroup
                            var llProName = LinearLayout(context)
                            llProName.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )

                            // 제품 텍스트뷰 - 부모 llOriPrice
                            val txtProName = TextView(context)
                            txtProName.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                            txtProName.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtProName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtProName.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtProName.text = info.get("proName").toString()
                            txtProName.typeface = typeface
                            txtProName.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                            llGroup.addView(txtProName, llProName.layoutParams)

                            // 결제금액 LinearLayout - 부모 llGroup
                            var llSetdPrice = LinearLayout(context)
                            llSetdPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )

                            // 결제금액 텍스트뷰 - 부모 llOriPrice
                            val txtSetdPrice = TextView(context)
                            txtSetdPrice.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                            txtSetdPrice.setPadding(CommonUtil.intToDp(this.context, 5))
                            txtSetdPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
                            txtSetdPrice.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                            txtSetdPrice.text = CommonUtil.digit2Comma(info.get("setdPrice").toString().toInt()) + "원"
                            txtSetdPrice.typeface = typeface
                            txtSetdPrice.textAlignment = View.TEXT_ALIGNMENT_TEXT_START

                            llGroup.addView(txtSetdPrice, llSetdPrice.layoutParams)

                            llCustomerList.addView(llGroup)
                        }
                    }
                }
                RESPONSE_STATE.FAILURE -> {
                    Toast.makeText(this.context, "api 호출 에러", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }
}