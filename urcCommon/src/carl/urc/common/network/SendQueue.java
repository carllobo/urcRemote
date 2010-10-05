/*
 *    This file is part of urcRemote. urcRemote turns your mobile phone into
 *    a touch-pad and keyboard for your computer.
 *
 *    Copyright  2010 Carl Lobo <carllobo@gmail.com>
 *
 *    urcRemote is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    urcRemote is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with urcRemote.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package carl.urc.common.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import carl.urc.common.CommonPreferences;

class FlowControlledQueue {
	
	private Vector queue = new Vector(10);
	private Vector buffers = new Vector();

	synchronized void push(UrcNetworkMethod method) {
		if((method instanceof BufferableMethod) 
				&& (queue.size() >= CommonPreferences.preferenceBufferStart)) {
			BufferableMethod m = (BufferableMethod) method;
			m = findBuffer(m);
			m.buffer(method);
		} else {
			flushBuffers();
			queue.addElement(method);
		}
	}

	synchronized UrcNetworkMethod pop() {
		UrcNetworkMethod m = (UrcNetworkMethod) queue.elementAt(0);
		queue.removeElementAt(0);
		return m;
	}

	synchronized void flushBuffers(DataOutputStream output) throws IOException {
		for(int i = 0; i < buffers.size(); ++i) {
			UrcNetworkMethod m = (UrcNetworkMethod) buffers.elementAt(i);
			m.write(output);
		}
		buffers.removeAllElements();
	}
	
	private void flushBuffers() {
		for(int i = 0; i < buffers.size(); ++i) {
			queue.addElement(buffers.elementAt(i));
		}
		buffers.removeAllElements();
		queue.trimToSize();
	}
	
	private BufferableMethod findBuffer(BufferableMethod method) {
		Class methodClass = method.getClass();
		for(int i = 0; i < buffers.size(); ++i) {
			if(methodClass.equals(buffers.elementAt(i).getClass()))
				return (BufferableMethod) buffers.elementAt(i);
		}
		buffers.addElement(method);
		return method;
	}

	synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	synchronized void clear() {
		queue.removeAllElements();
		queue.trimToSize();
	}
	
}

class SendQueue extends Thread {

	private DataOutputStream output;
	private FlowControlledQueue queue;
	private volatile boolean run = true;
	private Middleman middleman;

	SendQueue(Middleman middleman, DataOutputStream output) {
		this.middleman = middleman;
		this.output = output;
		queue = new FlowControlledQueue();
		start();
	}

	synchronized void push(UrcNetworkMethod method) {
		queue.push(method);
		notify();
	}

	void clear() {
		queue.clear();
	}

	public void run() {
		try {
			while (run) {
				if (queue.isEmpty()) {
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				while (!queue.isEmpty()) {
					UrcNetworkMethod method = queue.pop();
					method.write(output);
				}
				queue.flushBuffers(output);
				output.flush();
			}
		} catch (Exception e) {
			middleman.showError("Send Error", e);
		} finally {
			middleman.close();
		}
	}

	public void interrupt() {
		clear();
		run = false;
		try {
			((OutputStream) output).close();
		} catch (IOException e) {
			middleman.showError("Output Close Error", e);
		}
		super.interrupt();
	}
}
