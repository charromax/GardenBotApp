/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.apollographql.apollo.exception.ApolloException
import com.example.gardenbotapp.data.domain.ChartRepository
import com.example.gardenbotapp.data.local.PreferencesManager
import com.example.gardenbotapp.data.model.Measure
import com.example.gardenbotapp.di.ApplicationDefaultScope
import com.example.gardenbotapp.di.ApplicationIoScope
import com.example.gardenbotapp.ui.base.GardenBotBaseViewModel
import com.example.gardenbotapp.util.Errors
import com.example.gardenbotapp.util.MAX_ALLOWED_TEMPERATURE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ChartViewModel @Inject constructor(
    @ApplicationDefaultScope private val defScope: CoroutineScope,
    @ApplicationIoScope private val ioScope: CoroutineScope,
    private val chartRepository: ChartRepository,
    private val preferencesManager: PreferencesManager
) : GardenBotBaseViewModel() {

    private val _measures = MutableLiveData<List<Measure>>()
    val measures: LiveData<List<Measure>> get() = _measures
    private val chartEventsChannel = Channel<Errors>()
    val chartEvents = chartEventsChannel.receiveAsFlow()
    private val _measureSub = MutableLiveData<Measure>()
    val measureSub: LiveData<Measure> get() = _measureSub
    private val _chartScreenShot = MutableLiveData<Uri>()
    val chartScreenShot: LiveData<Uri> get() = _chartScreenShot
    private val IMAGES_DIR = "images"
    private val FILENAME = "chart.png"


    init {
        subscribeToMeasures()
    }

    override fun onCleared() {
        super.onCleared()
        defScope.cancel()
        ioScope.cancel()
    }

    /**
     * manually update current measure to trigger live views
     */
    fun manualUpdateNewMeasure(newMeasure: Measure) {
        _measureSub.value = newMeasure
    }

    private fun subscribeToMeasures() {
        viewModelScope.launch {
            try {
                val deviceId = preferencesManager.deviceIdFlow.first()
                chartRepository.newMeasureSub(deviceId)
                    .retryWhen { _, attempt ->
                        delay((attempt * 1000))    //exp delay
                        true
                    }
                    .collect { res ->
                        if (res.hasErrors() || res.data?.newMeasure == null) {
                            chartEventsChannel.send(Errors.SubError("ERROR: ${res.errors?.map { it.message }}"))
                        } else {
                            _measureSub.value = Measure.fromResponse(res)
                        }
                    }
            } catch (e: ApolloException) {
                chartEventsChannel.send(Errors.SubError("ERROR: ${e.message}"))
            }
        }
    }


    fun refreshChartData(measure: Measure) {
        defScope.launch {
            val listSoFar = arrayListOf<Measure>()
            _measures.value?.let { listSoFar.addAll(it) }
            listSoFar.add(measure)
            _measures.postValue(listSoFar)
        }

    }

    fun populateChartData() {
        viewModelScope.launch {
            try {
                val currentToken = preferencesManager.tokenFlow.first()
                val currentDevice = preferencesManager.deviceIdFlow.first()
                chartRepository.getMeasuresForDevice(currentDevice, currentToken)
                    .collect {
                        _measures.value = it
                    }
            } catch (e: Exception) {
                e.message?.let { message ->
                    chartEventsChannel.send(Errors.HomeError(message))
                }
            }
        }
    }

    val liveAirHumData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData { emit(it.airHum.toFloat()) }
        }


    val liveAirTempData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData(defScope.coroutineContext) {
                val temperaturePercent = (it.airTemp * 100) / MAX_ALLOWED_TEMPERATURE
                emit(temperaturePercent.toFloat())
            }
        }

    val liveSoilHumData: LiveData<Float> =
        Transformations.switchMap(_measureSub) {
            liveData { emit(it.soilHum.toFloat()) }
        }

    fun takeAndSaveScreenShot(fragment: Fragment) {
        val bitmap = getBitmapFromView(fragment.view)
        if (bitmap != null) {
            saveScreenShot(fragment.requireContext(), bitmap)
        }
    }

    private fun getBitmapFromView(view: View?): Bitmap? {
        view?.let {
            val bitmap = Bitmap.createBitmap(
                view.measuredWidth, view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.layout(view.left, view.top, view.right, view.bottom)
            view.draw(canvas)
            return bitmap
        }
        return null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun saveScreenShot(context: Context, bitmap: Bitmap) {
        // save bitmap to cache directory
        viewModelScope.launch {
            try {
                withContext(ioScope.coroutineContext) {
                    val cachePath = File(context.cacheDir, IMAGES_DIR)
                    cachePath.mkdirs() // don't forget to make the directory
                    val stream =
                        FileOutputStream("$cachePath/chart.png") // overwrites this image every time
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val imagePath = File(context.cacheDir, IMAGES_DIR)
            val newFile = File(imagePath, FILENAME)
            val contentUri: Uri =
                FileProvider.getUriForFile(
                    context,
                    "com.example.gardenbotapp.fileprovider",
                    newFile
                )
            _chartScreenShot.postValue(contentUri)
        }
    }
}
