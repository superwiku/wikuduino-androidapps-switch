package com.superwiku.speechcommand;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.superwiku.speechcommand.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
	String address = null;
	String command = null;
	ArrayList<String> result;
	TextView lumn;
	private ProgressDialog progress;
	BluetoothAdapter myBluetooth = null;
	BluetoothSocket btSocket = null;
	private boolean isBtConnected = false;
	Button btnDisconnect;
	static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private TextView txvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent newint = getIntent();
		address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
		setContentView(R.layout.activity_main);

		txvResult = (TextView) findViewById(R.id.txvResult);
		btnDisconnect=(Button)findViewById(R.id.btn_disconnect);
		lumn=(TextView)findViewById(R.id.textView5);

		new ConnectBT().execute();
		btnDisconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				Disconnect();
			}
		});
	}

	public void getSpeechInput(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, 10);
		} else {
			Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 10:
				if (resultCode == RESULT_OK && data != null) {
					result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					command=result.get(0);
					txvResult.setText(result.get(0));
					if((command.equals("1 nyala"))||(command.equals("satu nyala"))){
						sendSignal("a");
					}else if((command.equals("1 mati"))||(command.equals("satu mati"))){
						sendSignal("b");
					}else if((command.equals("2 nyala"))||(command.equals("dua nyala"))){
						sendSignal("c");
					}else if((command.equals("2 mati"))||(command.equals("dua mati"))){
						sendSignal("d");
					}else if((command.equals("3 nyala"))||(command.equals("tiga nyala"))){
						sendSignal("e");
					}else if((command.equals("3 mati"))||(command.equals("tiga mati"))){
						sendSignal("f");
					}else if((command.equals("4 nyala"))||(command.equals("empat nyala"))){
						sendSignal("g");
					}else if((command.equals("4 mati"))||(command.equals("empat mati"))){
						sendSignal("h");
					}else if(command.equals("semua nyala")){
						sendSignal("i");
					}else if(command.equals("semua mati")){
						sendSignal("j");
					}
				}
		}
	}

	private void sendSignal ( String number ) {
		if ( btSocket != null ) {
			try {
				btSocket.getOutputStream().write(number.toString().getBytes());
			} catch (IOException e) {
				msg("Error");
			}
		}
	}

	private void Disconnect () {
		if ( btSocket!=null ) {
			try {
				btSocket.close();
			} catch(IOException e) {
				msg("Error");
			}
		}

		finish();
	}

	private void msg (String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	private class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean ConnectSuccess = true;

		@Override
		protected  void onPreExecute () {
			progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please Wait!!!");

		}

		@Override
		protected Void doInBackground (Void... devices) {
			try {
				if ( btSocket==null || !isBtConnected ) {
					myBluetooth = BluetoothAdapter.getDefaultAdapter();
					BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
					btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					btSocket.connect();
				}
			} catch (IOException e) {
				ConnectSuccess = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute (Void result) {
			super.onPostExecute(result);

			if (!ConnectSuccess) {
				msg("Connection Failed. Try again...");
				finish();
			} else {
				msg("Connected");
				isBtConnected = true;
			}

			progress.dismiss();
		}
	}
}
