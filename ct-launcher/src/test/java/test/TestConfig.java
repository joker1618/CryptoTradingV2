package test;

import com.fede.ct.v2.common.config._public.ConfigPublic;
import com.fede.ct.v2.common.config._public.IConfigPublic;
import org.junit.Test;

import java.util.logging.Level;

import static java.lang.System.out;

/**
 * Created by f.barbano on 09/11/2017.
 */
public class TestConfig {

	@Test
	public void test() {
		IConfigPublic configPublic = ConfigPublic.getUniqueInstance();
		Level level = configPublic.getConsoleLevel();
		out.println(level);
	}
}
