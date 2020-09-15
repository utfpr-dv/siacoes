package br.edu.utfpr.dv.siacoes.model;

//import junit.framework.Test;
//import junit.framework.Campus;
//import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import br.edu.utfpr.dv.siacoes.model.Campus;

public class TesteJUnitCampus {
	

	@Test
	public void UnitCampusName() {
		Campus c = new Campus();
		c.setName("Campus CP");
		
		Assert.assertEquals("Campus CP", c.getName());
	}
}
