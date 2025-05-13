package com.example.companio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.companio.features.FaceDetectorProvider
import com.example.companio.features.face_recognition.EmbeddingStore
import com.example.companio.features.face_recognition.FaceNetModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    val faceNetModel: FaceNetModel by lazy {
        FaceNetModel(application)
    }

    init {
        // Initialize FaceNetModel in the background
        viewModelScope.launch(Dispatchers.IO) {
            faceNetModel
            EmbeddingStore.initialize(application, faceNetModel)
            FaceDetectorProvider.faceDetector
        }
    }
}