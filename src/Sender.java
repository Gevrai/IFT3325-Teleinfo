

public class Sender {

	private String 	machineName;
	private String 	portNumber;
	private String 	fileName;
	private int 	goBackAmmount;
    
	public Sender(String machineName, String portNumber, String fileName, String goBackAmmount) {
		// TODO vérifier la validité des informations données
		this.machineName = machineName;
		this.portNumber = portNumber;
		this.fileName = fileName;
		this.goBackAmmount = Integer.parseUnsignedInt(goBackAmmount);
	}

	public static void main(String[] args) {
		// Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>
		if (args.length != 4) {
			// ERROR !
			System.out.println("Invalid arguments, should be of form : Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
			return;
		}
		
		Sender sender = new Sender(args[0], args[1], args[2], args[3]);
		
	}
}
