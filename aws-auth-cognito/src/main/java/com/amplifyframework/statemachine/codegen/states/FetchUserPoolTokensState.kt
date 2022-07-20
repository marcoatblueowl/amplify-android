/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.statemachine.codegen.states

import com.amplifyframework.statemachine.State
import com.amplifyframework.statemachine.StateMachineEvent
import com.amplifyframework.statemachine.StateMachineResolver
import com.amplifyframework.statemachine.StateResolution
import com.amplifyframework.statemachine.codegen.actions.FetchUserPoolTokensActions
import com.amplifyframework.statemachine.codegen.events.FetchUserPoolTokensEvent
import java.lang.Exception

sealed class FetchUserPoolTokensState : State {
    data class Configuring(val id: String = "") : FetchUserPoolTokensState()
    data class Refreshing(val id: String = "") : FetchUserPoolTokensState()
    data class Fetched(val id: String = "") : FetchUserPoolTokensState()
    data class Error(val exception: Exception) : FetchUserPoolTokensState()

    class Resolver(private val fetchUserPoolTokensActions: FetchUserPoolTokensActions) :
        StateMachineResolver<FetchUserPoolTokensState> {
        override val defaultState = Configuring()
        private fun asFetchUserPoolTokensEvent(event: StateMachineEvent): FetchUserPoolTokensEvent.EventType? {
            return (event as? FetchUserPoolTokensEvent)?.eventType
        }

        override fun resolve(
            oldState: FetchUserPoolTokensState,
            event: StateMachineEvent
        ): StateResolution<FetchUserPoolTokensState> {
            val fetchUserPoolTokensEvent = asFetchUserPoolTokensEvent(event)
            val defaultResolution = StateResolution(oldState)
            return when (oldState) {
                is Configuring -> {
                    when (fetchUserPoolTokensEvent) {
                        is FetchUserPoolTokensEvent.EventType.Fetched -> StateResolution(Fetched())
                        is FetchUserPoolTokensEvent.EventType.Refresh -> {
                            val action =
                                fetchUserPoolTokensActions.refreshFetchUserPoolTokensAction(
                                    fetchUserPoolTokensEvent.amplifyCredential
                                )
                            StateResolution(Refreshing(), listOf(action))
                        }
                        else -> defaultResolution
                    }
                }
                is Refreshing -> {
                    when (fetchUserPoolTokensEvent) {
                        is FetchUserPoolTokensEvent.EventType.Fetched -> StateResolution(Fetched())
                        is FetchUserPoolTokensEvent.EventType.ThrowError -> StateResolution(
                            Error(fetchUserPoolTokensEvent.exception)
                        )
                        else -> defaultResolution
                    }
                }
                else -> defaultResolution
            }
        }
    }
}