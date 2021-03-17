package com.devtau.ironHeroes.ui.fragments.trainingsList

import androidx.lifecycle.*
import com.devtau.ironHeroes.BaseViewModel
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.IronSpinnerAdapter.ItemSelectedListener
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.SpinnerItem
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.prefs.PreferencesManager
import kotlinx.coroutines.launch
import timber.log.Timber

class TrainingsViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val prefs: PreferencesManager?
): BaseViewModel() {

    private val forceUpdateHeroes = MutableLiveData(false)
    private val forceUpdateTrainings = MutableLiveData<Boolean>()

    private val _trainings: LiveData<List<Training>> = forceUpdateTrainings.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch {
                trainingsRepository.fetchItemsFromBackend()
                heroesRepository.fetchItemsFromBackend()
            }
        }
        trainingsRepository.observeList().distinctUntilChanged().switchMap { processTrainingsFromDB(it) }
    }
    val trainings: LiveData<List<Training>> = _trainings
    private var isTrainingsListLoaded = false
    val trainingsEmpty: LiveData<Boolean> = _trainings.map { isTrainingsListLoaded && it.isEmpty() }


    val showChampionFilter: LiveData<Boolean> = PreferencesManager.observeShowChampionFilter()
    val champions: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.CHAMPION).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    var selectedChampionId: Long? = prefs?.favoriteChampionId

    val showHeroFilter: LiveData<Boolean> = PreferencesManager.observeShowHeroFilter()
    val heroes: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.HERO).switchMap {
            MutableLiveData(if (it is Success) it.data else emptyList())
        }
    }
    var selectedHeroId: Long? = prefs?.favoriteHeroId ?: 0


    val heroSelectedListener = object: ItemSelectedListener {
        override fun onItemSelected(item: SpinnerItem?, humanType: HumanType?) {
            when (humanType) {
                HumanType.CHAMPION -> {
                    if (selectedChampionId != item?.id) {
                        selectedChampionId = item?.id
                        forceUpdateTrainings.value = false
                    }
                }
                HumanType.HERO -> {
                    if (selectedHeroId != item?.id) {
                        selectedHeroId = item?.id
                        forceUpdateTrainings.value = false
                    }
                }
            }
        }
    }


    val openTrainingEvent = MutableLiveData<Event<Long>>()
    fun addTraining() = openTraining(Constants.OBJECT_ID_NA)
    fun openTraining(id: Long?) {
        if (id != null) openTrainingEvent.value = Event(id)
    }


    private fun processTrainingsFromDB(result: Result<List<Training>?>): LiveData<List<Training>> =
        if (result is Success && result.data != null) {
            Timber.d("got new trainings list. size=${result.data.size}")
            filterTrainingsIfPossible(result.data, selectedChampionId, selectedHeroId, prefs)
        } else {
            snackbarText.value = Event(R.string.error_trainings_list)
            MutableLiveData(emptyList())
        }

    private fun filterTrainingsIfPossible(
        trainings: List<Training>?,
        selectedChampionId: Long?,
        selectedHeroId: Long?,
        prefs: PreferencesManager?
    ): LiveData<List<Training>> {
        val liveData = MutableLiveData<List<Training>>()

        if (trainings == null) {
            Timber.w("filterTrainingsIfPossible. some data not ready. aborting")
            return liveData
        }

        saveState(selectedChampionId, selectedHeroId, prefs)

        liveData.value = filterTrainings(trainings, selectedChampionId, selectedHeroId)
        Timber.d("filterTrainingsIfPossible. trainings filtered size=${liveData.value?.size}")
        isTrainingsListLoaded = true
        return liveData
    }

    private fun filterTrainings(list: List<Training>, championId: Long?, heroId: Long?): List<Training> {
        if (championId == null && heroId == null) return list
        val filtered = ArrayList<Training>()
        for (next in list)
            if ((championId == null || next.championId == championId)
                && (heroId == null || next.heroId == heroId)) filtered.add(next)
        return filtered
    }

    private fun saveState(championId: Long?, heroId: Long?, prefs: PreferencesManager?) {
        if (prefs?.favoriteChampionId != championId)
            prefs?.favoriteChampionId = championId
        if (prefs?.favoriteHeroId != heroId)
            prefs?.favoriteHeroId = heroId
    }
}