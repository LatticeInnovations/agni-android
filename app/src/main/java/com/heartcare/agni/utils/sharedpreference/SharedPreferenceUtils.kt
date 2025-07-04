package com.heartcare.agni.utils.sharedpreference

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}


class LongPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit { putLong(name, value) }
    }
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.getString(name, defaultValue) ?: ""
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.edit { putString(name, value) }
    }
}

class SetPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: HashSet<String>? = null
) : ReadWriteProperty<Any, HashSet<String>?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): HashSet<String>? {
        return preferences.getStringSet(name, defaultValue)?.toHashSet()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: HashSet<String>?) {
        preferences.edit {
            putStringSet(name, value)
        }
    }
}