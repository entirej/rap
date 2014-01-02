/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application;

public interface EJRWTEntireJApp
{

    /***************************************************************************
     * Alert Message Codes
     **************************************************************************/
    public static final String YES                                   = "Yes";
    public static final String NO                                    = "No";
    public static final String ALERT_OPEN_CHANGES                    = "Open Changes";

    /***************************************************************************
     * Multilingual Message Codes
     **************************************************************************/
    public static final String MESSAGE_ASK_SAVE_CHANGES              = "Do you want to save the changes you have made?";
    public static final String MESSAGE_RECORD_LOCKED_BY_ANOTHER_USER = "This record has been locked by another user, please re-query to see changes.";
    public static final String MESSAGE_UNABLE_TO_LOCK_RECORD         = "Unable to lock record";
    public static final String MESSAGE_RECORD_LOCKED                 = "Record Locked";
    public static final String MESSAGE_NO_LOCK_STATEMENT_EXISTS      = "No lock statement exists for block:";
    public static final String MESSAGE_NO_LOCKS_WILL_BE_MADE         = "No locks will be made";
    public static final String MESSAGE_NO_BLOCK_PASSED_TO            = "No block passed to: ";
}
