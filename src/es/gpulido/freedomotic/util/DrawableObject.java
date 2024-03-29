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

import it.freedomotic.model.geometry.FreedomPolygon;
import it.freedomotic.model.object.EnvObject;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;


public class DrawableObject extends DrawableElement{

	private EnvObject envObject;
	private Bitmap ghostBitmap;
	private Path ghostPath;
	private Paint ghostPaint= new Paint();
	//current scale/rotate matrix of the object
	Matrix drawingMatrix = new Matrix();
	public static final String OBJECT_PATH = "object/";	
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
		String file = getEnvObject().getCurrentRepresentation().getIcon();
		Path objectPath = DrawingUtils.freedomPolygonToPath((FreedomPolygon)getEnvObject().getCurrentRepresentation().getShape());
		RectF box = new RectF();
		objectPath.computeBounds(box, true);
		System.out.print("GPT box: box widht:"+box.width()+ " box heigh"+box.height());
		drawingMatrix = new Matrix();		
		float rotation = (float) getEnvObject().getCurrentRepresentation().getRotation();			
		drawingMatrix.postRotate(rotation);
		drawingMatrix.postTranslate(getEnvObject().getCurrentRepresentation().getOffset().getX(), getEnvObject().getCurrentRepresentation().getOffset().getY());    		
		Bitmap bmp=null;
		if(file!=null)
		{    		 //TODO: Asign the bmp in the setEnvObject       
        	 bmp =BitmapUtils.getImage(file,(int)box.width(),(int)box.height());
		}
		if (bmp!=null)
        {                       
         	ghostBitmap =Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),Config.ARGB_8888);
        	canvas.drawBitmap(bmp,drawingMatrix,null);
    
        }
        else
        {
        	//TODO: Cache path    		
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
