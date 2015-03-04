/**
 *  BossAI.java
 *  Created on Oct 27, 2013 7:05:24 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.ai;

import net.wombatrpgs.mrogueschema.characters.ai.BossIntelligenceMDO;
import net.wombatrpgs.sdrl2015.rpg.Boss;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.abil.AbilTeleport;
import net.wombatrpgs.sdrl2015.rpg.act.ActStepHero;
import net.wombatrpgs.sdrl2015.rpg.act.ActWait;
import net.wombatrpgs.sdrl2015.rpg.act.ActWander;
import net.wombatrpgs.sdrl2015.rpg.ai.seq.RoutineChanceAbility;
import net.wombatrpgs.sdrl2015.rpg.ai.seq.RoutineOffenseAbility;

/**
 * Creates boss-like intelligence!! Oh yeah!!!
 */
public class BossAI extends BTSelector {
	
	protected BossIntelligenceMDO mdo;
	protected Boss boss;

	/**
	 * Creates boss AI for a boss.
	 * @param	actor				The boss to generate for
	 */
	public BossAI(CharacterEvent actor, BossIntelligenceMDO mdo) {
		super(actor);
		this.mdo = mdo;
		boss = (Boss) actor;		// you gotta be a boss to use dis AI!!
		
		BTSequence idleSeq = new BTSequence(actor);
		idleSeq.addChild(new BTCondition(actor) {
			@Override protected boolean isMet() {
				return !boss.hasBeenSighted();
			}
		});
		idleSeq.addChild(new BTAction(actor, new ActWait(), true));
		
		BTSelector fightRou = new BTSelector(actor);
		fightRou.addChild(new RoutineChanceAbility(actor, AbilTeleport.class, .1f));
		fightRou.addChild(new RoutineOffenseAbility(actor, .5f));
		fightRou.addChild(new BTAction(actor, new ActStepHero(actor), true));
		fightRou.addChild(new BTAction(actor, new ActWander(actor), true));
		
		addChild(idleSeq);
		addChild(fightRou);
	}

}
