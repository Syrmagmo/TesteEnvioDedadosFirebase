package com.example.testeenviodedadosfirebase.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.testeenviodedadosfirebase.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Add_livro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Add_livro extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Add_livro() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Add_livro.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Add_livro newInstance(String param1, String param2) {
        Fragment_Add_livro fragment = new Fragment_Add_livro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_livro, container, false);

        Spinner spinner = rootView.findViewById(R.id.EditText_Genero_Livro);

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

        return rootView;
    }


/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_livro, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.EditText_Genero_Livro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.generos_livros, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Seleciona o primeiro item da lista

        return rootView;
    }

*/

}