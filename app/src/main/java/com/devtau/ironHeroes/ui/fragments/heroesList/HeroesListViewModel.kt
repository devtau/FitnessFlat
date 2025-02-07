package com.devtau.ironHeroes.ui.fragments.heroesList

import androidx.lifecycle.*
import com.devtau.ironHeroes.R
import com.devtau.ironHeroes.data.Result
import com.devtau.ironHeroes.data.Result.Success
import com.devtau.ironHeroes.data.model.Hero
import com.devtau.ironHeroes.data.model.wrappers.HeroIdWithHumanType
import com.devtau.ironHeroes.data.source.repositories.HeroesRepository
import com.devtau.ironHeroes.enums.HumanType
import com.devtau.ironHeroes.util.Constants
import com.devtau.ironHeroes.util.Event
import com.devtau.ironHeroes.util.Logger

class HeroesListViewModel(
    private val heroesRepository: HeroesRepository,
    private val humanType: HumanType
): ViewModel() {

    private val forceUpdateHeroes = MutableLiveData(false)

    private val _heroes: LiveData<List<Hero>> = forceUpdateHeroes.switchMap {
        heroesRepository.observeList(humanType).distinctUntilChanged().switchMap { processHeroesFromDB(it) }
    }
    val heroes: LiveData<List<Hero>> = _heroes


    val toolbarTitle: LiveData<Event<Int>> = MutableLiveData(when (humanType) {
        HumanType.HERO -> Event(R.string.trainees)
        HumanType.CHAMPION -> Event(R.string.trainers)
    })


    private val _openHeroEvent = MutableLiveData<Event<HeroIdWithHumanType>>()
    val openHeroEvent: LiveData<Event<HeroIdWithHumanType>> = _openHeroEvent
    fun openHero() = openHero(Constants.OBJECT_ID_NA)//OBJECT_ID_NA for adding new item
    fun openHero(id: Long?) {
        if (id != null) _openHeroEvent.value = Event(HeroIdWithHumanType(id, humanType))
    }


    private fun processHeroesFromDB(result: Result<List<Hero>?>): LiveData<List<Hero>> =
        if (result is Success && result.data != null) {
            Logger.d(LOG_TAG, "got new heroes=${result.data}")
            MutableLiveData(result.data)
        } else {
            MutableLiveData(null)
        }


    companion object {
        private const val LOG_TAG = "HeroesListViewModel"
    }
}