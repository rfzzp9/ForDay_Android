package com.forday.app.presentation.home.hobbysetting

sealed interface HomeHobbySettingSideEffect {
    data object NavigateBack : HomeHobbySettingSideEffect
}
