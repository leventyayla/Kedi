package tez.levent.feyyaz.kedi.activities.details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import tez.levent.feyyaz.kedi.R;

public class EtkinlikQrReader extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView qrCodeReaderView;
    private Boolean flashState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000L);

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("QrData",text);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void flash(View v){
        if (!flashState) {
            qrCodeReaderView.setTorchEnabled(true);
            flashState = true;
        }else {
            qrCodeReaderView.setTorchEnabled(false);
            flashState = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
