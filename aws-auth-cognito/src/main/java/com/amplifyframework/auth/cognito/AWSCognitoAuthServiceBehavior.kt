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

package com.amplifyframework.auth.cognito

import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import com.amplifyframework.statemachine.codegen.data.AuthConfiguration

interface AWSCognitoAuthServiceBehavior {
    var cognitoIdentityProviderClient: CognitoIdentityProviderClient?
    var cognitoIdentityClient: CognitoIdentityClient?

    companion object {
        fun fromConfiguration(configuration: AuthConfiguration): AWSCognitoAuthServiceBehavior {
            val cognitoIdentityProviderClient = configuration.userPool?.let { it ->
                CognitoIdentityProviderClient { this.region = it.region }
            }

            val cognitoIdentityClient = configuration.identityPool?.let { it ->
                CognitoIdentityClient { this.region = it.region }
            }

            return object : AWSCognitoAuthServiceBehavior {
                override var cognitoIdentityProviderClient = cognitoIdentityProviderClient
                override var cognitoIdentityClient = cognitoIdentityClient
            }
        }
    }
}