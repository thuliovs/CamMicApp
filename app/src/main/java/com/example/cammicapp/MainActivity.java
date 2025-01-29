package com.example.cammicapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMARA_REQUEST = 100;
    private static final int AUDIO_REQUEST_CODE = 200;

    private ImageView imageView;
    private TextView txtEstado;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;

    private Button btnGravar, btnPararGravacao, btnReproduzirAudio, btnPararAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referências para os elementos da interface
        imageView = findViewById(R.id.imageView);
        txtEstado = findViewById(R.id.txtEstado);
        btnGravar = findViewById(R.id.btnGravar);
        btnPararGravacao = findViewById(R.id.btnPararGravacao);
        btnReproduzirAudio = findViewById(R.id.btnReproduzirAudio);
        btnPararAudio = findViewById(R.id.btnPararAudio);

        // Caminho do arquivo de áudio no diretório privado
        audioFilePath = getFilesDir().getAbsolutePath() + "/audio_gravado.3gp";

        // Botão para abrir a câmara
        findViewById(R.id.btnCamara).setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMARA_REQUEST);
            } else {
                abrirCamara();
            }
        });

        // Configurar gravação de áudio
        btnGravar.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
            } else {
                iniciarGravacao();
            }
        });

        btnPararGravacao.setOnClickListener(v -> pararGravacao());

        // Botões para reproduzir e parar áudio
        btnReproduzirAudio.setOnClickListener(v -> reproduzirAudio());
        btnPararAudio.setOnClickListener(v -> pararReproducaoAudio());
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMARA_REQUEST);
    }

    private void iniciarGravacao() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            txtEstado.setText("Gravando áudio...");
        } catch (IOException e) {
            txtEstado.setText("Erro ao gravar áudio.");
            e.printStackTrace();
        }
    }

    private void pararGravacao() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            txtEstado.setText("Gravação finalizada.");
        }
    }

    private void reproduzirAudio() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            txtEstado.setText("Reproduzindo áudio...");
        } catch (IOException e) {
            txtEstado.setText("Erro ao reproduzir áudio.");
            e.printStackTrace();
        }
    }

    private void pararReproducaoAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            txtEstado.setText("Reprodução parada.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMARA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            txtEstado.setText("Foto capturada.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMARA_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else if (requestCode == AUDIO_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarGravacao();
        } else {
            txtEstado.setText("Permissão negada.");
        }
    }
}

