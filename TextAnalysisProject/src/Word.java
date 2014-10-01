/**
 * @author Pratiksha Pai
 */

import java.util.HashMap;

public class Word {
	int frequency;
    HashMap<Integer, String> quotesMap = new HashMap<Integer, String>();

    public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public HashMap<Integer, String> getMap() {
		return quotesMap;
	}
//	public void setMap(HashMap<Integer, String> map) {
//		this.map = map;
//	}
    public void putLine(int lineNumber, String line){
    	this.quotesMap.put(lineNumber, line);
    }
	public Object entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
