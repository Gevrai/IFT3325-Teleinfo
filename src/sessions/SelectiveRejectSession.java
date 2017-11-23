package sessions;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

public class SelectiveRejectSession extends Session {

	public SelectiveRejectSession(String machineName, String portNumber) throws NumberFormatException, UnknownHostException, IOException {
		super(machineName, portNumber);
	}

	@Override
	public boolean send(InputStream istream) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
