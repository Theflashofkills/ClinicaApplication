// Generated by view binder compiler. Do not edit!
package com.thiagojunhonma.devhealthy.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.thiagojunhonma.devhealthy.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityCadastroPacienteBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnCadastrar;

  @NonNull
  public final TextInputEditText etCpf;

  @NonNull
  public final TextInputEditText etDataNascimento;

  @NonNull
  public final TextInputEditText etEndereco;

  @NonNull
  public final TextInputEditText etNome;

  @NonNull
  public final AutoCompleteTextView etSexo;

  @NonNull
  public final TextInputEditText etTelefone;

  @NonNull
  public final ScrollView formScroll;

  @NonNull
  public final Toolbar toolbarCadastro;

  private ActivityCadastroPacienteBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button btnCadastrar, @NonNull TextInputEditText etCpf,
      @NonNull TextInputEditText etDataNascimento, @NonNull TextInputEditText etEndereco,
      @NonNull TextInputEditText etNome, @NonNull AutoCompleteTextView etSexo,
      @NonNull TextInputEditText etTelefone, @NonNull ScrollView formScroll,
      @NonNull Toolbar toolbarCadastro) {
    this.rootView = rootView;
    this.btnCadastrar = btnCadastrar;
    this.etCpf = etCpf;
    this.etDataNascimento = etDataNascimento;
    this.etEndereco = etEndereco;
    this.etNome = etNome;
    this.etSexo = etSexo;
    this.etTelefone = etTelefone;
    this.formScroll = formScroll;
    this.toolbarCadastro = toolbarCadastro;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityCadastroPacienteBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityCadastroPacienteBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_cadastro_paciente, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityCadastroPacienteBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnCadastrar;
      Button btnCadastrar = ViewBindings.findChildViewById(rootView, id);
      if (btnCadastrar == null) {
        break missingId;
      }

      id = R.id.etCpf;
      TextInputEditText etCpf = ViewBindings.findChildViewById(rootView, id);
      if (etCpf == null) {
        break missingId;
      }

      id = R.id.etDataNascimento;
      TextInputEditText etDataNascimento = ViewBindings.findChildViewById(rootView, id);
      if (etDataNascimento == null) {
        break missingId;
      }

      id = R.id.etEndereco;
      TextInputEditText etEndereco = ViewBindings.findChildViewById(rootView, id);
      if (etEndereco == null) {
        break missingId;
      }

      id = R.id.etNome;
      TextInputEditText etNome = ViewBindings.findChildViewById(rootView, id);
      if (etNome == null) {
        break missingId;
      }

      id = R.id.etSexo;
      AutoCompleteTextView etSexo = ViewBindings.findChildViewById(rootView, id);
      if (etSexo == null) {
        break missingId;
      }

      id = R.id.etTelefone;
      TextInputEditText etTelefone = ViewBindings.findChildViewById(rootView, id);
      if (etTelefone == null) {
        break missingId;
      }

      id = R.id.formScroll;
      ScrollView formScroll = ViewBindings.findChildViewById(rootView, id);
      if (formScroll == null) {
        break missingId;
      }

      id = R.id.toolbarCadastro;
      Toolbar toolbarCadastro = ViewBindings.findChildViewById(rootView, id);
      if (toolbarCadastro == null) {
        break missingId;
      }

      return new ActivityCadastroPacienteBinding((ConstraintLayout) rootView, btnCadastrar, etCpf,
          etDataNascimento, etEndereco, etNome, etSexo, etTelefone, formScroll, toolbarCadastro);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
