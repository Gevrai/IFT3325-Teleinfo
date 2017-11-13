

public class Receiver {
	
	private String portNumber;

	public Receiver(String portNumber) {
		// TODO vérifier validité de cette information
		this.portNumber = portNumber;
	}

	public static void main(String[] args) {
		// Receiver <Numero_Port>
		if (args.length != 1) {
			// ERROR !
			System.out.println("Invalid arguments, should be of form : Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
			return;
		}
		
		Receiver receiver = new Receiver(args[0]);

	}

}
