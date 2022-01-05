package com.wpf.koindemo

import com.wpf.koindemo.demo.HelloRepository
import com.wpf.koindemo.demo.HelloRepositoryImpl
import com.wpf.koindemo.demo.HelloRepositoryImpl2
import com.wpf.koindemo.demo.HelloViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


/*val appModel = module{
    single<HelloRepository>() { HelloRepositoryImpl() }
    viewModel {
        HelloViewModel(get())//
    }
}*/

/*多实例对象，需要根据名字区分*/
val appModel = module{
    single<HelloRepository>(named("HelloRepositoryImpl")) { HelloRepositoryImpl() }
    single<HelloRepository>(named("HelloRepositoryImpl2")) { HelloRepositoryImpl2() }
    viewModel(named("vm2")) {
        HelloViewModel(get(named("HelloRepositoryImpl2")))//
    }
    viewModel(named("vm1")){
        HelloViewModel(get(named("HelloRepositoryImpl")))//
    }
}