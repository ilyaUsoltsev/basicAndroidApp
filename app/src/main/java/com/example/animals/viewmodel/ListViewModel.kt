package com.example.animals.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.animals.di.AppModule
import com.example.animals.di.CONTEXT_APP
import com.example.animals.di.DaggerViewModelComponent
import com.example.animals.di.PrefsModule
import com.example.animals.model.Animal
import com.example.animals.model.AnimalApiService
import com.example.animals.model.ApiKey
import com.example.animals.utils.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel(application: Application): AndroidViewModel(application)  {


    val animals by lazy { MutableLiveData<List<Animal>>() }
    val loadError by lazy {MutableLiveData<Boolean>()}
    val loading by lazy {MutableLiveData<Boolean>()}

    private val disposable = CompositeDisposable();

    @Inject
    lateinit var apiService: AnimalApiService;

    init {
        DaggerViewModelComponent.builder()
            .appModule(AppModule(getApplication()))
            .build()
            .inject(this)
    }

    @Inject
    @field:PrefsModule.TypeOfContext(CONTEXT_APP)
    lateinit var prefs: SharedPreferencesHelper;
    private var invalidApiKey = false;

    fun refresh() {
        loading.value = true;
        invalidApiKey = false;
        val key = prefs.getApiKey();
        if(key.isNullOrEmpty()){
            getKey()
        }else {
            getAnimals(key)
        }
    }

    fun hardRefresh() {
        loading.value = true;
        getKey();
    }


    private fun getKey() {
        disposable.add(
            apiService.getApiKey()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<ApiKey>() {
                    override fun onSuccess(key: ApiKey) {
                        print("key is $key")
                        if(key.key.isNullOrEmpty()) {
                            loadError.value = true;
                            loading.value = false;
                        } else {
                            prefs.saveApiKey(key.key)
                            getAnimals(key.key)
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loadError.value = true;
                        loading.value = false;
                    }

                })
        )
    }

    private fun getAnimals(key: String) {
        disposable.add(
            apiService.getAnimals(key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<Animal>>() {
                    override fun onSuccess(list: List<Animal>) {
                        loadError.value = false;
                        animals.value = list;
                        loading.value = false;
                    }

                    override fun onError(e: Throwable) {
                        if(!invalidApiKey) {
                            invalidApiKey = true
                            getKey();
                        }
                        e.printStackTrace()
                        loadError.value = true;
                        loading.value = false;
                        animals.value = null;
                    }

                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}