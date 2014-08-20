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

import com.emsg.client.beans.IPacket;

public interface PacketListener<T> {
	
	/**
	 * 全部请求
	 * @param packet
	 */
	public void processPacket(IPacket<T> packet);

	/**
	 * 文本信息交换
	 * @param packet
	 */
	public void textPacket(IPacket<T> packet);

	/**
	 * 类型 type=5 和 type=6 的音频视频拨号请求
	 * @param packet
	 */
	public void mediaPacket(IPacket<T> packet);

}
