/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.runtime.adaptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

//TODO shouldn't it be LockerJavaNIO instead?
public class Locker_JavaNio implements Locker {
	private FileLock fileLock;
	private FileOutputStream fileStream;
	private File lockFile;

	public Locker_JavaNio(File lockFile) {
		this.lockFile = lockFile;
	}

	public synchronized boolean lock() throws IOException {
		fileStream = new FileOutputStream(lockFile, true);
		fileLock = fileStream.getChannel().tryLock();
		if (fileLock != null)
			return true;
		fileStream.close();
		fileStream = null;
		fileLock = null;
		return false;
	}

	public synchronized void release() {
		if (fileLock != null) {
			try {
				fileLock.release();
			} catch (IOException e) {
				//don't complain, we're making a best effort to clean up
			}
			fileLock = null;
		}
		if (fileStream != null) {
			try {
				fileStream.close();
			} catch (IOException e) {
				//don't complain, we're making a best effort to clean up
			}
			fileStream = null;
		}
	}
}