/**
 *  Prompt.java
 *  Created on Oct 25, 2013 10:33:51 PM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.ui;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.ui.text.FontHolder;
import net.wombatrpgs.sdrl2015.ui.text.TextBoxFormat;
import net.wombatrpgs.sdrlschema.maps.data.OrthoDir;

/**
 * Binary selection box.
 */
public class SelectionDialog extends Popup {
	
	protected static final String CURSOR_FILE = "finger.png";
	
	protected static final int LINE_HEIGHT = 24;
	
	protected Graphic backer, cursor;
	protected TextBoxFormat optFormat;
	protected List<String> options;
	protected SelectionListener listener;
	protected int x, y;
	protected int selected;
	protected float alpha;
	
	/**
	 * Creates a new prompt from data.
	 * @param	mdo				The data to use
	 */
	public SelectionDialog(String backerFile, String... options) {
		this.options = Arrays.asList(options);
		z = 90;
		backer = startGraphic(backerFile);
		cursor = startGraphic(CURSOR_FILE);
	}

	/**
	 * @see net.wombatrpgs.mrogue.screen.ScreenObject#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		super.render(camera);
		Color old = getBatch().getColor();
		getBatch().setColor(1, 1, 1, alpha);
		FontHolder font = MGlobal.ui.getFont();
		
		backer.renderAt(getBatch(), x, y);
		
		for (int i = 0; i < options.size(); i += 1) {
			int offY = -i * LINE_HEIGHT;
			String option = options.get(i);
			font.setAlpha(alpha);
			font.draw(getBatch(), optFormat, option, offY);
			font.setAlpha(1);
		}
		
		int cursorX = x + 32;
		int cursorY = optFormat.y + LINE_HEIGHT * -selected - LINE_HEIGHT/2;
		getBatch().setColor(1, 1, 1, alpha);
		cursor.renderAt(getBatch(), cursorX, cursorY);
		
		getBatch().setColor(old);
		
	}

	/**
	 * @see net.wombatrpgs.mrogue.screen.ScreenObject#update(float)
	 */
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		if (active) {
			alpha = Math.min(1, alpha + elapsed / .2f);
		} else {
			alpha -= elapsed / .2f;
			if (alpha <= 0) {
				MGlobal.screens.peek().removeObject(this);
			}
		}
	}
	
	/**
	 * 7DRL
	 * @param	listener		Who to notify when finished
	 * @param	x				The x-coord to display at
	 * @param	y				The y-coord to display at
	 */
	public void ask(SelectionListener listener, int x, int y) {
		alpha = 0;
		MGlobal.screens.peek().addObject(this);
		this.listener = listener;
		this.x = x;
		this.y = y;
		
		optFormat = new TextBoxFormat();
		optFormat.align = HAlignment.CENTER;
		optFormat.x = x + backer.getWidth()/2 - TEXT_WIDTH/2;
		optFormat.y = y + backer.getHeight() * 3/4;
		optFormat.width = TEXT_WIDTH;
		optFormat.height = TEXT_HEIGHT;
		
		show();
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#onCursorMove
	 * (net.wombatrpgs.mrogueschema.maps.data.OrthoDir)
	 */
	@Override
	protected boolean onCursorMove(OrthoDir dir) {
		switch (dir) {
		case NORTH:
			selected -= 1;
			break;
		case SOUTH:
			selected += 1;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#cancel()
	 */
	@Override
	protected boolean cancel() {
		listener.onCancel();
		return super.cancel();
	}

	/**
	 * @see net.wombatrpgs.mrogue.ui.Popup#confirm()
	 */
	@Override
	protected boolean confirm() {
		listener.onResult(selected);
		hide();
		return true;
	}
	
}
