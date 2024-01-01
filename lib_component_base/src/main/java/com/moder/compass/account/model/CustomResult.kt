package com.moder.compass.account.model

import android.os.Bundle
import com.moder.compass.util.receiver.ErrorType
import java.io.Serializable

/**
 * 结果
 */
class CustomResult<out T>(val value: Any?) : Serializable {

    /**
     * Returns `true` if this instance represents successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value !is CustomFailure

    /**
     * Returns `true` if this instance represents failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is CustomFailure


    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? =
            when {
                isFailure -> null
                else -> value as? T
            }

    /**
     * getFailureOrNull
     * */
    fun getFailureOrNull(): CustomFailure? = when {
        isSuccess -> null
        else -> value as? CustomFailure
    }

    /**
     * Returns a string `Success(v)` if this instance represents [success][Result.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    override fun toString(): String =
            when (value) {
                is CustomFailure -> value.toString() // "Failure($exception)"
                else -> "Success($value)"
            }

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T> success(value: T): CustomResult<T> = CustomResult(value)

        /**
         * Returns an instance that encapsulates the given [errorType] as failure.
         */
        fun <T> failure(errorType: ErrorType?, errorCode: Int, opCode: Int = -1, data: Bundle? = null): CustomResult<T> = CustomResult(
            createFailure(errorType, errorCode, opCode, data)
        )
    }

    /**
     * 自定义失败
     */
    class CustomFailure(
        @JvmField
            val errorType: ErrorType?,
        @JvmField
            val errorCode: Int,
        @JvmField
            val opCode: Int = -1,
        @JvmField
            val data: Bundle?
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is CustomFailure && errorType == other.errorType
                && errorCode == other.errorCode && opCode == other.opCode

        override fun hashCode(): Int = errorType?.hashCode() ?: 0 + errorCode + opCode
        override fun toString(): String = "Failure(${errorType?.name})"
    }
}

/**
 * 网络错误
 * */
fun CustomResult<Any?>?.isNetworkError(): Boolean {
    return this?.getFailureOrNull()?.errorType == ErrorType.NETWORK_ERROR
}

/**
 * 错误码
 * */
fun CustomResult<Any?>?.errorCode(): Int {
    return this?.getFailureOrNull()?.errorCode ?: 0
}

/**
 * 错误码匹配
 * */
fun CustomResult<Any?>?.matchErrorCodes(vararg errors: Int): Boolean {
    val code = errorCode()
    return errors.count {
        it == code
    } > 0
}

/**
 * 快速创建错误对象
 * */
fun createFailure(errorType: ErrorType?, errorCode: Int, opCode: Int, data: Bundle?): CustomResult.CustomFailure =
    CustomResult.CustomFailure(errorType, errorCode, opCode, data)