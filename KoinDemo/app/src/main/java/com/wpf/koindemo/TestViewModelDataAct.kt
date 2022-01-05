package com.wpf.koindemo

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner

/**
 *  Author: feipeng.wang
 *  Time:   2022/1/4
 *  Description : 测试ViewModel带参数
 */
class TestViewModelDataAct : AppCompatActivity() {
    private var testviewmodelfuc: TextView? = null
    private val viewModel: MyViewModel by viewModels {
        MyViewModelFactory(this, Repository(),
                intent.extras)
    }
    private val textDataObserver =
            Observer<String> { data -> testviewmodelfuc?.text = data }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_testdata_viewmodel)
        testviewmodelfuc = findViewById(R.id.testviewmodelfuc)
        viewModel.showTextDataNotifier.observe(this, textDataObserver)
        testviewmodelfuc?.setOnClickListener {
           Log.e("wpf","data:${viewModel.fetchValue()}")
        }
    }

    class MyViewModelFactory(owner: SavedStateRegistryOwner,
            private val repository: Repository, defaultArgs: Bundle? = null
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return MyViewModel(repository, handle) as T
        }
    }

    class MyViewModel(private val repository: Repository, savedStateHandle: SavedStateHandle) : ViewModel(), LifecycleObserver {

        companion object {
            const val KEY = "KEY"
        }

        private val showTextLiveData = savedStateHandle.getLiveData<String>(KEY)

        val showTextDataNotifier: LiveData<String>
            get() = showTextLiveData

        fun fetchValue():String {
              showTextLiveData.value = repository.getMessage()
            return showTextLiveData.value.toString()
        }
    }

    class Repository {
        fun getMessage() = "From Repository"
    }
}