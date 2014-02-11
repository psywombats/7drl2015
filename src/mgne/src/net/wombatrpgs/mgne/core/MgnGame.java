/**
 *  MgnGame.java
 *  Created on Feb 10, 2014 12:17:05 AM for project mgne
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.core;

/**
 * The MgnGame corresponds to all the game-specific stuff a game might want to
 * define. Create a subclass called SagaGame or something, I don't care. This
 * should probably be turned into an interface that returns a data blob that
 * controls things like game-specific settings, but the database exists to
 * handle all of the things like game name, resolution, etc, that most
 * conventional engines pass up in their application class. Instead, this should
 * only require the asbolute raw-est stuff needed to make the game work from a
 * source level.
 * 
 * I'm still not sure what to do about hooks. Anything that followed the old
 * super-janky factory isClassAssignableFrom antipattern should definitely get
 * hooked in somewhere. Otherwise, let's hope the game can be mostly scripts and
 * database entries!
 */
public abstract class MgnGame {

}