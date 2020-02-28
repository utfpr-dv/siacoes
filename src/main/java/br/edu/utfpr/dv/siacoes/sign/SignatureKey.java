package br.edu.utfpr.dv.siacoes.sign;

import org.apache.commons.lang3.NotImplementedException;

/**
Gerenciamento de chaves deve ser implementado, contendo as duas funções descritas
*/

public class SignatureKey {
	
	public static byte[] sign(String login, String password, byte[] data) throws Exception {
		throw new NotImplementedException("Must be implemented.");
	}
	
	public static boolean verify(String login, byte[] data, byte[] signature) throws Exception {
		throw new NotImplementedException("Must be implemented.");
	}

}
