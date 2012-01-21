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

import it.freedom.model.geometry.FreedomPolygon;
import it.freedom.model.object.EnvObject;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

public class DrawableObject extends DrawableElement{

	private EnvObject envObject;
	private Bitmap ghostBitmap;
	private Path ghostPath;
	private Paint ghostPaint= new Paint();
	//current scale/rotate matrix of the object
	Matrix drawingMatrix = new Matrix();
	
	public DrawableObject(EnvObject envObject)
	{
		super();
		this.setEnvObject(envObject);		
	}
	
	
	public EnvObject getEnvObject() {
		return envObject;
	}


	public void setEnvObject(EnvObject envObject) {
		this.envObject = envObject;
		ghostPaint.setColor(indexColor);
	}


	@Override
	public String getName() {
		return getEnvObject().getName();
	}

	@Override
	public void draw(Canvas canvas) {
		System.out.println("drawing object: "+ getEnvObject().getName());
		String file = getEnvObject().getCurrentRepresentation().getIcon();
		drawingMatrix = new Matrix();		
		float rotation = (float) getEnvObject().getCurrentRepresentation().getRotation();
		System.out.println("rotation: "+ rotation);			
		drawingMatrix.postRotate(rotation);
		drawingMatrix.postTranslate(getEnvObject().getCurrentRepresentation().getOffset().getX(), getEnvObject().getCurrentRepresentation().getOffset().getY());    		
        if (file!=null)
        {
            //TODO: Asign the bmp in the setEnvObject
        	Bitmap bmp =BitmapUtils.getImage(file,-1,-1);            
            ghostBitmap =Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),Config.ARGB_8888);
            canvas.drawBitmap(bmp,drawingMatrix,null);
        }
        else
        {
        	//TODO: Cache path
    		Path objectPath = DrawingUtils.freedomPolygonToPath((FreedomPolygon)getEnvObject().getCurrentRepresentation().getShape());      		
    		Paint paint = new Paint();
    		paint.setStyle(Style.FILL);
    		
    		ghostPath = new Path();
    		objectPath.transform(drawingMatrix, ghostPath);
    		int fillColor=-1;
    		try
    		{
    			fillColor = Color.parseColor(getEnvObject().getCurrentRepresentation().getFillColor());    			
    			paint.setColor(fillColor);
    			canvas.drawPath(ghostPath, paint);
    		}
    		catch(IllegalArgumentException ex)
    		{
    			System.out.println("ParseColor exception in fill");
    		}
    		int borderColor=-1;
    		try
    		{
    			borderColor = Color.parseColor(getEnvObject().getCurrentRepresentation().getBorderColor());    			
    			paint.setColor(borderColor);
    			paint.setStyle(Style.STROKE);
    			canvas.drawPath(ghostPath, paint);
    		}
    		catch(IllegalArgumentException ex)
    		{
    			System.out.println("ParseColor exception in border");
    		}    		
        }
	}
		
	@Override
	public void drawGhost(Canvas canvas) {
		if (ghostBitmap!= null)
		{			
			ghostBitmap.eraseColor(indexColor);
			canvas.drawBitmap(ghostBitmap,drawingMatrix, null);
		}
		else
		{			
			canvas.drawPath(ghostPath, ghostPaint);			
		}
	}

}
