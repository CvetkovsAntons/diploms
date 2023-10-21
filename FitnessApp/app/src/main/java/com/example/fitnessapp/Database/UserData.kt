package com.example.fitnessapp.Database

data class UserData(
    var name : String ?= null,
    var age : Number ?= null,
    var height : Number ?= null,
    var weight : Number ?= null,
    var finished : Long ?= null,
)
