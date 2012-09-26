package com.portaura.zxing.encode;

import java.util.EnumMap;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.portaura.zxing.R;

public class QRPic extends Activity {
	EditText inputText;
	TextView showTextView;
	ImageView qrImageView, scalImageView;
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;
	private BarcodeFormat f;
	Bitmap bitmap;
	LinearLayout ll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrpic);

	}

	public void geneqr(View v) {
		inputText = (EditText) findViewById(R.id.inputId);
		String contentString = inputText.getText().toString();
		if (contentString.length() == 0 || null == contentString) {
			Toast.makeText(this, "未输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		qrImageView = (ImageView) findViewById(R.id.image_view);
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		int smallerDimension = width < height ? width : height;
		smallerDimension = smallerDimension * 13 / 10;
		// Toast.makeText(this, "维度" + smallerDimension,
		// Toast.LENGTH_LONG).show();
		showTextView = (TextView) findViewById(R.id.showcontents);
		InputMethodManager imm0 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm0.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
		int id = ((Button) v).getId();
		if (id == R.id.encode2DBtn) {
			f = BarcodeFormat.QR_CODE;
			bitmap = encodeAsBitmap(contentString, f, smallerDimension,
					smallerDimension);
		} else {
			f = BarcodeFormat.CODE_128;
			bitmap = encodeAsBitmap(contentString, f, smallerDimension,
					smallerDimension / 3);
		}

		if (bitmap == null) {
			showTextView.setText("编码失败。如果您在生成条形码，请遵循输入规则。");
			qrImageView.setImageBitmap(null);
			inputText.setText("");
			return;
		}
		qrImageView.setImageBitmap(bitmap);
		inputText.setText("");
		inputText.setHintTextColor(Color.GREEN);
		inputText.setHint("点击码图可放大，再次点击可缩回。");
		showTextView.setText(contentString);
		if (id == R.id.encode2DBtn) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getRequestedOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			qrImageView.setImageBitmap(bitmap);
		}
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			inputText.setInputType(InputType.TYPE_NULL);
		}
	}

	Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int w, int h) {
		String contentsToEncode = contents;
		Map<EncodeHintType, Object> hints = null;
		String encoding = "UTF-8";
		if (encoding != null) {
			hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contentsToEncode, format, w, h, hints);
			int width = result.getWidth();
			int height = result.getHeight();
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				int offset = y * width;
				for (int x = 0; x < width; x++) {
					pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (Exception e) {
			Log.e("adffeife", e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public void zoomup(View v) {
		ll = (LinearLayout) findViewById(R.id.linear);
		scalImageView = (ImageView) findViewById(R.id.scaleimg);
		ll.setVisibility(View.GONE);
		scalImageView.setVisibility(View.VISIBLE);
		scalImageView.setImageBitmap(bitmap);
	}

	public void zoomdown(View v) {
		ll.setVisibility(View.VISIBLE);
		scalImageView.setVisibility(View.GONE);
	}
}
