package ac.smu.embedded.mapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <I, O> LiveData<I>.map(mapFunction: (I) -> O): LiveData<O> =
    Transformations.map(this, mapFunction)

fun <I, O> LiveData<I>.switchMap(switchMapFunction: (I) -> LiveData<O>): LiveData<O> =
    Transformations.switchMap(this, switchMapFunction)