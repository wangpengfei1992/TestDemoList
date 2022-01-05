package com.wpf.koindemo.demo

class HelloRepositoryImpl : HelloRepository{
    override fun getHello(): String {
        return "HelloRepositoryImpl    hello world"
    }

}