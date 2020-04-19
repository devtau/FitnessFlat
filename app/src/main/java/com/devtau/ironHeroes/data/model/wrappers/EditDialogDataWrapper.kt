package com.devtau.ironHeroes.data.model.wrappers

data class EditDialogDataWrapper(
    val heroId: Long,
    val trainingId: Long,
    val exerciseInTrainingId: Long,
    val position: Int = 0
)