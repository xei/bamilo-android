/*
 * Copyright 2016 Scarab Research Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict;

/**
 * Holds error information for the query.
 */
class ErrorParameter {

    final String type;
    final String command;
    final String message;

    ErrorParameter(String type, String command, String message) {
        this.type = type;
        this.command = command;
        this.message = message;
    }

}