/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package es.gpulido.freedom.util;

import android.graphics.Canvas;

public abstract class DrawableElement {
	
	int indexColor;
	
	public DrawableElement()
	{
		setIndexColor();		
	}
	
		
	public abstract String getName();
	
		
	public void setIndexColor() {
		this.indexColor= DrawingUtils.generateNextValidColor();		
	}
	
	public int getIndexColor()
	{
		return indexColor;		
	}
	public abstract void draw(Canvas canvas);
	public abstract void drawGhost(Canvas canvas);
}
