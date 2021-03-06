/**
 *  RoutineAbilityTarget.java
 *  Created on Oct 27, 2013 8:16:03 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.ai.seq;

import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.act.ActAbilByTarget;
import net.wombatrpgs.sdrl2015.rpg.ai.BTAction;
import net.wombatrpgs.sdrl2015.rpg.ai.BTCondition;
import net.wombatrpgs.sdrl2015.rpg.ai.BTSequence;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityTargetType;

/**
 * Randomly use an ability with the specified target type if available.
 */
public class RoutineAbilByTarget extends BTSequence {
	
	protected AbilityTargetType type;
	protected ActAbilByTarget act;

	/**
	 * Creates a new ability targeting routine.
	 * @param	actor			The actor that will be acting.
	 * @param	type			The targeting type of the ability to use
	 */
	public RoutineAbilByTarget(CharacterEvent actor, AbilityTargetType type) {
		super(actor);
		this.type = type;
		
		act = new ActAbilByTarget(actor, type);
		addChild(new BTCondition(actor) {
			@Override protected boolean isMet() {
				return act.canUse();
			}
		});
		addChild(new BTAction(actor, act, true));
	}

}
