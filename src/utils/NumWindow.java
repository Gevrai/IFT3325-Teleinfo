package utils;

import frames.Frame;

/** Basic implementation of a window on a circular buffer, to be used for
 *  Go-back-N and SelectiveReject algorithms.
 *
 */
public class NumWindow {
	
	private byte currentFirstAck;
	private byte currentToSend;
	private int windowSize;
	private Frame[] iframes;
	private long[] timestamps;
	
	public NumWindow(int maxNum, int windowSize) {
		this.currentFirstAck = 0;
		this.currentToSend = 0;
		this.windowSize = windowSize % maxNum;
		this.iframes = new Frame[maxNum];
		this.timestamps = new long[maxNum];
	}
	
	public byte getCurrentFirstAck() { return (byte) currentFirstAck; }
	
	public void ackNext() {
		this.iframes[currentFirstAck] = null;
		this.timestamps[currentFirstAck] = 0;
		this.currentFirstAck = (byte) ((currentFirstAck+1) % iframes.length);
	}

	public void ackUpTo(byte num) {
		while (num != currentFirstAck)
			ackNext();
	}
	
	public boolean isInsideWindow(byte num) {
		// Return true if inside window
		if (currentFirstAck >= num)
			return (num < currentFirstAck + windowSize);
		else 
			return num < (currentFirstAck + windowSize) % iframes.length;
	}
	
	public boolean hasNextToSend() {
		return iframes[currentToSend] != null && isInsideWindow(currentToSend);
	}
	
	// Pop next frame to send and time stamp it
	public Frame getNextToSendAndStamp() {
		if (!hasNextToSend())
			return null;
		Frame iframe = this.iframes[currentToSend];
		this.timestamps[currentToSend] = System.currentTimeMillis();
		currentToSend = (byte) ((currentToSend+1) % iframes.length);
		return iframe;
	}
	
	// There is place if at least the last spot in window is empty
	public boolean canPut(byte frameCount) {
		return this.iframes[frameCount] == null && isInsideWindow(frameCount);
	}

	// Put the frame to be sent in window
	public boolean put(Frame f) {
		if (!canPut(f.getNum()))
			return false;
		// Add to window
		this.iframes[f.getNum()] = f;
		return true;
	}

	public void goBackTo(byte toNum) {
		// Reset timestamps
		this.timestamps = new long[this.timestamps.length];
		this.currentToSend = this.currentFirstAck;
		ackUpTo(toNum);
	}

	public boolean hasFirstExpired(long timeoutTime) {
		return iframes[currentFirstAck] != null && this.timestamps[currentFirstAck] + timeoutTime < System.currentTimeMillis();
	}

	public boolean isEmpty() {
		for (Frame f : iframes)
			if (f != null)
				return false;
		return true;
	}


}
