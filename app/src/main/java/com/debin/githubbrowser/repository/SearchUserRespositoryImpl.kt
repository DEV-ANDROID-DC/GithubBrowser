package com.debin.githubbrowser.repository

import com.debin.githubbrowser.datasource.GitUser
import com.debin.githubbrowser.datasource.GitUserTest
import com.debin.githubbrowser.network.ApiService
import com.debin.githubbrowser.util.StateResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.*
import java.lang.Exception


class SearchUserRespositoryImpl(
    private val apiService: ApiService,
    val dispatcher: CoroutineDispatcher) : SearchUserRepository {

    override suspend fun searchUser(userName: String): Flow<StateResponse<List<GitUser>>> {
        return flow<StateResponse<List<GitUser>>> {
            emit(StateResponse.loading())
            val users = apiService.searchUser(userName)
            emit(StateResponse.success(users))
        }.retry(2) { exception ->
            (exception is Exception).also { if (it) delay(1000)}
        }.catch { throwable ->
            emit(StateResponse.failed(throwable.message.toString()))
        }.flowOn(dispatcher)
    }
}