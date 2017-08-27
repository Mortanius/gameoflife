package integracao;

@SuppressWarnings("serial")
public class ProcessNotInitializedException extends Exception {
	public ProcessNotInitializedException(String commands, Throwable cause) {
		super("O processo \""+ commands+ "\" n�o foi iniciado.", cause);
	}
	
}
