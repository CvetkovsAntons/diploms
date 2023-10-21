package com.example.fitnessapp.Database

data class ExerciseList(
    var muscles : String ?= null,
    var description : String ?= null,
    var equipment : String ?= null,
    var image_1 : String ?= null,
    var image_full : String ?= null,
    var name : String ?= null
)
