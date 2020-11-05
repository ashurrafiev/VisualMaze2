package com.xrbpowered.visualmaze;

import java.awt.Color;
import java.io.File;
import java.util.Scanner;

public abstract class TemplateParser<R, T extends Template<R>> {

	public static boolean debugRegisterResource = false;
	
	protected T template = null;
	
	protected File rootDir = new File(".");
	
	protected int lineNumber = 1;
	protected boolean stop = false;
	protected boolean skipping = false;
	protected int makeVariants;
	
	protected int resIndex = 0;
	protected String[] prevKey = null;
	
	protected void warn(String s) {
		System.err.printf("Warning[%d]: %s\n", lineNumber, s);
	}
	
	protected void error(String s) {
		System.err.printf("Error[%d]: %s\n", lineNumber, s);
	}

	protected abstract T createTemplate();
	protected abstract R makeResource(int resIndex, int makeVariant);

	protected int defaultVariants() {
		return 1;
	}
	
	protected int maxVariants() {
		return defaultVariants();
	}
	
	protected String[] keyVariant(String[] key, int makeVariant) {
		return key;
	}
	
	protected R registerResource(String[] vkey, int resIndex, int makeVariant) {
		if(debugRegisterResource)
			System.out.printf("%s/%s/%s/%s:%d[%d]\n", vkey[0], vkey[1], vkey[2], vkey[3], resIndex, makeVariant);
		if(template!=null) {
			R res = makeResource(resIndex, makeVariant);
			if(res!=null)
				new Tile<>(vkey, res).register(template);
			return res;
		}
		else {
			return null;
		}
	}

	protected void registerResourceVariants(String[] key, int resIndex) {
		for(int i=0; i<makeVariants; i++)
			registerResource(keyVariant(key, i), resIndex, i);
	}

	protected float floatArg(String[] args, int index, float fallbackValue, boolean required) {
		boolean err = true;
		if(index<args.length) {
			try {
				return Float.parseFloat(args[index]);
			}
			catch(NumberFormatException e) {
			}
		}
		else {
			err = required;
		}
		if(err)
			error("Expected float argument "+index);
		return fallbackValue;
	}

	protected float floatArg(String[] args, int index, float fallbackValue) {
		return floatArg(args, index, fallbackValue, true);
	}

	protected int intArg(String[] args, int index, int fallbackValue, boolean required) {
		boolean err = true;
		if(index<args.length) {
			try {
				return Integer.parseInt(args[index]);
			}
			catch(NumberFormatException e) {
			}
		}
		else {
			err = required;
		}
		if(err)
			error("Expected int argument "+index);
		return fallbackValue;
	}

	protected int intArg(String[] args, int index, int fallbackValue) {
		return intArg(args, index, fallbackValue, true);
	}

	protected Color colorArg(String[] args, int index, Color fallbackValue, boolean required) {
		boolean err = true;
		if(index<args.length || args[index].isEmpty()) {
			try {
				if(args[index].startsWith("$")) {
					return new Color((int)Long.parseLong(args[index].substring(1), 16), args[index].length()>7);
				}
				else {
					String[] s = args[index].split(",", 4);
					if(s.length>=3) {
						float[] f = new float[s.length];
						for(int i=0; i<s.length; i++)
							f[i] = Float.parseFloat(s[i]);
						return (s.length==3) ? new Color(f[0], f[1], f[2]) : new Color(f[0], f[1], f[2], f[3]);
					}
				}
			}
			catch(NumberFormatException e) {
			}
		}
		else {
			err = required;
		}
		if(err)
			error("Expected color argument "+index);
		return fallbackValue;
	}
	
	protected Color colorArg(String[] args, int index, Color fallbackValue) {
		return colorArg(args, index, fallbackValue, true);
	}
	
	protected String stringArg(String[] args, int index, String fallbackValue, boolean required) {
		boolean err = true;
		if(index<args.length)
			return args[index];
		else
			err = required;
		if(err)
			error("Expected string argument "+index);
		return fallbackValue;
	}

	protected String stringArg(String[] args, int index, String fallbackValue) {
		return stringArg(args, index, fallbackValue, true);
	}

	protected void command(String[] args) {
		if(args[0].equals("@stop"))
			stop = true;
		
		else if(args[0].equals("@makevars")) {
			makeVariants = intArg(args, 1, makeVariants);
			if(makeVariants<0 || makeVariants>maxVariants()) {
				error(String.format("Cannot make %d variants, set to %d", defaultVariants()));
				makeVariants = defaultVariants();
			}
		}
		
		else if(args[0].equals("@skip")) {
			prevKey = null;
			skipping = true;
		}
		
		else if(args[0].equals("@noskip"))
			skipping = false;

		else if(args[0].equals("@fillpattern")) {
			try {
				template.fillPattern = (FillPattern) FillPattern.class.getField(args[1]).get(null);
			} catch(Exception e) {
				error(String.format("Cannot set fill pattern %s: %s(%s)", args[1], e.getClass().getSimpleName(), e.getMessage()));
			}
		}

		else if(args[0].equals("@attempts"))
			template.generatorAttempts = Integer.parseInt(args[1]);
		
		else
			warn("Unknown command, ignored: "+args[0]);
	}
	
	protected String[] parseKey(String line) {
		if(skipping)
			return null;
		if(line.matches("(\\w+\\/){3}\\w+")) {
			return line.split("\\/", 4);
		}
		else {
			error("Bad key format");
			return null;
		}
	}
	
	protected void parseLine(String line) {
		if(line.startsWith("@")) {
			String args[] = line.split("\\s+");
			command(args);
		}
		
		else if(line.startsWith("--")) {
			resIndex++;
			prevKey = null;
		}
		
		else if(line.startsWith("+")) {
			int count = 1;
			if(line.matches("\\+\\d*")) {
				if(line.length()>1)
					count = Integer.parseInt(line.substring(1));
				for(int i=0; i<count; i++) {
					if(prevKey!=null)
						registerResourceVariants(prevKey, resIndex);
					resIndex++;
				}
			}
			else
				error("Bad +format");
		}
		
		else {
			String[] key = parseKey(line);
			if(key!=null)
				registerResourceVariants(key, resIndex);
			resIndex++;
			prevKey = key;
		}
	}
	
	protected void finish() {
	}
	
	public T parse(Scanner in) {
		if(template!=null || lineNumber!=1)
			throw new RuntimeException("Parser not reset");
		template = createTemplate();
		makeVariants = defaultVariants();
		boolean comment = false;
		while(!stop && in.hasNextLine()) {
			String line = in.nextLine().trim();
			if(comment) {
				if(line.equals(">#"))
					comment = false;
			}
			else if(line.equals("#<")) {
				comment = true;
			}
			else if(!line.isEmpty() && !line.startsWith("#"))
				parseLine(line);
			lineNumber++;
		}
		finish();
		return template;
	}
	
	public T parse(File file) {
		try {
			rootDir = file.getParentFile();
			Scanner in = new Scanner(file);
			parse(in);
			in.close();
			return template;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[] rotateKey(String[] key, int cw) {
		if(cw==0)
			return key;
		String[] vkey = new String[4];
		for(int i=0; i<4; i++)
			vkey[i] = key[(i+4-cw)%4];
		return vkey;
	}
	
}
