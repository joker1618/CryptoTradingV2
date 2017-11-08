package com.fede.ct.v2.common.config;

import com.fede.ct.v2.common.logger.LogService;
import com.fede.ct.v2.common.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 12/10/2017.
 */
public abstract class AbstractConfig {

	private Map<String, Prop> configMap;

	private static final String KEY_SEP = "=";
	private static final String COMMENT_START = "#";


	protected AbstractConfig(String configPath) {
		this.configMap = Collections.synchronizedMap(new HashMap<>());

		try {
			loadConfigFile(configPath);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Unable to init config from file \"%s\"", configPath), e);
		}
	}

	protected void loadConfigFile(String configFilePath) throws IOException {
		InputStream is = new FileInputStream(configFilePath);
		loadConfigFile(is);
	}
	
	protected void loadConfigFile(InputStream is) throws IOException {
		// read properties from file
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		Map<String, Prop> propMap = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			if(StringUtils.isNotBlank(line.trim()) && !line.trim().startsWith(COMMENT_START) && line.contains(KEY_SEP)) {
				int idxSep = line.indexOf(KEY_SEP);
				String key = line.substring(0, idxSep).trim();
				String value = line.substring(idxSep+1).trim();
				configMap.put(key, new Prop(key, value, value));
			}
		}

		// replace variables
		// #var#  and  ${var}  allowed
		evaluateVariables();
	}

	private void evaluateVariables() {
		configMap.forEach((key,prop) -> prop.evalutedValue = prop.originalValue);

		Set<String> keys = configMap.keySet();
		boolean changed;
		do {
			changed = false;
			for(String key : keys) {
				Prop prop = configMap.get(key);
				String actualEval = prop.evalutedValue;
				if(containsVariables(actualEval)) {
					List<Var> vars = getVariables(actualEval);
					String newEval = actualEval;
					for (Var v : vars) {
						Prop p = configMap.get(v.varName);
						if(p != null) 	newEval = newEval.replace(v.placeholder, p.evalutedValue);

					}
					if(!newEval.equals(actualEval)) {
						prop.evalutedValue = newEval;
						changed = true;
					}
				}
			}
		} while (changed);
	}

	// return the next variable found:   #var#  or  ${var}
	private List<Var> getVariables(String value) {
		String str = value;
		String varName;
		boolean go = true;
		List<Var> toRet = new ArrayList<>();

		while(go) {
			Var var = null;

			varName = StringUtils.substringBetween(str, "#", "#");
			if(StringUtils.isNotBlank(varName)) {
				var = new Var(varName, "#" + varName + "#");
			} else {
				varName = StringUtils.substringBetween(str, "${", "}");
				if(StringUtils.isNotBlank(varName)) {
					var = new Var(varName, "${" + varName + "}");
				}
			}

			if(var == null) {
				go = false;
			} else {
				toRet.add(var);
				int nextStart = str.indexOf(var.placeholder) + var.placeholder.length();
				str = str.substring(nextStart);
			}
		}

		return toRet;
	}

	private boolean containsVariables(String value) {
		String varName = StringUtils.substringBetween(value, "#", "#");
		if(StringUtils.isNotBlank(varName)) {
			return true;
		}

		varName = StringUtils.substringBetween(value, "${", "}");
		if(StringUtils.isNotBlank(varName)) {
			return true;
		}

		return false;
	}


	protected String getString(String key) {
		return getString(key, null);
	}
	protected String getString(String key, String _default) {
		Prop prop = configMap.get(key);
		return prop == null ? _default : prop.evalutedValue;
	}
	protected Integer getInt(String key) {
		return getInt(key, null);
	}
	protected Integer getInt(String key, Integer _default) {
		String value = getString(key);
		return value == null ? _default : Integer.parseInt(value);
	}
	protected Double getDouble(String key) {
		return getDouble(key, null);
	}
	protected Double getDouble(String key, Double _default) {
		String value = getString(key);
		return value == null ? _default : Double.parseDouble(value);
	}
	protected BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, null);
	}
	protected BigDecimal getBigDecimal(String key, BigDecimal _default) {
		String value = getString(key);
		return value == null ? _default : BigDecimal.valueOf(Double.parseDouble(value));
	}
	protected Path getPath(String key) {
		return Paths.get(getString(key));
	}
	protected Path getPath(String key, Path _default) {
		String value = getString(key);
		return value == null ? _default : Paths.get(value);
	}
	protected boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}
	protected List<String> getCommaSeparatedList(String key) {
		return StrUtil.splitFieldsList(getString(key), ",", true);
	}

	protected Level getLoggerLevel(String key) {
		return getLoggerLevel(key, null);
	}
	protected Level getLoggerLevel(String key, Level _default) {
		try {
			return LogService.LogLevel.parse(getString(key));
		} catch(Exception ex) {
			return _default;
		}
	}

	protected void addNewProperty(String key, String value) {
		Prop prop = new Prop(key, value, value);
		configMap.put(key, prop);
		evaluateVariables();
	}

	protected boolean existsKey(String key) {
		return configMap.containsKey(key);
	}

	@Override
	public String toString() {
		List<String> list = new ArrayList<>();
		configMap.forEach((k,v) -> list.add(k + "=" + v.evalutedValue));
		return list.stream().collect(Collectors.joining("\n"));
	}

	private class Var {
		String varName;
		String placeholder;
		Var(String varName, String placeholder) {
			this.varName = varName;
			this.placeholder = placeholder;
		}
	}

	private class Prop {
		String key;
		String originalValue;
		String evalutedValue;
		public Prop(String key, String originalValue, String evalutedValue) {
			this.key = key;
			this.originalValue = originalValue;
			this.evalutedValue = evalutedValue;
		}
		public String toString() {
			return String.format("[%s, %s]", originalValue, evalutedValue);
		}
	}
}

