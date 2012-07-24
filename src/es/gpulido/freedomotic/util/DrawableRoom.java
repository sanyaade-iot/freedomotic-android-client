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
package es.gpulido.freedomotic.util;

import es.gpulido.freedomotic.ui.preferences.Preferences;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import it.freedomotic.model.environment.Zone;

public class DrawableRoom extends DrawableElement{
	
	private Zone roomObject;
	private Paint fillingPaint;
	private Paint borderPaint;
	private Paint textPaint;
	private Paint ghostPaint;
	private Path drawingPath;
	private RectF bounds= new RectF();
	public static final String TEXTURE_PATH = "";
	
	public DrawableRoom(Zone roomObject, Paint borderPaint,Paint textPaint)
	{
		super();
		this.roomObject = roomObject;
					
		createFillingPaint();
		this.borderPaint = borderPaint;
		this.textPaint = textPaint;		
		setDrawingPath(DrawingUtils.freedomPolygonToPath(roomObject.getShape()));	
		ghostPaint = new Paint();
		ghostPaint.setStyle(Paint.Style.FILL);
		ghostPaint.setAntiAlias(false);	      		
		
	}

	public Zone getRoomObject() {
		return roomObject;
	}

	public void setRoomObject(Zone roomObject) {
		this.roomObject = roomObject;
	}

	public Paint getFillingPaint() {
		return fillingPaint;
	}
	
	public void setFillingPaint(Paint fillingPaint){
		this.fillingPaint = fillingPaint;
	}
	
	public void createFillingPaint()
	{
		String textureName = TEXTURE_PATH+roomObject.getTexture();
		if (!DrawingUtils.roomsBitmapsCache.containsKey(textureName))			
			DrawingUtils.roomsBitmapsCache.put(textureName,BitmapUtils.downloadFile(Preferences.getResourcesURL() + textureName)); 		
		
		fillingPaint = new Paint();		
		fillingPaint.setShader(new BitmapShader(DrawingUtils.roomsBitmapsCache.get(textureName),Shader.TileMode.REPEAT,
              Shader.TileMode.REPEAT));		  		
	}
	public Paint getBorderPaint() {
		return borderPaint;
	}

	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}
				
	public Path getDrawingPath() {
		return drawingPath;
	}

	public void setDrawingPath(Path drawingPath) {		
		this.drawingPath = drawingPath;
		drawingPath.computeBounds(bounds, true);
	}
	
	public void transform(Matrix matrix)
	{				
		drawingPath.transform(matrix);
		drawingPath.computeBounds(bounds, true);
	}
	
	@Override
	public void draw(Canvas canvas)
	{
		//draw the fill
		canvas.drawPath(drawingPath,fillingPaint);
		//draw the border
		canvas.drawPath(drawingPath,borderPaint);
		//draw the text
		
		canvas.drawText(roomObject.getName(), bounds.left+22, bounds.top+22, textPaint);
	}

	@Override
	public void drawGhost(Canvas canvas)
	{
		ghostPaint.setColor(getIndexColor());
		canvas.drawPath(drawingPath,ghostPaint);	
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return roomObject.getName();
	}


	
}
