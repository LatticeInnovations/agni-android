package com.latticeonfhir.android.utils.builders

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject

object GenericEntity {

    fun processPatch(
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>,
        entryValues: List<ChangeRequest>
    ): List<ChangeRequest> {
        val existingList = mutableListOf<ChangeRequest>()
        var alreadyExist: ChangeRequest?
        if(existingMap[mapEntry.key] != null) {
            (existingMap[mapEntry.key] as List<*>).map {
                (it as LinkedTreeMap<*, *>).mapToObject(ChangeRequest::class.java)
                    ?.let { it1 -> existingList.add(it1) }
            }
            entryValues.forEach { entryValue ->
                alreadyExist = existingList.find { it.key == entryValue.key }
                when (entryValue.operation) {
                    ChangeTypeEnum.ADD.value -> {
                        existingList.apply {
                            remove(alreadyExist)
                            add(entryValue)
                        }
                    }

                    ChangeTypeEnum.REPLACE.value -> {
                        if (alreadyExist?.operation != ChangeTypeEnum.ADD.value) {
                            existingList.apply {
                                remove(alreadyExist)
                                add(entryValue)
                            }
                        } else {
                            existingList.apply {
                                remove(alreadyExist)
                                add(alreadyExist!!.copy(value = entryValue.value))
                            }
                        }
                    }

                    ChangeTypeEnum.REMOVE.value -> {
                        when (alreadyExist?.operation) {
                            ChangeTypeEnum.ADD.value -> {
                                existingList.apply {
                                    remove(alreadyExist)
                                }
                            }
                            ChangeTypeEnum.REPLACE.value -> {
                                existingList.apply {
                                    remove(alreadyExist)
                                    add(alreadyExist!!.copy(value = entryValue.value, operation = ChangeTypeEnum.REMOVE.value))
                                }
                            }
                            else -> {
                                existingList.apply {
                                    remove(alreadyExist)
                                    add(entryValue)
                                }
                            }
                        }
                    }
                }
                alreadyExist = null
            }

        } else {
            entryValues.forEach { entryValue ->
                existingList.add(entryValue)
            }
        }
        return existingList
    }

    internal fun processPatch(existingMap: MutableMap<String, Any>, mapEntry: Map.Entry<String, Any>) {
        if (existingMap[mapEntry.key] != null) {
            (existingMap[mapEntry.key] as LinkedTreeMap<*, *>).mapToObject(ChangeRequest::class.java)?.let { alreadyExistChangeRequest ->
                when ((mapEntry.value as ChangeRequest).operation) {
                    ChangeTypeEnum.ADD.value -> {
                        if(alreadyExistChangeRequest.operation == ChangeTypeEnum.REMOVE.value && alreadyExistChangeRequest.value == mapEntry.value) {
                            existingMap.remove(mapEntry.key)
                        } else if(alreadyExistChangeRequest.operation == ChangeTypeEnum.REMOVE.value && alreadyExistChangeRequest.value != mapEntry.value) {
                            existingMap[mapEntry.key] = alreadyExistChangeRequest.copy(value = (mapEntry.value as ChangeRequest).value, operation = ChangeTypeEnum.REPLACE.value)
                        } else {
                            existingMap[mapEntry.key] = mapEntry.value
                        }
                    }

                    ChangeTypeEnum.REPLACE.value -> {
                        if (alreadyExistChangeRequest.operation != ChangeTypeEnum.ADD.value) {
                            existingMap[mapEntry.key] = mapEntry.value
                        } else {
                            existingMap[mapEntry.key] = alreadyExistChangeRequest.copy(value = (mapEntry.value as ChangeRequest).value)
                        }
                    }

                    ChangeTypeEnum.REMOVE.value -> {
                        when (alreadyExistChangeRequest.operation) {
                            ChangeTypeEnum.ADD.value -> {
                                existingMap.remove(mapEntry.key)
                            }
                            ChangeTypeEnum.REPLACE.value -> {
                                existingMap[mapEntry.key] = alreadyExistChangeRequest.copy(value = (mapEntry.value as ChangeRequest).value)
                            }
                            else -> {
                                existingMap[mapEntry.key] = mapEntry.value
                            }
                        }
                    }

                    else -> {}
                }
            }
        } else {
            existingMap[mapEntry.key] = mapEntry.value
        }
    }
}