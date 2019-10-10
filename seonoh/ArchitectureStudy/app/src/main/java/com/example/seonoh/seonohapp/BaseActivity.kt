package com.example.seonoh.seonohapp

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.seonoh.seonohapp.contract.BaseContract

abstract class BaseActivity<P : BaseContract.Presenter>(
    @LayoutRes
    private val layoutRes: Int
) : AppCompatActivity(), BaseContract.View<P> {

    private lateinit var toast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }

    override fun showToast() {
        if (toast.view.isShown) {
            finish()
        } else {
            toast.show()
        }
    }

    override fun onBackPressed() {
        showToast()
    }

    override fun onDestroy() {
        presenter.clearDisposable()
        super.onDestroy()
    }
}