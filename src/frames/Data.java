package frames;

public class Data {

    private byte[] data;
    
    public Data(String sData){ this.data = sData.getBytes(); }
    
    public Data(byte[] bData) { this.data = bData; }
    
    public byte[] getByteData() { return this.data; }
    
}

