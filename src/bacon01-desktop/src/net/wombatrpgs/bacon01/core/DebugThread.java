/**
 *  DebugThread.java
 *  Created on Sep 20, 2014 9:08:46 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.bacon01.core;

import java.util.Scanner;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.scenes.SceneLib;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import com.badlogic.gdx.Gdx;

/**
 * A debuggy sort of thing. Launched when you press select somewhere?
 */
public class DebugThread implements Runnable {
	
	protected static DebugThread instance;
	
	protected Thread thread;
	protected boolean running;
	
	/**
	 * Launches the debug thread if it is not already running.
	 */
	public static void launchInstance() {
		MGlobal.reporter.inform("Debug thread launched.");
		if (instance == null) {
			instance = new DebugThread();
		}
		if (!instance.isRunning()) {
			instance.launch();
		}
	}
	
	public static void stopInstance() {
		if (instance != null && instance.isRunning()) {
			MGlobal.reporter.inform("Debug thread stopped.");
			instance.running = false;
		}
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		while (running) {
			final String line = sc.nextLine();
			if (line.equals("exit") || line.equals("exit()")) {
				stopInstance();
			}
			Gdx.app.postRunnable(new Runnable() {
				@Override public void run() {
					try {
						LuaValue script = MGlobal.lua.interpret("return (" + line + ")");
						LuaValue result = MGlobal.lua.run(script, LuaValue.NIL);
						if (result != LuaValue.NIL) {
							Object resultObject = CoerceLuaToJava.coerce(result, Object.class);
							System.out.println(resultObject.toString());
						}
						SceneLib.runExtraCommands();
						System.out.println("Executed successfully");
					} catch (LuaError error) {
						System.err.println(error.getMessage());
					}
				}
			});
		}
		sc.close();
	}

	/**
	 * Checks if the debug thread is running.
	 * @return					True if thread is running
	 */
	protected boolean isRunning() {
		return running;
	}
	
	/**
	 * Starts the debug thread.
	 */
	protected void launch() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

}
