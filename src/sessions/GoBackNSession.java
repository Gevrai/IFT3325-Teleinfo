package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

public class GoBackNSession extends Session {

	public GoBackNSession(String machineName, int portNumber) throws UnknownHostException, IOException {
		super(machineName, portNumber);
	}

	@Override
	public boolean send(InputStream istream) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}


}
