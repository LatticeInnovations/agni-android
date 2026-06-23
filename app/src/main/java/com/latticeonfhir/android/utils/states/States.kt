package com.latticeonfhir.android.utils.states

import android.content.Context
import com.google.gson.Gson
import com.latticeonfhir.android.data.local.model.states.States
import timber.log.Timber
import java.io.InputStreamReader

private var cachedStates: States? = null

private fun getStatesData(context: Context): States {
    return cachedStates ?: context.assets
        .open("india_states_districts_blocks_pincodes.json")
        .use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                Gson().fromJson(reader, States::class.java)
            }
        }.also {
            cachedStates = it
        }
}

fun getStateNames(context: Context): List<String> {
    return try {
        getStatesData(context)
            .states
            .map { it.stateName }
            .sorted()
    } catch (e: Exception) {
        Timber.e(e)
        emptyList()
    }
}

fun getStateCode(
    context: Context,
    stateName: String
): String {
    return try {
        getStatesData(context)
            .states
            .firstOrNull {
                it.stateName == stateName
            }
            ?.stateCode.orEmpty()
    } catch (e: Exception) {
        Timber.e(e)
        ""
    }
}

fun getDistrictNames(
    context: Context,
    stateName: String
): List<String> {
    return try {
        val states = getStatesData(context).states

        if (stateName.isBlank()) {
            states
                .flatMap { state -> state.districts }
                .map { district -> district.districtName }
                .distinct()
                .sorted()
        } else {
            states
                .firstOrNull {
                    it.stateName.equals(stateName, ignoreCase = true)
                }
                ?.districts
                ?.map { district -> district.districtName }
                ?.sorted()
                ?: emptyList()
        }
    } catch (e: Exception) {
        Timber.e(e)
        emptyList()
    }
}

fun getDistrictCode(
    context: Context,
    stateName: String,
    districtName: String
): String {
    return try {
        getStatesData(context)
            .states
            .asSequence()
            .filter {
                stateName.isBlank() ||
                        it.stateName == stateName
            }
            .flatMap { state ->
                state.districts.asSequence()
            }
            .firstOrNull {
                it.districtName == districtName
            }
            ?.districtCode
            .orEmpty()
    } catch (e: Exception) {
        Timber.e(e)
        ""
    }
}

fun getBlockNames(
    context: Context,
    stateName: String,
    districtName: String
): List<String> {
    return try {
        getStatesData(context)
            .states
            .asSequence()
            .filter {
                stateName.isBlank() ||
                        it.stateName.equals(stateName, ignoreCase = true)
            }
            .flatMap { state ->
                state.districts.asSequence()
            }
            .filter {
                districtName.isBlank() ||
                        it.districtName.equals(districtName, ignoreCase = true)
            }
            .flatMap { district ->
                district.blocks.asSequence()
            }
            .map { block ->
                block.blockName
            }
            .distinct()
            .sorted()
            .toList()
    } catch (e: Exception) {
        Timber.e(e)
        emptyList()
    }
}

fun getBlockCode(
    context: Context,
    stateName: String,
    districtName: String,
    blockName: String
): String {
    return try {
        getStatesData(context)
            .states
            .asSequence()
            .filter {
                stateName.isBlank() ||
                        it.stateName == stateName
            }
            .flatMap { state ->
                state.districts.asSequence()
            }
            .filter {
                districtName.isBlank() ||
                        it.districtName == districtName
            }
            .flatMap { district ->
                district.blocks.asSequence()
            }
            .firstOrNull {
                it.blockName == blockName
            }
            ?.blockCode
            .orEmpty()
    } catch (e: Exception) {
        Timber.e(e)
        ""
    }
}

fun getStateAndDistrictByPincode(
    context: Context,
    pincode: String
): Pair<String, String>? {
    return try {
        getStatesData(context)
            .states
            .asSequence()
            .flatMap { state ->
                state.districts.asSequence().map { district ->
                    Triple(
                        state.stateName,
                        district.districtName,
                        district.pincodes
                    )
                }
            }
            .firstOrNull { (_, _, pincodes) ->
                pincode in pincodes
            }
            ?.let { (stateName, districtName, _) ->
                stateName to districtName
            }
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}