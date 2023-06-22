package com.example.testeenviodedadosfirebase.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class Fragment_Add_livro extends Fragment {
    // Dados do livro
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    private MaterialCardView Escolherfoto;
    private Uri ImageUri;
    private Bitmap bitmap;
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage;
    private FirebaseDatabase BancoTempoReal;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;
    private ProgressDialog progressDialog;

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

        BancoTempoReal = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UsuarioAtualID = firebaseAuth.getCurrentUser().getUid();

        Spinner spinner = rootView.findViewById(R.id.EditSP_Genero_Livro);
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.generos_livros, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeneroDoLivro.setAdapter(generoAdapter);

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        return rootView;
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                pickImage();
            }
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getActivity(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            ImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                FotoLivroImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void EnviarDadosDoLivro() {
        String TituloDoLivro = edTituloDoLivro.getText().toString();
        String AutorDoLivro = edAutorDoLivro.getText().toString();
        String GeneroDoLivro = spGeneroDoLivro.getSelectedItem().toString();
        String AnoDoLivro = edAnoDoLivro.getText().toString();
        String EstadoDoLivro = spEstadoDoLivro.getSelectedItem().toString();
        String DescricaoDoLivro = edDescricaoDoLivro.getText().toString();

        if (TextUtils.isEmpty(TituloDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o título do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AutorDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o nome do autor do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AnoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o ano do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(DescricaoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira uma descrição do livro", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Enviando livro");
            progressDialog.setMessage("Aguarde enquanto enviamos o livro...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ArmazenarImagemNoFirebase();
        }
    }

    private void ArmazenarImagemNoFirebase() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        StorageReference filepath = mStorageRef.child("Imagens").child(edTituloDoLivro.getText().toString() + ".jpg");
        UploadTask uploadTask = filepath.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                FotoUrl = downloadUrl.toString();

                SalvarDadosDoLivroNoFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void SalvarDadosDoLivroNoFirebase() {
        DatabaseReference LivrosRef = BancoTempoReal.getReference().child("Livros");
        String LivroID = LivrosRef.push().getKey();

        Map<String, Object> livroMap = new HashMap<>();
        livroMap.put("livroID", LivroID);
        livroMap.put("titulo", edTituloDoLivro.getText().toString());
        livroMap.put("autor", edAutorDoLivro.getText().toString());
        livroMap.put("genero", spGeneroDoLivro.getSelectedItem().toString());
        livroMap.put("ano", edAnoDoLivro.getText().toString());
        livroMap.put("estado", spEstadoDoLivro.getSelectedItem().toString());
        livroMap.put("descricao", edDescricaoDoLivro.getText().toString());
        livroMap.put("foto", FotoUrl);
        livroMap.put("userID", UsuarioAtualID);

        LivrosRef.child(LivroID).updateChildren(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Livro enviado com sucesso", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String mensagem = task.getException().toString();
                    Toast.makeText(getActivity(), "Erro ao enviar livro: " + mensagem, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
}


//\/\/\/\/\/\ codigo a baixo esta quase funcionando
/*
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Fragment_Add_livro extends Fragment {
    //Dados do livro \/
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    //Dados do livro /\

    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;

    private String LivroId;
    private MaterialCardView Escolherfoto; // Foi usado para executar uma ação ao clicar no card
    private Uri ImageUri; // Foi usado para armazenar o dado da imagem
    private Bitmap bitmap; // Foi usado para converter a imagem para bitmap
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage; // Método para salvar a imagem para o Storage do Firebase

    private FirebaseDatabase BancoTempoReal; // Método para chamar o banco de dados

    private StorageReference mStorageRef; // Método para referenciar os dados para o Storage

    // Progress dialog para exibir o progresso do envio do livro
    private ProgressDialog progressDialog;

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
        Spinner spinner = rootView.findViewById(        R.id.EditSP_Genero_Livro);

        // Configurando o Adapter para o Spinner de Gênero do Livro
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.generos_livros, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeneroDoLivro.setAdapter(generoAdapter);

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);

        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        return rootView;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                pickImage();
            }
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getActivity(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            ImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                FotoLivroImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar os dados para o banco de dados
    private void EnviarDadosDoLivro() {
        String TituloDoLivro = edTituloDoLivro.getText().toString();
        String AutorDoLivro = edAutorDoLivro.getText().toString();
        String GeneroDoLivro = spGeneroDoLivro.getSelectedItem().toString();
        String AnoDoLivro = edAnoDoLivro.getText().toString();
        String EstadoDoLivro = spEstadoDoLivro.getSelectedItem().toString();
        String DescricaoDoLivro = edDescricaoDoLivro.getText().toString();

        if (TextUtils.isEmpty(TituloDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o título do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AutorDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o nome do autor do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AnoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o ano do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(DescricaoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira uma descrição do livro", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Enviando livro");
            progressDialog.setMessage("Aguarde enquanto enviamos o livro...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ArmazenarImagemNoFirebase();
        }
    }

    // Método para armazenar a imagem no Firebase Storage
    private void ArmazenarImagemNoFirebase() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        StorageReference filepath = mStorageRef.child("Imagens").child(edTituloDoLivro + ".jpg");
        UploadTask uploadTask = filepath.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        SalvarDadosNoBancoDeDados(downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Erro ao enviar a imagem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para salvar os dados do livro no banco de dados
    private void SalvarDadosNoBancoDeDados(String imagemUrl) {
        DatabaseReference livroRef = FirebaseDatabase.getInstance().getReference().child("Livros").push();

        Map<String, Object> livroMap = new HashMap<>();
        livroMap.put("titulo", TituloDoLivro);
        livroMap.put("autor", AutorDoLivro);
        livroMap.put("genero", GeneroDoLivro);
        livroMap.put("ano", AnoDoLivro);
        livroMap.put("estado", EstadoDoLivro);
        livroMap.put("descricao", DescricaoDoLivro);
        livroMap.put("imagem", imagemUrl);

        livroRef.setValue(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Livro enviado com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Erro ao enviar o livro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
*/
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^CODIGO A CIMA ESTA QUASE PRONTO

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
import java.util.TimeZone;


public class Fragment_Add_livro extends Fragment {
    //Dados do livro \/
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    //Dados do livro /\


    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;

    private String LivroId;
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

    // Adicione este método à classe Fragment_Add_livro

    private String getUsuarioAtualID() {
        return UsuarioAtualID;
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

                        //Precisa pegar o id do usuario nessa parte
                        String usuarioID = getUsuarioAtualID();
                        livroModel livroModel = new livroModel(titulo, autor, ano, genero, estado, descricao, "", "", FotoUrl, "", usuarioID);

                        Toast.makeText(getContext(), "Envio com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Falha no envio!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
*/