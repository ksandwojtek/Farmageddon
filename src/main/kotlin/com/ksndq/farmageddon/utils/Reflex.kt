package com.ksndq.farmageddon.utils

import java.lang.reflect.Field

object Reflex {
    fun getField(source: Class<*>, name: String): Field? {
        return try {
            source.getDeclaredField(name)
        } catch (exception: NoSuchFieldException) {
            val superClass = source.superclass
            superClass?.let { getField(it, name) }
        }
    }

    fun getFieldValue(source: Any, name: String): Any? {
        return try {
            val clazz = if (source is Class<*>) source else source::class.java
            val field = getField(clazz, name) ?: return null

            field.isAccessible = true
            field[source]
        } catch (exception: IllegalAccessException) {
            exception.printStackTrace()
            null
        }
    }

    fun setFieldValue(source: Any, name: String, value: Any?): Boolean {
        return try {
            val isStatic = source is Class<*>
            val clazz = if (isStatic) source as Class<*> else source::class.java

            val field = getField(clazz, name) ?: return false

            field.isAccessible = true
            field[if (isStatic) null else source] = value
            true
        } catch (exception: IllegalAccessException) {
            exception.printStackTrace()
            false
        }
    }
}