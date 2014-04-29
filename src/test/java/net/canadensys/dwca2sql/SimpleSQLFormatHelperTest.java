package net.canadensys.dwca2sql;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for SimpleSQLFormatHelper.
 * @author cgendreau
 *
 */
public class SimpleSQLFormatHelperTest {
	
	@Test
	public void testFormatSQLStatementComponent(){
		String formatted = SimpleSQLFormatHelper.formatSQLStatementComponent("test.1", "my %s");
		assertEquals("my test_1", formatted);
		
		formatted = SimpleSQLFormatHelper.formatSQLStatementComponent("test1", "my %s %s", "again");
		assertEquals("my test1 again", formatted);
	}

}
