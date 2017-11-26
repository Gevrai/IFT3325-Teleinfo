package receiver;

import frames.InformationFrame;

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

}
