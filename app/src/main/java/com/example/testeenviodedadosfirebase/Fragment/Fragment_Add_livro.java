package com.example.testeenviodedadosfirebase.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testeenviodedadosfirebase.R;
import com.example.testeenviodedadosfirebase.model.livroModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class Fragment_Add_livro extends Fragment {
    //Dados do livro \/
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    //Dados do livro /\


    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;

    private MaterialCardView Escolherfoto; // Foi usaro para execultar uma ação ao clicar no card
    private Uri ImageUri;//Foi usaro para armazena o dado da imagem
    private Bitmap bitmap;//Foi usaro para converter a imagem para bitmap
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage; //Metodo para salvar a img para o Storage do fire base

    private FirebaseDatabase BancoTempoReal; //Metodo para chamar o banco de dados

    private StorageReference mStorageRef; //Metodo para referenciar os dados para o Storage

    public Fragment_Add_livro() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_livro, container, false);

        edTituloDoLivro = rootView.findViewById(R.id.EditText_Titulo_Livro);
        edAutorDoLivro = rootView.findViewById(R.id.EditText_Autor_Livro);
        spGeneroDoLivro = rootView.findViewById(R.id.EditSP_Genero_Livro);
        edAnoDoLivro = rootView.findViewById(R.id.EditText_Ano_Livro);
        spEstadoDoLivro = rootView.findViewById(R.id.Spinner_Estado_Livro);
        edDescricaoDoLivro = rootView.findViewById(R.id.editTextTextMultiLine_Descricao);

        // instanciando o banco
        BancoTempoReal = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        // para obter o id do usuário atual
        UsuarioAtualID = firebaseAuth.getCurrentUser().getUid();

        // Torna o primeiro parâmetro do Spinner Nulo
        Spinner spinner = rootView.findViewById(R.id.EditSP_Genero_Livro);

        // Obter o array de opções de gênero do strings.xml
        CharSequence[] generos = getResources().getTextArray(R.array.generos_livros);

        // Criar um novo array com o primeiro item inválido
        CharSequence[] generosComInvalido = new CharSequence[generos.length + 1];
        generosComInvalido[0] = "Opções"; // Primeiro item inválido
        System.arraycopy(generos, 0, generosComInvalido, 1, generos.length);

        // Criar um ArrayAdapter com as opções de gênero, incluindo o primeiro item inválido
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                generosComInvalido
        );

        // Definir o layout para os itens do Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Definir o adapter no Spinner
        spinner.setAdapter(adapter);

        // Selecionar o primeiro item inválido
        spinner.setSelection(0);

        //\/ \/ \/  Metodo para tratar a foto antes de enviar para o banco de dados \/ \/ \/

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);

        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermicoesArmazenamento();//metodo responsavel por verificar as permissões
            }

            private void PermicoesArmazenamento() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        EscolherImagemGaleria();//metodo responsavel por abrir a galeria
                    }
                } else {
                    EscolherImagemGaleria();//metodo responsavel por abrir a galeria
                }
            }
        });
        // /\ /\ /\ Metodo para tratar a foto antes de enviar para o banco de dados /\ /\ /\

        return rootView;
    }

    private void EscolherImagemGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // metodo para lançar a ação
        launcher.launch(intent);
    }
    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                ImageUri = data.getData();

                                //Metodo que converte a imagem para Bitmap
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // metodo para definir a imagem para o ImageView
                            if (ImageUri != null) {
                                FotoLivroImageView.setImageBitmap(bitmap);
                            }
                        }
                    });

    // Aqui é onde será feito o Upload da foto para o Firebase Storage pela URL da imagem dentro do Firestore

    // Metodo de Upload da imagem
    private void UploadImage() {
        // verifica a imagemUri
        if (ImageUri != null) {
            final StorageReference myRef = mStorageRef.child("foto/" + ImageUri.getLastPathSegment());
            myRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // aqui precisa do dowloadUrl do storage em string
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                FotoUrl = uri.toString();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Aqui será upado as outras informações do livro!
    private void UploadDadosLivro() {
        String titulo = edTituloDoLivro.getText().toString().trim();
        String autor = edAutorDoLivro.getText().toString().trim();
        String genero = spGeneroDoLivro.getSelectedItem().toString();
        String estado = spEstadoDoLivro.getSelectedItem().toString();
        String ano = edAnoDoLivro.getText().toString().trim();
        String descricao = edDescricaoDoLivro.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) ||
                TextUtils.isEmpty(autor) ||
                TextUtils.isEmpty(genero) ||
                TextUtils.isEmpty(estado) ||
                TextUtils.isEmpty(ano) ||
                TextUtils.isEmpty(descricao)) {
            Toast.makeText(getContext(), "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference databaseReference = BancoTempoReal.getReference("PostLivro").push();

            livroModel livroModel = new livroModel(titulo, autor, ano, genero, estado,
                    descricao, "", "", FotoUrl, "", UsuarioAtualID);

            databaseReference.setValue(livroModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Envio com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Falha no envio!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}




/*
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testeenviodedadosfirebase.R;
import com.example.testeenviodedadosfirebase.model.livroModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class Fragment_Add_livro extends Fragment {
    //Dados do livro \/
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    //Dados do livro /\


    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;

    private MaterialCardView Escolherfoto; // Foi usaro para execultar uma ação ao clicar no card
    private Uri ImageUri;//Foi usaro para armazena o dado da imagem
    private Bitmap bitmap;//Foi usaro para converter a imagem para bitmap
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage; //Metodo para salvar a img para o Storage do fire base

    private FirebaseDatabase BancoTempoReal; //Metodo para chamar o banco de dados

    private StorageReference mStorageRef; //Metodo para referenciar os dados para o Storage
    public Fragment_Add_livro() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_livro, container, false);

        edTituloDoLivro = rootView.findViewById(R.id.EditText_Titulo_Livro);
        edAutorDoLivro = rootView.findViewById(R.id.EditText_Autor_Livro);
        spGeneroDoLivro = rootView.findViewById(R.id.EditSP_Genero_Livro);
        edAnoDoLivro = rootView.findViewById(R.id.EditText_Ano_Livro);
        spEstadoDoLivro = rootView.findViewById(R.id.Spinner_Estado_Livro);
        edDescricaoDoLivro = rootView.findViewById(R.id.editTextTextMultiLine_Descricao);



        // instancinado o banco
        BancoTempoReal = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();



        firebaseAuth = firebaseAuth.getInstance();
        //para obter o id do usuario atual
        UsuarioAtualID = firebaseAuth.getCurrentUser().getUid();





        // Torna o primeiro parametro do Spinner Nulo
        Spinner spinner = rootView.findViewById(R.id.EditSP_Genero_Livro);

        // Obter o array de opções de gênero do strings.xml
        CharSequence[] generos = getResources().getTextArray(R.array.generos_livros);

        // Criar um novo array com o primeiro item inválido
        CharSequence[] generosComInvalido = new CharSequence[generos.length + 1];
        generosComInvalido[0] = "Opções"; // Primeiro item inválido
        System.arraycopy(generos, 0, generosComInvalido, 1, generos.length);

        // Criar um ArrayAdapter com as opções de gênero, incluindo o primeiro item inválido
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                generosComInvalido
        );

        // Definir o layout para os itens do Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Definir o adapter no Spinner
        spinner.setAdapter(adapter);

        // Selecionar o primeiro item inválido
        spinner.setSelection(0);





        //\/ \/ \/  Metodo para tratar a foto antes de enviar para o banco de dados \/ \/ \/

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);

        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermicoesArmazenamento();//metodo responsavel por verificar as permissões

            }

            private void PermicoesArmazenamento() {
                if(Build.VERSION.SDK_INT >+ Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        EscolherImagemGaleria();//metodo responsavel por abrir a galeria
                    }
                }else {
                    EscolherImagemGaleria();//metodo responsavel por abrir a galeria
                }
            }
        });
        // /\ /\ /\ Metodo para tratar a foto antes de enviar para o banco de dados /\ /\ /\

        return rootView;
    }

    private void EscolherImagemGaleria(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // metodo para lançar a ação

        launcher.launch(intent);

    }

    ActivityResultLauncher<Intent> launcher
            =registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
            result -> {
                      if (result.getResultCode() == Activity.RESULT_OK) {
                          Intent data = result.getData();
                          if (data != null && data.getData() != null) {

                              ImageUri = data.getData();

                              //Metodo que converte a imagem para Bitmap
                              try {
                                  bitmap = MediaStore.Images.Media.getBitmap(
                                          getActivity().getContentResolver(),
                                          ImageUri
                                  );
                              }catch (IOException e ){
                                  e.printStackTrace();
                            }
                          }
                          // metodo para definir a imagem para o ImageView
                          if(ImageUri != null){
                              FotoLivroImageView.setImageBitmap(bitmap);
                          }
                      }
            }
    );

    // Aqui é onde sera feito o Upload da foto para o firebase Storage pela url da imagem dentro do fireStore

    // Metodo de Upload da imagem
    private void UploadImage(){

        // verifica a imagemUri
        if (ImageUri != null){
            final StorageReference myRef=mStorageRef.child("foto/" + ImageUri.getLastPathSegment());
            myRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // aqui precisa do dowloadUrl do store em string
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null){
                                FotoUrl = uri.toString();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    // Aqui sera upado as outras informações do livro!
    private void UploadDadosLivro(){

        String titulo = edTituloDoLivro.getText().toString().trim();
        String autor = edAutorDoLivro.getText().toString().trim();
        String genero = spGeneroDoLivro.getSelectedItem().toString();
        String estado = spEstadoDoLivro.getSelectedItem().toString();
        String ano = edAnoDoLivro.getText().toString().trim();
        String descricao = edDescricaoDoLivro.getText().toString().trim();

        //

        if (TextUtils.isEmpty(titulo) &&
                TextUtils.isEmpty(autor) &&
                TextUtils.isEmpty(genero) &&
                TextUtils.isEmpty(estado) &&
                TextUtils.isEmpty(ano) &&
                TextUtils.isEmpty(descricao)){
            Toast.makeText(getContext(), "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
        }else {

            /*
            Caso fosse utilizado o Firestore, porem estou utilizando o firebase Realtime
            DocumentReference myRef = BancoTempoReal.collection("LivroInformacoes").document();
             */
  /*          DatabaseReference databaseReference = BancoTempoReal.getReference("PostLivro").push();

            livroModel livroModel = new livroModel(titulo,
                    autor, ano, genero, estado,
                    descricao,
                    "","",FotoUrl,"", UsuarioAtualID);

            databaseReference.set(livroModel. SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task){

                    if (task.isSuccessful()){
                        if (task.isSuccessful()){

                            // we need to get doc id and set to model to store in firestore
                            docID = documentReference.getid();
                            livroModel.setDocId(docId);
                            // now this doc id will sent into firestore
                            documentReference.set(livroModel, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Envio com sucesso!", Toast.LENGTH_SHORT).show();

                                }
                                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            }

                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void OnFailureListener(@NonNull Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
/*
        }


    }

}*/
