package com.example.es;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraIntentActivity extends Activity {
	  private static final int REQUEST_CODE = 100;
	  //private Bitmap bitmap;
	  private ImageView imageView;
	  private Uri fileUri;
	  private String imgFileUriStr;
	  //private File imgFile;
	  public final static String  BUNDLE_KEY_IMG_FILE_URI_STR = "fileUriStr";
	  public final static String LANG="eng";
	  public static final String DATA_PATH = Environment
				.getExternalStorageDirectory().toString() + "/vd/ocr/";

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    copyOCRLanFile();
	    setContentView(R.layout.activity_camera_intent);
	    
		SharedPreferences pref = getSharedPreferences(CameraUtil.SHARE_PREF_IMG_URL, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		imgFileUriStr = pref.getString(CameraUtil.SHARE_PREF_IMG_KEY , "");
	    imageView = (ImageView) findViewById(R.id.result);
	    //if (savedInstanceState !=null &&savedInstanceState.containsKey(BUNDLE_KEY_IMG_FILE_URI_STR)) {
	    if (!imgFileUriStr.isEmpty()) {	
	    	//fileUri = Uri.fromFile(new File (savedInstanceState.getString(BUNDLE_KEY_IMG_FILE_URI_STR)));
	    	fileUri = Uri.fromFile(new File (imgFileUriStr));
	    	 Log.d(getClass().getName(), ".onCreate: path is " + fileUri.getPath());
	    	 Bitmap bitmap = CameraUtil.decodeFile(new File (fileUri.getPath()));  
		    	//Bitmap bitmap = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/EsCameraApp/IMG_20131016_124425.jpg");  
		        imageView.setImageBitmap(bitmap);
		        //imageView.setAdjustViewBounds(true);
		        
		        //OCR 
		        TessBaseAPI baseApi = new TessBaseAPI();
				baseApi.setDebug(true);
				baseApi.init(DATA_PATH, LANG);
				
				//PlanarYUVLuminanceSource source = this.getCameraManager().buildLuminanceSource(data, width, height);
				byte[] yuvData = CameraUtil.convertBitmapToByteArray(bitmap);
				PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(yuvData,bitmap.getWidth(),bitmap.getHeight(),0,0,bitmap.getWidth(),bitmap.getHeight(),false);
				
				
				//baseApi.setImage(bitmap);
				baseApi.setImage(source.renderCroppedGreyscaleBitmap());

				String recognizedText = baseApi.getUTF8Text();
				

				baseApi.end();
				Log.d("**eric ***", "ocr characters: " + recognizedText);
				
				Toast.makeText(this, recognizedText, Toast.LENGTH_LONG).show();
		  
		       
		        TextView tv = (TextView)findViewById(R.id.label);
		        tv.setText(fileUri.getPath());
	    }
	    
	   
	    
	    //Bitmap bitmap = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/EsCameraApp/IMG_20131016_124425.jpg"); 
	    //Bitmap bitmap = CameraUtil.decodeFile(new File("/storage/sdcard0/Pictures/EsCameraApp/IMG_20131016_124425.jpg"));
	    
	    //imageView.setImageBitmap(bitmap);
	    //bitmap.recycle();
	  //upload button
	    Button upload_btn = (Button) this.findViewById(R.id.upload_photo_btn);
		upload_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// To open up a gallery browser
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 1);
			}
		});

	    Button btnTakePhoto = (Button)this.findViewById(R.id.take_photo_btn);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
          
           @Override
           public void onClick(View v) {
              Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              // create a file to save the image
              //imgFile = CameraUtil.getOutputMediaFile(CameraUtil.MEDIA_TYPE_IMAGE);
              //fileUri = Uri.fromFile(imgFile);
              //Uri localFileUri = Uri.fromFile(imgFile);
              fileUri = CameraUtil.getOutputMediaFileUri(CameraUtil.MEDIA_TYPE_IMAGE);
              SharedPreferences pref = getSharedPreferences(CameraUtil.SHARE_PREF_IMG_URL, Context.MODE_PRIVATE);
  			SharedPreferences.Editor editor = pref.edit();
  			editor.putString(CameraUtil.SHARE_PREF_IMG_KEY, fileUri.getPath());
  			editor.commit();
              
              Log.d(getClass().getName(), ".onClick: path is " + fileUri.getPath());
              // set the image file name
              cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
             
              cameraIntent.putExtra("crop", "true");
              cameraIntent.putExtra("outputX", 150);
              cameraIntent.putExtra("outputY", 150);
              cameraIntent.putExtra("aspectX", 1);
              cameraIntent.putExtra("aspectY", 1);
              cameraIntent.putExtra("scale", true);
              
              //cameraIntent.putExtra("outputFormat",
                      //Bitmap.CompressFormat.JPEG.toString());
              
              cameraIntent.putExtra("outputFormat",
                      Bitmap.CompressFormat.PNG.toString());
              // start the image capture Intent
              CameraIntentActivity.this.startActivityForResult(cameraIntent, REQUEST_CODE);
           }
       });
	  }
      /*
	  public void onClick(View View) {
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		fileUri = CameraUtil.getOutputMediaFileUri(CameraUtil.MEDIA_TYPE_IMAGE);
			
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
	   
	    //Intent intent = new Intent();
	    //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    //intent.setType("image/*");
	    //intent.setAction(Intent.ACTION_GET_CONTENT);
	    //intent.addCategory(Intent.CATEGORY_OPENABLE);
	    startActivityForResult(intent, REQUEST_CODE);
	  }/*/

	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    InputStream stream = null;
	    //ImageView imageView = (ImageView) findViewById(R.id.result);
	    TextView tv = (TextView)findViewById(R.id.label);
	    SharedPreferences pref = getSharedPreferences(CameraUtil.SHARE_PREF_IMG_URL, Context.MODE_PRIVATE);
	    Uri fileUri = Uri.fromFile(new File (pref.getString(CameraUtil.SHARE_PREF_IMG_KEY, "")));
	    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
	     // try {
	    	  
	    	  //Toast.makeText(this, "Image saved to:\n" +
	    		//	  fileUri.getPath(), Toast.LENGTH_LONG).show();
	    	  
	    	  Log.d(getClass().getName(), ".onActivityResult" + fileUri.getPath());
	    	  
  
	        // recyle unused bitmaps
	    	/*  
	        if (bitmap != null) {
	          bitmap.recycle();
	        }*/
	        //stream = getContentResolver().openInputStream(data.getData());
	        //FileInputStream fis = new FileInputStream(fileUri.getPath());
	        //FileInputStream fis = new FileInputStream(Uri.fromFile(imgFile).getPath());

	        //bitmap = BitmapFactory.decodeStream(fis);
	        //stream = getContentResolver().openInputStream(fileUri);
	        //stream = getContentResolver().openInputStream(fileUri);
	        //bitmap = BitmapFactory.decodeStream(stream);
	    	File f = new File(fileUri.getPath());
	    	if (f.exists()) {
	    		Log.d("eric*** ", " file " +  f.getPath() + "exist");
	    	}
	    	
	    	finish();
	    	startActivity(getIntent());
	    	
	    	
	    	//Bitmap bitmap = CameraUtil.decodeFile(new File (fileUri.getPath()));  
	    	//Log.d("eric***", "111 file path is " + fileUri.getPath());
	    	//Bitmap bitmap = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/EsCameraApp/IMG_20131016_124425.jpg");  
	        //imageView.setImageBitmap(bitmap);
	        //imageView.invalidate();
	        //imageView.invalidateDrawable(imageView.getDrawable());
	        //tv.setText(fileUri.getPath());
	        //tv.invalidate();
	        //imageView.setImageURI(fileUri);
	      /*
	      } catch (FileNotFoundException e) {
	        e.printStackTrace();
	      } catch (IOException e) {
	    	  e.printStackTrace();
	      } finally {
	        if (stream != null)
	          try {
	            stream.close();
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
	      }*/
	  }
	  
	  /*
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	   // TODO Auto-generated method stub
	   super.onSaveInstanceState(outState);
	   if (fileUri!=null) {
		   outState.putString(BUNDLE_KEY_IMG_FILE_URI_STR, fileUri.getPath());
		   Log.d(getClass().getName(), ".onSaveInstanceState:  path is " + fileUri.getPath());
	   }
	  } */
	  
	  private void copyOCRLanFile() {
		  String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

			for (String path : paths) {
				File dir = new File(path);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						Log.d(getClass().getName(), "ERROR: Creation of directory " + path + " on sdcard failed");
						return;
					} else {
						Log.d(getClass().getName(), "Created directory " + path + " on sdcard");
					}
				}

			}

			// lang.traineddata file with the app (in assets folder)
			// You can get them at:
			// http://code.google.com/p/tesseract-ocr/downloads/list
			// This area needs work and optimization
			if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata")).exists()) {
				try {

					AssetManager assetManager = getAssets();
					InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
					//GZIPInputStream gin = new GZIPInputStream(in);
					OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + LANG + ".traineddata");

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					//while ((lenf = gin.read(buff)) > 0) {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					//gin.close();
					out.close();

					Log.d(getClass().getName(), "Copied " + LANG + " traineddata");
				} catch (IOException e) {
					Log.d(getClass().getName(), "Was unable to copy " + LANG + " traineddata " + e.toString());
				}
			}
	  }

}
