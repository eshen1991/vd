package com.example.es;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CameraUtil {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int IMAGE_MAX_SIZE = 400;
	public static final String SHARE_PREF_IMG_URL = "prefImgUrl";
	 public final static String  SHARE_PREF_IMG_KEY = "prefImgUrlKey";

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "EsCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public static Bitmap decodeFile(File f) {
		Bitmap b = null;
		FileInputStream fis = null;
		try {
	    

	        //Decode image size
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    fis = new FileInputStream(f);
	    
	    BitmapFactory.decodeStream(fis, null, o);
	    fis.close();

	    int scale = 1;
	    if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	        scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / 
	           (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	    }

	    //Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    fis = new FileInputStream(f);
	    b = BitmapFactory.decodeStream(fis, null, o2);
	    fis.close();
	    ExifInterface exif = new ExifInterface(f.getPath());
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle);
        //matrix.setRotate(rotationAngle, (float) b.getWidth()/2 , (float) b.getHeight()/2);
        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
	    
	    
		}
	    catch (IOException e) {
	    	e.printStackTrace();
	    } finally { 
	    	if (fis!=null) {
	    		try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    		
	    }

	    return b;
	}
	
	public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
		 if (bitmap == null) {
		 return null;
		 } else {
		 byte[] b = null;
		 try {
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		 bitmap.compress(CompressFormat.PNG, 0, byteArrayOutputStream);
		 b = byteArrayOutputStream.toByteArray();
		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		 return b;
		 }
		 }

}
