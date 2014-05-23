/**
 * $RCSfile$
 * $Revision: 11613 $
 * $Date: 2010-02-09 03:55:56 -0800 (Tue, 09 Feb 2010) $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emsg.client;


/**
 * Provides a mechanism to listen for packets that pass a specified filter.
 * This allows event-style programming -- every time a new packet is found,
 * the {@link #processPacket(Packet)} method will be called. This is the
 * opposite approach to the functionality provided by a {@link PacketCollector}
 * which lets you block while waiting for results.
 *
 * @see Connection#addPacketListener(PacketListener, org.jivesoftware.smack.filter.PacketFilter)
 * @author Matt Tucker
 */
public interface PacketListener {

    public void processPacket(String packet);
    
}
