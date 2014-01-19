package org.ris.trackvideo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {
	VideoView videoView;
	FileWriter outputFileWriter = null;
	int currentFileCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Video file name (inside /sdcard/)");

		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        File f = new File("/sdcard/" + input.getText().toString() /*Movies/anim_card_flip.mp4*/);
		        videoView.setVideoURI(Uri.fromFile(f));
		        
		    }
		});

		builder.show();
		
		videoView = (VideoView)findViewById(R.id.videoView1);
    	
    	while(true) {
    		File file = new File("/sdcard/TrackVideoLog" + currentFileCounter + ".csv");
        	if(file.exists()) {
        		currentFileCounter++;
        		continue;
        	}
        	break;
    	}

		try {
			outputFileWriter = new FileWriter ("/sdcard/TrackVideoLog" + currentFileCounter + ".csv");
		} catch (IOException e) {
			e.printStackTrace();
			outputFileWriter = null;
			Toast.makeText(getApplicationContext(), "Error in handling files, will display, but cannot save Log!!!", Toast.LENGTH_LONG).show();
		}
		
		videoView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(videoView.isPlaying()) {
					if(outputFileWriter != null) {
						try {
							outputFileWriter.write(videoView.getCurrentPosition() + "," + event.getX() + "," + event.getY() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getApplicationContext(), videoView.getCurrentPosition() + "," + event.getX() + "," + event.getY(), Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
    	
    	videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    	    @Override
    	    public void onCompletion(MediaPlayer vmp) {
    	    	((Button)findViewById(R.id.button1)).setText("Start/Resume");        
    	    }
    	});  
    	//videoView.setMediaController(new MediaController(this));
		
		 findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {        	
	            public void onClick(View v) { // start button
	            	videoView.requestFocus();
	            	if(videoView.isPlaying()) {
	            		Toast.makeText(getApplicationContext(), "Pausing video...", Toast.LENGTH_SHORT).show();
	            		videoView.pause();
	            		((Button)findViewById(R.id.button1)).setText("Start/Resume");
	            	} else {
	            		Toast.makeText(getApplicationContext(), "Starting video...", Toast.LENGTH_SHORT).show();
	            		videoView.start();
	            		((Button)findViewById(R.id.button1)).setText("Pause");
	            	}
	            }
	        });
		 
		 findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {        	
	            public void onClick(View v) { // reset button
	            	if(outputFileWriter != null) {
	            		try {
	            			outputFileWriter.close();
	            			outputFileWriter = null;
						} catch (IOException e) {
							outputFileWriter = null;
							Toast.makeText(getApplicationContext(), "Warning, file could not be closed!", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
	            	}
	            	
            		try {
            			outputFileWriter = new FileWriter("/sdcard/TrackVideoLog" + ++currentFileCounter + ".csv");
					} catch (IOException e) {
						outputFileWriter = null;
						Toast.makeText(getApplicationContext(), "Error in handling files, will display, but cannot save Log!!!", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
	            }
	        });
		 
		 findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {        	
	            public void onClick(View v) { // save button
	            	if(outputFileWriter != null) {
	            		try {
	            			outputFileWriter.flush();
	            			outputFileWriter.close();
	            			outputFileWriter = null;
	            			Toast.makeText(getApplicationContext(), "Output has been saved to /sdcard/TrackVideoLog" + currentFileCounter + ".csv", Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							outputFileWriter = null;
							Toast.makeText(getApplicationContext(), "Warning, file could not be closed!", Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
	            	} else {
	            		Toast.makeText(getApplicationContext(), "Something bad happened, I don't know!!", Toast.LENGTH_LONG).show();
	            	}
	            	
	            	try {
	            		outputFileWriter = new FileWriter("/sdcard/TrackVideoLog" + ++currentFileCounter + ".csv");
					} catch (IOException e) {
						outputFileWriter = null;
						Toast.makeText(getApplicationContext(), "Error in handling files, will display, but cannot save Log!!!", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
