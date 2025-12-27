package com.forday.app.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.usecase.GetExampleDataUseCase
import com.forday.app.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val getExampleDataUseCase: GetExampleDataUseCase
) : BaseViewModel<ExampleSideEffect>() {

    private val _uiState: MutableStateFlow<ExampleModel> = MutableUiStateFlow { ExampleModel() }
    val uiState: StateFlow<ExampleModel> = _uiState.toStateIn()

    init {
        fetchExampleData()
    }

    fun onAction(action: ExampleAction) = viewModelScope.launch {
        when(action) {
            is ExampleAction.OnClose -> _sideEffectChannel.send(ExampleSideEffect.OnClose)
        }
    }

    private fun fetchExampleData() = viewModelScope.launch {
        flow {
            emit(getExampleDataUseCase().toPresentation())
        }.catch { throwable ->
            _sideEffectChannel.send(ExampleSideEffect.Error(throwable))
        }.collect {
            _uiState.update {
                it.copy(
                    id = it.id,
                    name = it.name,
                    description = it.description
                )
            }
        }
    }
}