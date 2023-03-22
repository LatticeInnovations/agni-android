package com.latticeonfhir.android.utils.builders

import com.google.gson.internal.LinkedTreeMap
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.ChangeRequest
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.mapToObject

object GenericEntity {

    fun processPatch(
        existingMap: MutableMap<String, Any>,
        mapEntry: Map.Entry<String, Any>,
        entryValue: ChangeRequest
    ): List<ChangeRequest> {
        val existingList = mutableListOf<ChangeRequest>()
        (existingMap[mapEntry.key] as List<*>).map {
            (it as LinkedTreeMap<String,Any>).mapToObject(ChangeRequest::class.java)?.let { it1 -> existingList.add(it1) }
        }
        val alreadyExist = existingList.find { it.key == entryValue.key }
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
                        add(alreadyExist.copy(value = entryValue.value))
                    }
                }
            }

            ChangeTypeEnum.REMOVE.value -> {
                if (alreadyExist?.operation == ChangeTypeEnum.ADD.value || alreadyExist?.operation == ChangeTypeEnum.REPLACE.value) {
                    existingList.apply {
                        remove(alreadyExist)
                    }
                } else {
                    existingList.apply {
                        remove(alreadyExist)
                        add(entryValue)
                    }
                }
            }
        }
        return existingList
    }
}