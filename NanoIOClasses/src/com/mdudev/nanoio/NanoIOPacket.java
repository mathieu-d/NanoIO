package com.mdudev.nanoio;

import java.io.Serializable;

/**
 *	This file is part of NanoIO.
 *
 *	NanoIO is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	NanoIO is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with NanoIO.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Copyright 2016 Mathieu Dupriez
 */

/**
 * 
 * Generic NanoIO packet class
 * All packets send to a NanoIO client or server must extend this class
 *
 * @param <DataType>
 * NanoIO packet data type
 * 
 * @author Mathieu Dupriez
 */
public class NanoIOPacket<DataType extends Serializable> implements Serializable{

	private static final long serialVersionUID = 2166531973037957513L;

	private DataType data;

	/**
	 * Access to packet data
	 * @return
	 * NanoIO packet data
	 */
	public DataType getData() {
		return data;
	}

	/**
	 * Access to packet data
	 * @param data
	 * NanoIO packet data
	 */
	public void setData(DataType data) {
		this.data = data;
	}
}
