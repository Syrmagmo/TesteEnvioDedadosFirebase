package com.example.testeenviodedadosfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    //Metodo responsavel por salvar os dados no DatabaseRealTime
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Metodo responsavel por salvar os dados no DatabaseRealTime
        //referencia.child("usuarios").child("001").setValue("Lincoln");

        Usuario Usuario = new Usuario();
        DatabaseReference usuarioDB = referencia.child("usuarioDB");

        Usuario.setIdade(22);
        Usuario.setNome("Lincoln dos");
        Usuario.setSobrenome("Santos");
        usuarioDB.child("100").setValue(Usuario);

        /* Obter a referência ao ImageView
        ImageView imageView = findViewById(R.id.imageView);

        // Obter o Drawable da imagem do diretório "drawable"
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.wublanceiro);

        // Definir o Drawable no ImageView
        imageView.setImageDrawable(drawable);*/

        /*
        Metodo responsavel por recuperar os dados do firebase
         */
        usuarioDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //aqui é onde recupera os valores

                Log.i("Firebase", snapshot.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.i("FIREBASE", "Erro relacionado ao banco de dados" + error);

            }
        });


    }
}