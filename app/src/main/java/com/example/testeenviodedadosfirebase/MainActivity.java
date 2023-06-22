package com.example.testeenviodedadosfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.testeenviodedadosfirebase.Fragment.Fragment_Add_livro;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.integrity.v;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class MainActivity extends AppCompatActivity {


    //Metodo responsavel por salvar os dados no DatabaseRealTime
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference referenciaIMG = FirebaseDatabase.getInstance().getReference("Image");
    private StorageReference referenciaStorage = FirebaseStorage.getInstance().getReference();

    private Button uploadBtn, showAllBtn;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;

    private EditText editTextNome;
    private EditText editTextIdade;
    private EditText editTextSobrenome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNome = findViewById(R.id.editTextNome);
        editTextIdade = findViewById(R.id.editTextIdade);
        editTextSobrenome = findViewById(R.id.editTextSobrenome);

        Usuario usuario = new Usuario();
        DatabaseReference usuarioDB = referencia.child("usuarioDB");

        Button salvarBtn = findViewById(R.id.salvar_btn);
        salvarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editTextNome.getText().toString();
                int idade = Integer.parseInt(editTextIdade.getText().toString());
                String sobrenome = editTextSobrenome.getText().toString();

                usuario.setIdade(idade);
                usuario.setNome(nome);
                usuario.setSobrenome(sobrenome);

                usuarioDB.child("100").setValue(usuario);

                Toast.makeText(MainActivity.this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        usuarioDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Firebase", snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("FIREBASE", "Erro relacionado ao banco de dados" + error);
            }
        });

        // metodo para fazer o UPLOAD da imagem
        uploadBtn = findViewById(R.id.upload_btn);
        showAllBtn = findViewById(R.id.showall_btn);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        //para setar uma imagem especifica na Imageview
        imageView.setImageResource(R.drawable.ic_addfoto);

        // Para deixar o Progresse bar visivel
        progressBar.setVisibility(View.INVISIBLE);

        //salvando no banco Storege
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                // Para salvar todos os tipos de imagens
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);
                }else{
                    Toast.makeText(MainActivity.this, "Selecione uma imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpar o cache da imagem escolhida
                imageUri = null;
                imageView.setImageDrawable(null);

                // Resto do código para exibir todos os itens
                imageView.setImageResource(R.drawable.ic_addfoto);
            }
        });

        Button button = findViewById(R.id.btnTeste); // Substitua "button" pelo ID do seu Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_Add_livro fragment = new Fragment_Add_livro();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    private void uploadToFirebase(Uri uri){
        StorageReference fileRef = referenciaStorage.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //faz o Download da URL da imagem
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Model model = new Model(uri.toString());
                        //o parametro PUSH serve para criar um ID aleatorio
                        String modelId = referenciaIMG.push().getKey();
                        referenciaIMG.child(modelId).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(MainActivity.this, "Envio com sucesso!", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.ic_addfoto);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                progressBar.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "O envio falhou!", Toast.LENGTH_SHORT).show();
                
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

}