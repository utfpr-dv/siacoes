package br.edu.utfpr.dv.siacoes.model;

//import junit.framework.Test;
//import junit.framework.Campus;
//import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import br.edu.utfpr.dv.siacoes.model.Department;


public class TesteJUnitDepartment {

	@Test
	public void UnitDepartmentName() {
		Department d = new Department();
		d.setName("Suporte");

		Assert.assertEquals("Suporte", d.getName());

	}
}



