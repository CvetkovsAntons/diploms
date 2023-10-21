package com.example.fitnessapp.Database

import android.net.Uri
import com.google.rpc.Help

data class WorkoutExercisesPreview(
    var name : String ?= null,
    var equipment : String ?= null,
    var muscles : String ?= null,
    var image_1 : String ?= null,
    var image_full : String ?= null,
    var description : String ?= null
)
