package ro.blaxphoenix.touchreflex.ui

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ro.blaxphoenix.touchreflex.db.HighScoreItem
import ro.blaxphoenix.touchreflex.db.HighScoreRepository

class HighScoreViewModel(private val highScoreRepository: HighScoreRepository) : ViewModel() {

    val allHighScoreItems: LiveData<MutableList<HighScoreItem>> =
        highScoreRepository.allHighScoreItems.asLiveData()

    fun insert(highScoreItem: HighScoreItem) =
        viewModelScope.launch { highScoreRepository.insert(highScoreItem) }

    @Suppress("unused")
    fun delete(highScoreItem: HighScoreItem) =
        viewModelScope.launch { highScoreRepository.delete(highScoreItem) }

    class HighScoreViewModelFactory(private val highScoreRepository: HighScoreRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HighScoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HighScoreViewModel(highScoreRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}