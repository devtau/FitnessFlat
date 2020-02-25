package com.devtau.ironHeroes.ui.fragments.trainingsList

import androidx.lifecycle.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.adapters.HeroesSpinnerAdapter.HeroSelectedListener
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.Training
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.data.source.repositories.TrainingsRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.PreferencesManager
import kotlinx.coroutines.launch

class TrainingsViewModel(
    private val trainingsRepository: TrainingsRepository,
    private val heroesRepository: HeroesRepository,
    private val prefs: PreferencesManager?
): ViewModel(), HeroSelectedListener {

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val forceUpdateHeroes = MutableLiveData<Boolean>(false)
    private val forceUpdateTrainings = MutableLiveData<Boolean>()

    private val _trainings: LiveData<List<Training>> = forceUpdateTrainings.switchMap { forceUpdate ->
        if (forceUpdate) {
            viewModelScope.launch {
                trainingsRepository.fetchItemsFromBackend()
                heroesRepository.fetchItemsFromBackend()
            }
        }
        trainingsRepository.observeList().distinctUntilChanged().switchMap { processTrainingsResult(it) }
    }
    val trainings: LiveData<List<Training>> = _trainings
    private var isTrainingsListLoaded = false
    val trainingsEmpty: LiveData<Boolean> = _trainings.map { isTrainingsListLoaded && it.isEmpty() }


    val showChampionFilter: LiveData<Boolean> = PreferencesManager.observeShowChampionFilter()
    val champions: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.CHAMPION).switchMap {
            val result = MutableLiveData<List<Hero>>()
            result.value = if (it is Success) it.data else emptyList()
            result
        }
    }
    var selectedChampionId: Long? = prefs?.favoriteChampionId

    val showHeroFilter: LiveData<Boolean> = PreferencesManager.observeShowHeroFilter()
    val heroes: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(HumanType.HERO).switchMap {
            val result = MutableLiveData<List<Hero>>()
            result.value = if (it is Success) it.data else emptyList()
            result
        }
    }
    var selectedHeroId: Long? = prefs?.favoriteHeroId

    private val _openTrainingEvent = MutableLiveData<Event<Long>>()
    val openTrainingEvent: LiveData<Event<Long>> = _openTrainingEvent


    override fun onHeroSelected(hero: Hero?, humanType: HumanType) {
        when (humanType) {
            HumanType.CHAMPION -> selectedChampionId = hero?.id
            HumanType.HERO -> selectedHeroId = hero?.id
        }
        forceUpdateTrainings.value = false
        Logger.d(LOG_TAG, "onHeroSelected. hero=$hero, humanType=$humanType")
    }

    //uses Constants.OBJECT_ID_NA for adding new training
    fun openTraining(id: Long) {
        _openTrainingEvent.value = Event(id)
    }


    private fun processTrainingsResult(result: Result<List<Training>>): LiveData<List<Training>> =
        if (result is Success) {
            Logger.d(LOG_TAG, "got new trainings list. size=${result.data.size}")
            filterTrainingsIfPossible(result.data, selectedChampionId, selectedHeroId, prefs)
        } else {
            _snackbarText.value = Event(R.string.error_trainings_list)
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
            Logger.w(LOG_TAG, "filterTrainingsIfPossible. some data not ready. aborting")
            return liveData
        }

        saveState(selectedChampionId, selectedHeroId, prefs)

        viewModelScope.launch {
            liveData.value = filterTrainings(trainings, selectedChampionId, selectedHeroId)
            Logger.d(LOG_TAG, "filterTrainingsIfPossible. trainings filtered size=${liveData.value?.size}")
            isTrainingsListLoaded = true
        }
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


    companion object {
        private const val LOG_TAG = "TrainingsViewModel"
    }
}