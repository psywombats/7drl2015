/**
 *  Hud.java
 *  Created on Feb 6, 2013 1:56:43 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.rpg.abil.Ability;
import net.wombatrpgs.sdrl2015.ui.text.FontHolder;
import net.wombatrpgs.sdrl2015.ui.text.TextBoxFormat;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;
import net.wombatrpgs.sdrlschema.ui.FontMDO;
import net.wombatrpgs.sdrlschema.ui.NumberSetMDO;

/**
 * Heads-up display! Everybody's favorite piece of UI. This version is stuck
 * into a UI object and should be told to draw itself as part of a screen.<br>
 * This specific HUD is probably overridden on a per-game basis. Christ knows I
 * just ripped it apart for Rainfall. Ugh.
 * 
 * 7DRL: ripped it apart for 7DRL as well! Oh joy!
 */
public class Hud extends UIElement {
	
	protected static final int TEXT_WIDTH = 256;
	
	protected static final String FONT_DEFAULT = "font_c64mrogue_small";
	protected static final String NUMSET_BIG = "numberset_ghost";
	protected static final String NUMSET_SMALL = "numberset_small";
	
	protected static final String CAMP_STRING = "press c to camp";

	protected static final String IMG_FRAME = "hud.png";
	protected static final String IMG_FRAME_BACK = "hud_backer.png";
	protected static final String IMG_HPBLOB = "healthball.png";
	protected static final String IMG_MP = "mp.png";
	protected static final String IMG_SP = "sp.png";
	
	protected static final float DELAY_DIGIT = .03f;
	
	protected static final int BAR_HEIGHT = 134;
	protected static final int HP_TEXT_X = 866;
	protected static final int HP_TEXT_Y = 680;
	protected static final int SPRITE_X = 850;
	protected static final int SPRITE_Y = 625;
	protected static final int BLOB_X = 850;
	protected static final int BLOB_Y = 774;
	protected static final int BAR1_X = 913;
	protected static final int BAR1_Y = 716;
	protected static final int BAR2_X = 936;
	protected static final int BAR2_Y = 716;
	
	protected static final int ABIL_START_X = 11;
	protected static final int ABIL_START_Y = 709;
	protected static final int ABIL_WIDTH = 78;
	protected static final int ABIL_SPRITE_X = 17;
	protected static final int ABIL_SPRITE_Y = -32;
	protected static final int ABIL_TEXT_X = 8;
	protected static final int ABIL_TEXT_Y = -20;
	
	protected Graphic frame, frameBacker;
	protected Graphic hpBlob, mpBlob, spBlob;
	protected NumberSet numbersBig, numbersSmall;
	protected TextBoxFormat abilFormat, campFormat;
	
	protected FontHolder font;
	protected float hp, mhp, toCamp, toCampMax;
	
	protected boolean enabled;
	protected boolean ignoresTint;
	protected boolean awaitingReset;
	protected int currentHPDisplay;
	protected float timeToDigitHP;

	/**
	 * Creates a new HUD from... thin air!
	 */
	public Hud() {
		ignoresTint = true;
		
		frame = startGraphic(IMG_FRAME);
		frameBacker = startGraphic(IMG_FRAME_BACK);
		hpBlob = startGraphic(IMG_HPBLOB);
		mpBlob = startGraphic(IMG_MP);
		spBlob = startGraphic(IMG_SP);
		
		awaitingReset = true;
		currentHPDisplay = 0;
		
		numbersBig = new NumberSet(MGlobal.data.getEntryFor(NUMSET_BIG, NumberSetMDO.class));
		numbersSmall = new NumberSet(MGlobal.data.getEntryFor(NUMSET_SMALL, NumberSetMDO.class));
		assets.add(numbersBig);
		assets.add(numbersSmall);
		
		font = new FontHolder(MGlobal.data.getEntryFor(FONT_DEFAULT, FontMDO.class));
		assets.add(font);
		
		abilFormat = new TextBoxFormat();
		
		campFormat = new TextBoxFormat();
		campFormat.align = HAlignment.RIGHT;
		campFormat.height = 50;
		campFormat.width = 100;
		campFormat.x = MGlobal.window.getWidth() - 6 - campFormat.width;
		campFormat.y = MGlobal.window.getHeight() - 562;
	}
	
	/** @return True if the hud is displaying right now */
	public boolean isEnabled() { return enabled; }
	
	/** @param True if the hud is going to be displayed */
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	
	/** @see net.wombatrpgs.mrogue.screen.ScreenObject#ignoresTint() */
	@Override public boolean ignoresTint() { return ignoresTint; }

	/**
	 * @see net.wombatrpgs.mrogue.core.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		if (awaitingReset) {
			currentHPDisplay = MGlobal.hero.getUnit().get(Stat.HP);
			awaitingReset = false;
			timeToDigitHP = 0;
		}
		timeToDigitHP += elapsed;
		while (timeToDigitHP > DELAY_DIGIT) {
			timeToDigitHP -= DELAY_DIGIT;
			if (currentHPDisplay > MGlobal.hero.getUnit().get(Stat.HP)) {
				currentHPDisplay -= 1;
			} else if (currentHPDisplay < MGlobal.hero.getUnit().get(Stat.HP)) {
				currentHPDisplay += 1;
			}
		}
		mhp = MGlobal.hero.getUnit().get(Stat.MHP);
		hp = currentHPDisplay;
	}

	/**
	 * @see net.wombatrpgs.mrogue.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		SpriteBatch batch = getBatch();
		float ratioHP = hp/mhp;
		float ratioCamp = MGlobal.hero.getCampRatio();
		
		frameBacker.renderAt(batch, 0, 0);
		
		int blobX = BLOB_X;
		int blobY = BLOB_Y;
		blobX -= hpBlob.getWidth()/2 * ratioHP;
		blobY -= hpBlob.getHeight()/2 * ratioHP;
		blobY -= (hpBlob.getHeight()) * (1f - ratioHP);
		blobY = MGlobal.window.getHeight() - blobY;
		hpBlob.renderAt(batch, blobX, blobY, ratioHP, ratioHP);
		
		blobX = BAR1_X;
		blobY = BAR1_Y;
		blobY = MGlobal.window.getHeight() - blobY;
		mpBlob.renderAt(batch, blobX, blobY, 1f,
				(ratioCamp * (float) BAR_HEIGHT) / mpBlob.getHeight());
		
//		blobX = BAR2_X;
//		blobY = BAR2_Y;
//		blobY = MGlobal.window.getHeight() - blobY;
//		spBlob.renderAt(batch, blobX, blobY, 1f, (ratioSP * (float) BAR_HEIGHT) / spBlob.getHeight());
		
		int hpTextX = HP_TEXT_X;
		int hpTextY = (MGlobal.window.getHeight() - HP_TEXT_Y);
		int digits = String.valueOf(hp).length();
		hpTextX -= digits * 8;
		if (ratioHP > .31) {
			numbersBig.renderNumberAt((int) hp, hpTextX, hpTextY,
					.8f, 1, .8f);
		} else if (ratioHP > .11) {
			numbersBig.renderNumberAt((int) hp, hpTextX, hpTextY,
					1, 1, .6f);
		} else if (ratioHP > 0) {
			numbersBig.renderNumberAt((int) hp, hpTextX, hpTextY,
					1, .6f, .6f);
		}
		
		batch.begin();
		TextureRegion tex = MGlobal.hero.getAppearance().getFrame(2, 1);
		batch.draw(tex,
				SPRITE_X - tex.getRegionWidth()/2,
				(MGlobal.window.getHeight() - SPRITE_Y) - tex.getRegionHeight()/2);
		batch.end();
		
		frame.renderAt(batch, 0, 0);
		
		List<Ability> abilities = MGlobal.hero.getUnit().getAbilities();
		String pre = MGlobal.config.get("use_function_keys") ? "F" : "";
		for (int i = 0; i < abilities.size(); i += 1) {
			Ability abil = abilities.get(i);
			if (abil.getUses(MGlobal.hero.getUnit()) <= 0) {
				batch.setColor(new Color(.5f, .5f, .5f, 1));
			}
			abil.getIcon().renderAt(batch,
					ABIL_START_X + ABIL_WIDTH*i + ABIL_SPRITE_X,
					MGlobal.window.getHeight() - (ABIL_START_Y + ABIL_SPRITE_Y));
			abilFormat.align = HAlignment.LEFT;
			abilFormat.width = 100;
			abilFormat.height = 50;
			abilFormat.x = ABIL_START_X + ABIL_WIDTH*i + ABIL_TEXT_X;
			abilFormat.y = MGlobal.window.getHeight() - (ABIL_START_Y + ABIL_TEXT_Y);
			String useString = "left: " + abil.getUses(MGlobal.hero.getUnit());
			font.draw(batch, abilFormat, useString, 0);
			font.draw(batch, abilFormat, pre+(i+1), (int) font.getLineHeight());
			batch.setColor(1, 1, 1, 1);
		}
		
		if (MGlobal.hero.isEligibleForCamp(true)) {
			font.draw(batch, campFormat, CAMP_STRING, 0);
		}
		
//		format.align = HAlignment.LEFT;
//		format.x = mdo.offX - 14;
//		format.y = (int) (mdo.offY - font.getLineHeight());
//		format.width = TEXT_WIDTH;
//		format.height = 32;
//		if (MGlobal.hero.getParent() != null) {
//			String text = "floor: " + MGlobal.hero.getParent().getFloor() + "/13";
//			font.draw(batch, format, text, 0);
//		}
	}
	
	/**
	 * Sets whether the hud ignores tint. It should be ignoring tint for things
	 * like environmental tint but following it for things like transitions.
	 * Basically this solves the old RM problem that pictures would have to fade
	 * out separately if you were using them as a HUD.
	 * @param 	ignoreTint			True if this hud should ignore tint.
	 */
	public void setOverlayTintIgnore(boolean ignoreTint) {
		this.ignoresTint = ignoreTint;
	}
	
	/**
	 * Forces the HUD to update its numerical displays.
	 */
	public void forceReset() {
		currentHPDisplay = MGlobal.hero.getUnit().get(Stat.HP);
	}

}
