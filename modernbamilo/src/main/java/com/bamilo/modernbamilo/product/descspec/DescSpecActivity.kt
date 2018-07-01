package com.bamilo.modernbamilo.product.descspec

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.app.BaseActivity
import com.bamilo.modernbamilo.util.extension.replaceFragmentInActivity

fun startActivity(invokerContext: Context) {
    val intent = Intent(invokerContext, DescSpecActivity::class.java)
    invokerContext.startActivity(intent)
}

class DescSpecActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desc_spec)

        replaceFragmentInActivity(DescSpecFragment.newInstance("1234"), R.id.activityDescSpec_frameLayout_container)
    }
}
