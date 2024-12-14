package org.example.framework;
/**
 * This class represents the trace of the simulation.
 */
public class Trace {
	public enum Level{INFO, WAR, ERR}
	private static Level traceLevel;
	/**
	 * Sets the trace level of the simulation.
	 * @param lvl The trace level to set.
	 */
	public static void setTraceLevel(Level lvl){
		traceLevel = lvl;
	}
	/**
	 * Outputs a message to the console.
	 * @param lvl The level of the message.
	 * @param txt The text of the message.
	 */
	public static void out(Level lvl, String txt){
		if (lvl.ordinal() >= traceLevel.ordinal()){
			System.out.println(txt);
		}
	}
}