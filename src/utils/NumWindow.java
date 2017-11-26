package utils;

import java.util.Arrays;

import frames.InformationFrame;
import frames.RejFrame;

/** Basic implementation of a window on a buffer, to be used for
 *  Go-back-N and SelectiveReject algorithms.
 *
 */
public class NumWindow {
	
	int currentFirst;
	int windowSize;
	InformationFrame[] iframes;
	
	public NumWindow(int maxNum, int windowSize) {
		this.currentFirst = 0;
		this.windowSize = windowSize % maxNum;
		this.iframes = new InformationFrame[maxNum];
	}
	
	public byte getCurrentNum() { return (byte) currentFirst; }
	
	public boolean hasNext() {
		return iframes[currentFirst] != null;
	}
	
	public boolean isInsideWindow(byte num) {
		// Return true if inside window
		if (currentFirst >= num)
			return (currentFirst + windowSize < num);
		else 
			return (currentFirst + windowSize) % iframes.length < num;
	}
	
	public InformationFrame popNext() {
		if (!hasNext())
			return null;
		InformationFrame nextFrame = iframes[currentFirst];
		iframes[currentFirst] = null;
		currentFirst = (currentFirst+1) % iframes.length;
		return nextFrame;
	}
	
	public boolean put(InformationFrame f) {
		// Add to window
		this.iframes[f.getNum()] = f;
		return true;
	}

	public void reset(int currentNum) {
		this.iframes = new InformationFrame[this.iframes.length];
		this.currentFirst = currentNum;
	}

	public RejFrame[] getSelectiveRejects() {
		RejFrame[] rejs = new RejFrame[this.iframes.length];
		int rejsAmt = 0;
		for (int i = this.currentFirst; isInsideWindow((byte) i) ; i = (i+1)%this.iframes.length) {
			if (this.iframes[i] == null) {
				rejs[rejsAmt] = new RejFrame((byte) i);
				rejsAmt++;
			}
		}
		return Arrays.copyOfRange(rejs, 0, rejsAmt);
	}

}
