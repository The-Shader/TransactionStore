package com.fireblade.transactionstore

import arrow.core.Either
import com.fireblade.transactionstore.home.domain.*
import org.junit.Test

class TransactionStoreTest {
    private val service: TransactionStoreService = TransactionStoreImpl()

    @Test
    fun `simple set`() {
        val key = "foo"
        val value = "123"
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key,
                value = value
            )
        )
        val result = service.processPropertyRequest(
            request = PropertyRequest.Get(
                name = key
            )
        )
        assert(
            result is Either.Right &&
                    result.value is PropertyResponse.Result &&
                    (result.value as PropertyResponse.Result).value == value
        )
    }

    @Test
    fun `get fails after delete`() {
        val key = "foo"
        val value = "123"
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key,
                value = value
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Delete(
                name = key
            )
        )
        val result = service.processPropertyRequest(
            request = PropertyRequest.Get(
                name = key
            )
        )
        assert(result is Either.Left && result.value == PropertyError.GET)
    }

    @Test
    fun `count successfully`() {
        val key1 = "foo"
        val value1 = "123"
        val key2 = "bar"
        val value2 = "456"
        val key3 = "baz"
        val expectedCount = 2
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value1
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key2,
                value = value2
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key3,
                value = value1
            )
        )
        val result = service.processPropertyRequest(
            request = PropertyRequest.Count(
                value = value1
            )
        )
        assert(
            result is Either.Right &&
                    result.value is PropertyResponse.Result &&
                    (result.value as PropertyResponse.Result).value.toInt() == expectedCount
        )
    }

    @Test
    fun `cannot rollback after commit successfully`() {
        val key1 = "bar"
        val value1 = "123"
        val key2 = "foo"
        val value2 = "456"
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value1
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.BEGIN
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key2,
                value = value2
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Delete(
                name = key1
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.COMMIT
        )
        val result = service.processTransactionRequest(
            request = TransactionRequest.ROLLBACK
        )
        assert(result is Either.Left && result.value == TransactionError.ROLLBACK)
    }

    @Test
    fun `cannot commit after rollback successfully`() {
        val key1 = "foo"
        val value1 = "123"
        val key2 = "bar"
        val value2 = "abc"
        val value3 = "456"
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value1
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key2,
                value = value2
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.BEGIN
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value3
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.ROLLBACK
        )
        val result = service.processTransactionRequest(
            request = TransactionRequest.COMMIT
        )
        assert(result is Either.Left && result.value == TransactionError.COMMIT)
    }

    @Test
    fun `nesting transactions successfully`() {
        val key1 = "foo"
        val value1 = "123"
        val key2 = "bar"
        val value2 = "456"
        val value3 = "789"
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value1
            )
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key2,
                value = value2
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.BEGIN
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value2
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.BEGIN
        )
        service.processPropertyRequest(
            request = PropertyRequest.Set(
                name = key1,
                value = value3
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.ROLLBACK
        )
        service.processPropertyRequest(
            request = PropertyRequest.Delete(
                name = key1
            )
        )
        service.processTransactionRequest(
            request = TransactionRequest.ROLLBACK
        )
        val result = service.processPropertyRequest(
            request = PropertyRequest.Get(
                name = key1
            )
        )
        assert(
            result is Either.Right &&
                    result.value is PropertyResponse.Result &&
                    (result.value as PropertyResponse.Result).value == value1
        )
    }
}