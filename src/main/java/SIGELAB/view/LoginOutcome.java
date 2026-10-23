package SIGELAB.view;

import SIGELAB.model.Usuario;
import SIGELAB.integration.facial.FacialVerificationResult;

public class LoginOutcome {
    private Usuario usuario;
    private FacialVerificationResult facialResult;
    private boolean rostroNoRegistrado;
    private boolean credencialesInvalidas;

    // Caso 1: Usuario sin rostro registrado
    public static LoginOutcome rostroNoRegistrado(Usuario usuario) {
        LoginOutcome outcome = new LoginOutcome();
        outcome.usuario = usuario;
        outcome.rostroNoRegistrado = true;
        return outcome;
    }

    // Caso 2: Credenciales incorrectas
    public static LoginOutcome sinCredenciales() {
        LoginOutcome outcome = new LoginOutcome();
        outcome.credencialesInvalidas = true;
        return outcome;
    }

    // Caso 3: Login exitoso con verificación facial
    public LoginOutcome(Usuario usuario, FacialVerificationResult facialResult) {
        this.usuario = usuario;
        this.facialResult = facialResult;
        this.rostroNoRegistrado = false;
        this.credencialesInvalidas = false;
    }

    public LoginOutcome() {
        this.credencialesInvalidas = false;
        this.rostroNoRegistrado = false;
    }

    // Getters y métodos de validación
    public boolean isRostroNoRegistrado() {
        return rostroNoRegistrado;
    }

    public boolean credencialesInvalidas() {
        return credencialesInvalidas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Usuario usuario() {
        return usuario;
    }

    public FacialVerificationResult facial() {
        return facialResult;
    }
}