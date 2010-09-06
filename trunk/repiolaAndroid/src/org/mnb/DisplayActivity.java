package org.mnb;

import gui.MobileCanvas;
import storage.Storage;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;

public class DisplayActivity extends Activity {
    MobileCanvas canvas = null;
	Display currentDisplay = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        currentDisplay = getWindowManager().getDefaultDisplay();
        
        getMobileCanvas().newImage(currentDisplay.getWidth(), currentDisplay.getHeight());
        final String program = Storage.getInstance(this.getApplicationContext()).load("code");
        new Thread() { public void run() {getMobileCanvas().setProgram(program);} }.run();
    }
    
    private MobileCanvas getMobileCanvas() {
    	if(canvas == null) {
    		canvas = (MobileCanvas) findViewById(R.id.canvas);
    	}
    	return canvas;
    }
}