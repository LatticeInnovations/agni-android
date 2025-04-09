package com.latticeonfhir.android.utils.builders

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.core.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.core.data.local.model.patch.ChangeRequest
import com.latticeonfhir.core.utils.converters.responseconverter.GsonConverters.mapToObject

object GenericEntityPatchBuilder {

    fun processPatch(
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>,
        entryValues: List<ChangeRequest>
    ): List<ChangeRequest> {
        val existingList = mutableListOf<ChangeRequest>()
        var alreadyExist: ChangeRequest?
        if (existingMap[mapEntry.key] != null) {
            (existingMap[mapEntry.key] as List<*>).map {
                (it as LinkedTreeMap<*, *>).mapToObject(ChangeRequest::class.java)
                    ?.let { it1 -> existingList.add(it1) }
            }
            entryValues.forEach { entryValue ->
                alreadyExist = existingList.find { it.key == entryValue.key }
                when (entryValue.operation) {
                    ChangeTypeEnum.ADD.value -> {
                        addActionList(alreadyExist, existingList, entryValue, mapEntry)
                    }

                    ChangeTypeEnum.REPLACE.value -> {
                        replaceActionList(alreadyExist, existingList, entryValue)
                    }

                    ChangeTypeEnum.REMOVE.value -> {
                        removeActionList(alreadyExist, existingList, entryValue)
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


    internal fun processPatch(
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>
    ) {
        if (existingMap[mapEntry.key] != null) {
            (existingMap[mapEntry.key] as LinkedTreeMap<*, *>).mapToObject(ChangeRequest::class.java)
                ?.let { alreadyExistChangeRequest ->
                    when ((mapEntry.value as ChangeRequest).operation) {
                        ChangeTypeEnum.ADD.value -> {
                            addAction(alreadyExistChangeRequest, existingMap, mapEntry)
                        }

                        ChangeTypeEnum.REPLACE.value -> {
                            replaceAction(alreadyExistChangeRequest, existingMap, mapEntry)
                        }

                        ChangeTypeEnum.REMOVE.value -> {
                            removeAction(alreadyExistChangeRequest, existingMap, mapEntry)
                        }
                    }
                }
        } else {
            existingMap[mapEntry.key] = mapEntry.value
        }
    }

    /** For list type keys in map */

    /** Add action list*/
    private fun addActionList(
        alreadyExist: ChangeRequest?,
        existingList: MutableList<ChangeRequest>,
        entryValue: ChangeRequest,
        mapEntry: Map.Entry<String, Any>
    ) {
        existingList.apply {
            if (alreadyExist?.operation == ChangeTypeEnum.REMOVE.value && alreadyExist.value == mapEntry.value) {
                remove(alreadyExist)
            } else if (alreadyExist?.operation == ChangeTypeEnum.REMOVE.value && alreadyExist.value != mapEntry.value) {
                remove(alreadyExist)
                add(entryValue.copy(operation = ChangeTypeEnum.REPLACE.value))
            } else {
                remove(alreadyExist)
                add(entryValue)
            }
        }
    }

    /** Replace action list */
    private fun replaceActionList(
        alreadyExist: ChangeRequest?,
        existingList: MutableList<ChangeRequest>,
        entryValue: ChangeRequest
    ) {
        if (alreadyExist?.operation != ChangeTypeEnum.ADD.value) {
            existingList.apply {
                remove(alreadyExist)
                add(entryValue)
            }
        } else {
            existingList.apply {
                remove(alreadyExist)
                add(alreadyExist.copy(value = entryValue.value))
            }
        }
    }

    /** Remove action list */
    private fun removeActionList(
        alreadyExist: ChangeRequest?,
        existingList: MutableList<ChangeRequest>,
        entryValue: ChangeRequest
    ) {
        when (alreadyExist?.operation) {
            ChangeTypeEnum.ADD.value -> {
                existingList.apply {
                    remove(alreadyExist)
                }
            }

            ChangeTypeEnum.REPLACE.value -> {
                existingList.apply {
                    remove(alreadyExist)
                    add(
                        alreadyExist.copy(
                            value = entryValue.value,
                            operation = entryValue.operation
                        )
                    )
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

    /** Non list type keys in patch */

    /** Add Action */
    private fun addAction(
        alreadyExistChangeRequest: ChangeRequest,
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>
    ) {
        if (alreadyExistChangeRequest.operation == ChangeTypeEnum.REMOVE.value && alreadyExistChangeRequest.value == mapEntry.value) {
            existingMap.remove(mapEntry.key)
        } else if (alreadyExistChangeRequest.operation == ChangeTypeEnum.REMOVE.value && alreadyExistChangeRequest.value != mapEntry.value) {
            existingMap[mapEntry.key] = alreadyExistChangeRequest.copy(
                value = (mapEntry.value as ChangeRequest).value,
                operation = ChangeTypeEnum.REPLACE.value
            )
        } else {
            existingMap[mapEntry.key] = mapEntry.value
        }
    }

    /** Replace Action */
    private fun replaceAction(
        alreadyExistChangeRequest: ChangeRequest,
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>
    ) {
        if (alreadyExistChangeRequest.operation != ChangeTypeEnum.ADD.value) {
            existingMap[mapEntry.key] = mapEntry.value
        } else {
            existingMap[mapEntry.key] =
                alreadyExistChangeRequest.copy(value = (mapEntry.value as ChangeRequest).value)
        }
    }

    /** Remove Action */
    private fun removeAction(
        alreadyExistChangeRequest: ChangeRequest,
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>
    ) {
        when (alreadyExistChangeRequest.operation) {
            ChangeTypeEnum.ADD.value -> {
                existingMap.remove(mapEntry.key)
            }

            ChangeTypeEnum.REPLACE.value -> {
                existingMap[mapEntry.key] = alreadyExistChangeRequest.copy(
                    value = (mapEntry.value as ChangeRequest).value,
                    operation = (mapEntry.value as ChangeRequest).operation
                )
            }

            else -> {
                existingMap[mapEntry.key] = mapEntry.value
            }
        }
    }
}