import br.edu.utfpr.dv.siacoes.model.Campus;
import br.edu.utfpr.dv.siacoes.model.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTests {
	private Department objDepartment;

	@BeforeEach
	void setUp() {
		this.objDepartment = new Department();
	}

	@Test
	void departmentGettersTest() {
		assertEquals(0 , this.objDepartment.getIdDepartment());
		assertEquals("" , this.objDepartment.getName());
		assertEquals("" , this.objDepartment.getFullName());
		assertNull(this.objDepartment.getLogo());
		assertTrue(this.objDepartment.isActive());
		assertEquals("", this.objDepartment.getSite());
		assertEquals("", this.objDepartment.getInitials());
	}

	@Test
	void departmentSettersTest() {
		this.objDepartment.setIdDepartment(10);
		this.objDepartment.setCampus(null);
		this.objDepartment.setActive(false);
		this.objDepartment.setName("Leandro");
		this.objDepartment.setFullName("Leandro da Silva");

		assertEquals(10, objDepartment.getIdDepartment());
		assertNull(objDepartment.getCampus());
		assertFalse(objDepartment.isActive());
		assertEquals("Leandro", objDepartment.getName());
		assertEquals("Leandro da Silva", objDepartment.getFullName());
	}
}